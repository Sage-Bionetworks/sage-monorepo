import * as mariadb from 'mariadb';
import { config } from './config';
import { glob } from 'glob';
import * as path from 'path';
import { promises } from 'fs';
import { logger } from './logger';

export const connect = async (database = '') => {
  const conn = await mariadb
    .createConnection({
      host: config.mariadb.host,
      user: config.mariadb.user,
      password: config.mariadb.pass,
      port: config.mariadb.port,
      database,
    })
    .catch((err: any) => {
      logger.error('mariadb connection error:', err.message);
    });
  return conn;
};

export const ping = async (conn: any): Promise<boolean> => {
  const res = await conn
    .ping()
    .then(() => {
      return 'ok';
    })
    .catch((err: any) => logger.error('unable to ping', err.message));
  return res === 'ok';
};

export const listDatabases = async (conn: any): Promise<Array<any>> => {
  const res = await conn.query({ sql: 'SHOW DATABASES;', rowsAsArray: true });
  const microserviceDbs: Array<any> = [];
  for (const db of res) {
    if (db[0].endsWith('_service')) {
      await conn
        .query({
          sql: `SELECT COUNT(*) FROM information_schema.tables \
          WHERE table_schema = '${db}' AND table_name NOT IN ('flyway_schema_history');`,
          rowsAsArray: true,
          bigIntAsNumber: true,
        })
        .then((count: any) => {
          microserviceDbs.push({
            name: db[0],
            tablesCount: count[0][0],
          });
        });
    }
  }
  return microserviceDbs;
};

export const listTables = async (conn: any): Promise<Array<any>> => {
  const res = await conn.query({ sql: 'SHOW TABLES;', rowsAsArray: true });
  const tables: Array<any> = [];
  for (const table of res) {
    if (table[0] !== 'flyway_schema_history') {
      await conn
        .query({ sql: `SELECT COUNT(*) FROM ${table};`, rowsAsArray: true })
        .then((count: any) => {
          tables.push({
            name: table[0],
            rowsCount: count[0][0],
          });
        });
    }
  }
  return tables;
};

export const removeTables = async (conn: any): Promise<boolean> => {
  const tables = await listTables(conn);
  await conn
    .query('SET FOREIGN_KEY_CHECKS = 0;')
    .then(() => {
      for (const table of tables) {
        conn
          .query(`DROP TABLE IF EXISTS ${table.name}`)
          .then(() => logger.info(`table dropped: ${table.name}`))
          .catch((err: any) =>
            logger.error('unable to drop table:', err.message)
          );
      }
    })
    .then(() => conn.query('SET FOREIGN_KEY_CHECKS = 1;'));
  return true;
};

export const seedTables = async (conn: any, dir: string): Promise<boolean> => {
  await removeTables(conn);
  await createTables(conn, dir).then(() => logger.info(`🎉 tables created`));
  const seedFiles = await listSeedFiles(dir);
  for (const seed of seedFiles) {
    const records: Array<any> = [];
    const res = await readInputFile(seed.file, 'json');
    for (const obj of res) {
      records.push(Object.values(obj));
    }
    await seedTable(conn, seed.table_name, records);
  }
  return true;
};

// Helper: list files in directory
const listSeedFiles = async (dir: string): Promise<Array<any>> => {
  return new Promise((resolve, reject) => {
    glob(dir + '/*.json', { ignore: 'nodir' }, function (err, files) {
      if (err) {
        reject(err);
      } else {
        const seedFiles: Array<any> = [];
        for (const file of files) {
          seedFiles.push({
            table_name: path.basename(file, '.json').replace(/^\d+-/, ''),
            file,
          });
        }
        resolve(seedFiles);
      }
    });
  });
};

// Helper: read in file
const readInputFile = async (seedFile: string, type = 'text'): Promise<any> => {
  return promises
    .readFile(seedFile, 'utf8')
    .then((data) => (type === 'json' ? JSON.parse(data) : data.toString()))
    .catch((err: any) =>
      logger.error('something wrong with the input file:', err.message)
    );
};

// Helper: create tables from input file, using '--' as delimiter
const createTables = async (conn: any, dir: string): Promise<boolean> => {
  const schemas = (await readInputFile(dir + '/_create.txt')).split('--');
  for (const schema of schemas) {
    await conn.query(schema).catch(() => logger.error('table not created'));
  }
  return true;
};

// Helper: insert values into one table
const seedTable = async (
  conn: any,
  table: string,
  records: Array<any>
): Promise<boolean> => {
  const subQuery = await createInsertSql(conn, table);
  await conn
    .batch(
      `INSERT INTO \`${table}\` (${subQuery.columns}) VALUES (${subQuery.values})`,
      records
    )
    .then((res: any) =>
      logger.info(`🌱 table seeded: ${table} (${res.affectedRows} records)`)
    )
    .catch((err: any) => logger.error(`unable to seed ${table}`, err.message));
  return true;
};

// Helper: create colnames and placeholders for insert sql
const createInsertSql = async (conn: any, table: string) => {
  let columns = '';
  let values = '';
  await conn
    .query(
      `SELECT column_name FROM information_schema.columns \
      WHERE table_name = '${table}' AND column_name NOT IN ('created_at', 'updated_at');`
    )
    .then((table: any) => {
      columns = `${table[0].column_name}`;
      for (let i = 1; i < table.length; i++) {
        columns = columns.concat(`,${table[i].column_name}`);
      }
      values = `${'?,'.repeat(table.length - 1)}?`;
    });
  return { columns, values };
};
