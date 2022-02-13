import { connect, connection, Model, Mongoose } from 'mongoose';
import { config } from './config';
import { glob } from 'glob';
import * as path from 'path';
import { promises } from 'fs';
import {
  ChallengeModel,
  ChallengeOrganizerModel,
  ChallengePlatformModel,
  ChallengeReadmeModel,
  ChallengeSponsorModel,
  OrganizationModel,
  OrgMembershipModel,
  UserModel,
} from './models';
import { logger } from './logger';

interface SeedFiles {
  [key: string]: string;
}

export const connectToDatabase = async(): Promise<Mongoose> => {
  const mongooseConnection = connect(config.mongo.uri, config.mongo.options);
  connection.on('connected', function() {
    logger.verbose(`Mongoose connected to ${config.mongo.uri}`);
  });
  connection.on('error', (err: any) => {
    logger.error(`Mongoose connection error: ${err}`);
  });
  connection.on('disconnected', function() {
    logger.verbose('Mongoose disconnected');
  });
  return mongooseConnection;
};

export const removeCollections = async(): Promise<boolean> => {
  const db: any = connection.db;
  const collections = await db.listCollections().toArray();
  const promises: Promise<any>[] = collections.map((collection: any) => {
    return db
      .dropCollection(collection.name)
      .then(() => logger.info(`Collection ${collection.name} removed`))
      .catch((err: any) => logger.error('Unable to remove collection', err));
  });
  await Promise.all(promises);
  return true;
};

export const pingDatabase = async(): Promise<boolean> => {
  const res = await connection.db.admin().ping();
  return !!res && res['ok'] === 1;
};

export const seedDatabase = async(directory: string): Promise<boolean> => {
  await removeCollections();
  const seedFiles = await listSeedFiles(directory);
  // The order of the seeds matters
  const seeds = [
    { name: 'users', model: UserModel },
    { name: 'organizations', model: OrganizationModel },
    { name: 'orgMemberships', model: OrgMembershipModel },
    { name: 'challengePlatforms', model: ChallengePlatformModel },
    { name: 'challenges', model: ChallengeModel },
    { name: 'challengeReadmes', model: ChallengeReadmeModel },
    { name: 'challengeOrganizers', model: ChallengeOrganizerModel },
    { name: 'challengeSponsors', model: ChallengeSponsorModel },
  ] as any[];
  for (const seed of seeds) {
    await seedCollection(seedFiles[seed.name], seed.name, seed.model);
  }
  return true;
};

const readSeedFile = async(seedFile: string): Promise<any> => {
  return promises
    .readFile(seedFile, 'utf8')
    .then((data) => JSON.parse(data))
    .catch((err: any) => logger.error('Unable to read seed file', err));
};

const seedCollection = async <T>(
  seedFile: string,
  name: string,
  model: Model<T>
): Promise<any> => {
  return readSeedFile(seedFile)
    .then((data) => model.create(data[name]))
    .then(() => logger.info(`🌱 Seeding ${name} completed`))
    .catch((err: any) => logger.error(`Unable to seed ${name}`, err));
};

const listSeedFiles = async(directory: string): Promise<SeedFiles> => {
  return new Promise((resolve, reject) => {
    glob(directory + '/*.json', { ignore: 'nodir' }, function(err, files) {
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
