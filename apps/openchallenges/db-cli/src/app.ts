import { Command } from 'commander';
import {
  connect,
  listDatabases,
  listTables,
  ping,
} from './database';
import { config } from './config';
import * as Pkg from '../package.json';
import { Mongoose } from 'mongoose';
import { logger, Level } from './logger';

export class App {
  private program: Command;
  private conn!: any;
  private padding = 35;

  constructor() {
    this.program = new Command();

    this.program
      .name('openchallenges-db-cli')
      .usage('[global options] command')
      .version(Pkg.version, '-v, --version', 'output the current version')
      .description(Pkg.description);

    this.program
      .command('list-dbs')
      .description('list available databases')
      .action(() => this.listDatabases())
      .hook('preAction', () => this.setConfig(this.program.opts()));

    this.program
      .command('list-tables')
      .description('list tables in a database')
      .argument('<database>')
      .action((database: string) => this.listTables(database))
      .hook('preAction', () => this.setConfig(this.program.opts()));

    this.program
      .command('seed')
      .description(
        'empty and seed the db with the JSON files from the directory specified'
      )
      .argument('<directory>')
      .action((directory: string) => this.seed(directory))
      .hook('preAction', () => this.setConfig(this.program.opts()));

    this.program
      .command('ping')
      .description('ping the mariadb instance')
      .action(() => this.ping())
      .hook('preAction', () => this.setConfig(this.program.opts()));
  }

  public async gracefulShutdown(msg: string, callback: any): Promise<void> {
    logger.debug('Gracefully shutdown');
    if (this.conn) {
      await this.conn.end();
    }
    callback();
    return Promise.resolve();
  }

  private async listDatabases(): Promise<void> {
    try {
      this.conn = await connect();
      const databases = await listDatabases(this.conn);
      console.log('database'.padEnd(this.padding, ' ') + '\ttables_count');
      console.log('-'.repeat(this.padding) + '\t------------');
      for (const database of databases) {
        console.log(
          database.name.padEnd(this.padding, ' ') + '\t' + database.tablesCount
        );
      }
      return this.gracefulShutdown('', () => {
        process.exit(databases ? 0 : -1);
      });
    } catch (err: any) {
      logger.error('unable to list databases:', err.text);
      return this.gracefulShutdown('', () => {
        process.exit(-1);
      });
    }
  }

  private async listTables(database: string): Promise<void> {
    try {
      this.conn = await connect(database);
      const tables = await listTables(this.conn);
      console.log('table'.padEnd(this.padding, ' ') + '\trecords_count');
      console.log('-'.repeat(this.padding) + '\t------------');
      for (const table of tables) {
        console.log(
          table.name.padEnd(this.padding, ' ') + '\t' + table.rowsCount
        );
      }
      return this.gracefulShutdown('', () => {
        process.exit(tables ? 0 : -1);
      });
    } catch (err: any) {
      logger.error('database not found');
      return this.gracefulShutdown('', () => {
        process.exit(-1);
      });
    }
  }

  private async removeCollections(): Promise<void> {
    try {
      this.mongoose = await connectToDatabase();
      const success = await removeCollections();
      return this.gracefulShutdown('', () => {
        process.exit(success ? 0 : -1);
      });
    } catch (err) {
      logger.error('Unable to remove the collections', err);
      return this.gracefulShutdown('', () => {
        process.exit(-1);
      });
    }
  }

  private async ping(): Promise<void> {
    try {
      this.conn = await connect();
      const pong = await ping(this.conn);
      logger.info(pong ? 'pong' : 'No pong received');
      return this.gracefulShutdown('', () => {
        process.exit(pong ? 0 : -1);
      });
    } catch (err) {
      logger.error('unable to ping the database', err);
      return this.gracefulShutdown('', () => {
        process.exit(-1);
      });
    }
  }

  private setConfig(options: any): void {
    config.mongo.uri = options.uri;
    config.mongo.options.user = options.username;
    config.mongo.options.pass = options.password;
    if (options.debug) {
      logger.setLevel(Level.Debug);
    }
  }

  public async run(): Promise<void> {
    await this.program.parseAsync(process.argv);
  }
}
