import util from 'util';
import winston from 'winston';

/* eslint-disable no-unused-vars */
export enum Level {
  Debug = 'debug',
  Verbose = 'verbose',
  Info = 'info',
  Warn = 'warn',
  Error = 'error',
}
/* eslint-enable no-unused-vars */

const colors = {
  debug: 'white',
  verbose: 'white',
  info: 'green',
  warn: 'yellow',
  error: 'red',
};

const transports = [new winston.transports.Console()];

const combineMessageAndSplat = () => {
  return {
    transform: (info: any) => {
      // combine message and args if any
      // https://github.com/winstonjs/winston/issues/1427#issuecomment-811184784
      info.message = util.format(
        info.message,
        ...(info[Symbol.for('splat')] || [])
      );
      return info;
    },
  };
};

const format = winston.format.combine(
  winston.format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss' }),
  winston.format.colorize({ all: true }),
  combineMessageAndSplat(),
  winston.format.printf(
    (info) => `${info['timestamp']} ${info.level}: ${info.message}`
  )
);

class Logger {
  private logger!: winston.Logger;

  constructor() {
    this.logger = winston.createLogger({
      level: Level.Info,
      transports: transports,
      format: format,
    });
    winston.addColors(colors);
  }

  public setLevel(level: Level): void {
    this.logger.level = level;
  }

  public debug(message: string, ...meta: any[]) {
    this.logger.debug(message, ...meta);
  }

  public verbose(message: string, ...meta: any[]) {
    this.logger.verbose(message, ...meta);
  }

  public info(message: string, ...meta: any[]) {
    this.logger.info(message, ...meta);
  }

  public warn(message: string, ...meta: any[]) {
    this.logger.warn(message, ...meta);
  }

  public error(message: string, ...meta: any[]) {
    this.logger.error(message, ...meta);
  }
}

export const logger = new Logger();
