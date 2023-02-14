import { Command, Option } from 'commander';
import {
  connect,
  ping,
  listDatabases,
  listTables,
  removeTables,
  seedTables,
} from './database';
import { config } from './config';
import * as Pkg from '../package.json';
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
      .command('ping')
      .description('ping the mariadb instance')
      .action(() => this.ping())
      .hook('preAction', () => this.setConfig(this.program.opts()));

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
      .command('drop-tables')
      .description('remove all tables from a database')
      .argument('<database>')
      .action((database: string) => this.removeTables(database))
      .hook('preAction', () => this.setConfig(this.program.opts()));

    this.program
      .command('seed-database')
      .description(
        'empty and seed a database with the JSON files from the directory specified'
      )
      .argument('<database>')
      .argument('<directory>')
      .action((database: string, dir: string) => this.seed(database, dir))
      .hook('preAction', () => this.setConfig(this.program.opts()));

    this.program
      .option('-d, --debug', 'output extra debugging')
      .addOption(
        new Option(
          '--host <host>',
          'IP address or DNS of the database server'
        ).default(config.mariadb.host, 'value defined in config file')
      )
      .addOption(
        new Option('--port <port>', 'database server port number').default(
          config.mariadb.port,
          'value defined in config file'
        )
      )
      .addOption(
        new Option('--user <user>', 'user to access database').default(
          config.mariadb.user,
          'value defined in config file'
        )
      )
      .addOption(
        new Option('--password <password>', 'user password').default(
          config.mariadb.pass,
          'value defined in config file'
        )
      );
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

  private async removeTables(database: string): Promise<void> {
    try {
      this.conn = await connect(database);
      const res = await removeTables(this.conn);
      return this.gracefulShutdown('', () => {
        process.exit(res ? 0 : -1);
      });
    } catch (err: any) {
      logger.error('database not found');
      return this.gracefulShutdown('', () => {
        process.exit(-1);
      });
    }
  }

  private async seed(database: string, dir: string): Promise<void> {
    try {
      this.conn = await connect(database);
      const success = await seedTables(this.conn, dir);
      return this.gracefulShutdown('', () => {
        process.exit(success ? 0 : -1);
      });
    } catch (err) {
      logger.error('Unable to seed the database:', err);
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
    config.mariadb.host = options.host;
    config.mariadb.user = options.user;
    config.mariadb.pass = options.password;
    config.mariadb.port = options.port;
    if (options.debug) {
      logger.setLevel(Level.Debug);
    }
  }

  public async run(): Promise<void> {
    await this.program.parseAsync(process.argv);
  }
}
