import * as mariadb from 'mariadb';
import { config } from './config';
// import { glob } from 'glob';
// import * as path from 'path';
// import { promises } from 'fs';
// import {
//   ChallengeModel,
//   ChallengeOrganizerModel,
//   ChallengePlatformModel,
//   ChallengeReadmeModel,
//   ChallengeSponsorModel,
//   OrganizationModel,
//   OrgMembershipModel,
//   UserModel,
// } from './models';
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

const listSeedFiles = async (directory: string): Promise<SeedFiles> => {
  return new Promise((resolve, reject) => {
    glob(directory + '/*.json', { ignore: 'nodir' }, function (err, files) {
      if (err) {
        reject(err);
      } else {
        // TODO consider throwing an error if an unexpected json file is found
        // in the directory specified, e.g. when a key is not in the interface
        // SeedFiles
        const seedFiles: SeedFiles = {};
        files.forEach((file) => {
          const key = path.basename(file, '.json');
          seedFiles[key] = file;
        });
        resolve(seedFiles);
      }
    });
  });
};
