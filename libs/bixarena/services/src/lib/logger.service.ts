import { inject, Injectable, isDevMode } from '@angular/core';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { ErrorLogContext } from '@sagebionetworks/web-shared/angular/logger';

type LogLevel = 'debug' | 'info' | 'warn' | 'error';

const LEVEL_ORDER: Record<LogLevel, number> = { debug: 0, info: 1, warn: 2, error: 3 };

/**
 * Creates source-bound console loggers. There is no way to log without a source, so every line
 * says where it came from.
 */
@Injectable({ providedIn: 'root' })
export class LoggerService {
  private readonly configService = inject(ConfigService);

  /**
   * `source` is the name of the class, or of the exported function, that does the logging. Pass it
   * as a string literal: production builds minify class names, so it can't be derived at runtime.
   */
  forSource(source: string): SourceLogger {
    return new SourceLogger(source, (level) => this.allows(level));
  }

  private allows(level: LogLevel): boolean {
    const configured = this.configService.config?.logging?.level ?? 'info';
    return LEVEL_ORDER[level] >= LEVEL_ORDER[configured];
  }
}

/** A console logger bound to one source; each line starts with `<source>: <message>`. */
export class SourceLogger {
  constructor(
    readonly source: string,
    private readonly allows: (level: LogLevel) => boolean,
  ) {}

  // isDevMode() is a hard floor: debug never outputs in prod builds even if YAML is misconfigured.
  debug(message: string, data?: unknown): void {
    if (!isDevMode() || !this.allows('debug')) return;
    console.debug(this.title(message), ...definedValues(data));
  }

  info(message: string, data?: unknown): void {
    if (!this.allows('info')) return;
    console.info(this.title(message), ...definedValues(data));
  }

  warn(message: string, data?: unknown): void {
    if (!this.allows('warn')) return;
    console.warn(this.title(message), ...definedValues(data));
  }

  error(message: string, { error, data }: ErrorLogContext): void {
    if (!this.allows('error')) return;
    console.error(this.title(message), ...definedValues(error, data));
  }

  private title(message: string): string {
    return `${this.source}: ${message}`;
  }
}

function definedValues(...values: unknown[]): unknown[] {
  return values.filter((value) => value !== undefined);
}
