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
      logger.error('mariadb connection error:', err.text);
    });
  return conn;
};

export const ping = async (conn: any): Promise<boolean> => {
  const res = await conn
    .ping()
    .then(() => {
      return 'ok';
    })
    .catch((err: any) => logger.error('Unable to ping', err));
  return res === 'ok';
};

export const listDatabases = async (conn: any): Promise<Array<any>> => {
  const res = await conn.query({ sql: 'SHOW DATABASES;', rowsAsArray: true });
  const microserviceDbs: Array<any> = [];
  for (const db of res) {
    if (db[0].endsWith('_service')) {
      await conn
        .query({
          sql: `SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '${db}';`,
          rowsAsArray: true,
          bigIntAsNumber: true,
        })
        .then((count: any) => {
          const finalCount = count[0][0] - 1; // un-tally `flyway_schema_history` table
          microserviceDbs.push({
            name: db[0],
            tablesCount: finalCount >= 0 ? finalCount : 0,
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
          .then(() => logger.info(`Table dropped: ${table.name}`))
          .catch((err: any) => logger.error('Unable to drop table:', err));
      }
    })
    .then(() => conn.query('SET FOREIGN_KEY_CHECKS = 1;'));
  return true;
};

export const seedTables = async (conn: any, dir: string): Promise<boolean> => {
  await removeTables(conn);
  await createTables(conn, dir);
  const seedFiles = await listSeedFiles(dir);
  for (const seed of seedFiles) {
    const res = (await readInputFile(seed.file)).split(/\r\n(?=\d+?,)/);
    console.log(res);
  }
  return true;
};

const createTables = async (conn: any, dir: string): Promise<boolean> => {
  const schema = await readInputFile(dir + '/_create.txt');
  await conn
    .query(schema)
    .then(() => logger.info(`🎉 tables created`))
    .catch(() => logger.error('tables not created'));
  return true;
};

const readInputFile = async (seedFile: string): Promise<any> => {
  return promises
    .readFile(seedFile, 'utf8')
    .then((data) => data.toString())
    .catch((err: any) =>
      logger.error('something wrong with the input file:', err.message)
    );
};

// const seedCollection = async <any>(
//   seedFile: string,
//   name: string
// ): Promise<any> => {
//   return readInputFile(seedFile)
//     .then(() => logger.info(`🌱 Seeding ${name} completed`))
//     .catch((err: any) => logger.error(`Unable to seed ${name}`, err));
// };

const listSeedFiles = async (dir: string): Promise<Array<any>> => {
  return new Promise((resolve, reject) => {
    glob(dir + '/*.csv', { ignore: 'nodir' }, function (err, files) {
      if (err) {
        reject(err);
      } else {
        const seedFiles: Array<any> = [];
        for (const file of files) {
          seedFiles.push({
            table_name: path.basename(file, '.csv'),
            file,
          });
        }
        resolve(seedFiles);
      }
    });
  });
};
