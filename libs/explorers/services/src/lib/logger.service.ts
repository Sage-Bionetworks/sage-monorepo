import { Injectable, isDevMode } from '@angular/core';
import { ErrorLogContext, Logger, LoggerFactory } from '@sagebionetworks/web-shared/angular/logger';
import * as Sentry from '@sentry/angular';

/**
 * Creates source-bound loggers that report to Sentry. There is no way to log without a source, so
 * every event says where it came from.
 */
@Injectable({ providedIn: 'root' })
export class LoggerService implements LoggerFactory {
  /**
   * `source` is the name of the class, or of the exported function for functional guards and
   * interceptors, that does the logging. Pass it as a string literal: production builds minify
   * class names, so it can't be derived at runtime.
   */
  forSource(source: string): SourceLogger {
    return new SourceLogger(source);
  }
}

/**
 * A logger bound to one source. Sentry events are titled `<source>: <message>`, grouped by source
 * and message, and tagged with the source.
 */
export class SourceLogger implements Logger {
  constructor(readonly source: string) {}

  /**
   * Record context for later Sentry events as a breadcrumb in the source's category. Never creates
   * a Sentry issue by itself, and writes to the console in dev mode only.
   */
  log(message: string, data?: Record<string, unknown>) {
    if (isDevMode()) {
      console.log('[LOG]', this.title(message), ...definedValues(data));
    }
    Sentry.addBreadcrumb({ category: this.source, message, level: 'info', data });
  }

  /**
   * Log a warning and send it to Sentry as a warning-level event, with `data` attached as extra
   * context. Use for unexpected conditions the caller recovers from but that should still be
   * investigated.
   */
  warn(message: string, data?: Record<string, unknown>) {
    console.warn('[WARN]', this.title(message), ...definedValues(data));
    Sentry.captureMessage(this.title(message), this.captureContext('warning', message, data));
  }

  /**
   * Log an error and send it to Sentry as an error-level event, with `data` attached as extra
   * context. A caught `error` is attached as the event's cause, so its own type, text, and stack
   * trace appear in the event without deciding its grouping.
   */
  error(message: string, { error, data }: ErrorLogContext) {
    console.error('[ERROR]', this.title(message), ...definedValues(error, data));

    const captureContext = this.captureContext('error', message, data);
    if (error !== undefined) {
      Sentry.captureException(new Error(this.title(message), { cause: error }), captureContext);
    } else {
      Sentry.captureMessage(this.title(message), captureContext);
    }
  }

  private title(message: string): string {
    return `${this.source}: ${message}`;
  }

  private captureContext(
    level: 'warning' | 'error',
    message: string,
    data: Record<string, unknown> | undefined,
  ) {
    return {
      level,
      fingerprint: [this.source, message],
      tags: { source: this.source },
      extra: data,
    };
  }
}

function definedValues(...values: unknown[]): unknown[] {
  return values.filter((value) => value !== undefined);
}
