import { Injectable, isDevMode } from '@angular/core';
import {
  definedValues,
  ErrorLogContext,
  Logger,
  LoggerFactory,
  sourceTitle,
} from '@sagebionetworks/web-shared/angular/logger';
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
      console.log('[LOG]', sourceTitle(this.source, message), ...definedValues(data));
    }
    Sentry.addBreadcrumb({ category: this.source, message, level: 'info', data });
  }

  /**
   * Log a warning and send it to Sentry as a warning-level event, with `data` attached as extra
   * context and a caught `error`, if any, attached the way `error` attaches one. Use for
   * unexpected conditions the caller recovers from but that should still be investigated.
   */
  warn(message: string, data?: Record<string, unknown>, error?: unknown) {
    console.warn('[WARN]', sourceTitle(this.source, message), ...definedValues(error, data));
    this.capture('warning', message, data, error);
  }

  /**
   * Log an error and send it to Sentry as an error-level event, with `data` attached as extra
   * context. A caught `Error` is attached as the event's cause, so its own type, text, and stack
   * trace appear in the event without deciding its grouping. Sentry links only `Error` causes, so
   * any other caught value, such as an `HttpErrorResponse`, is attached to the extra context as
   * `cause` instead.
   */
  error(message: string, { error, data }: ErrorLogContext) {
    console.error('[ERROR]', sourceTitle(this.source, message), ...definedValues(error, data));
    this.capture('error', message, data, error);
  }

  private capture(
    level: 'warning' | 'error',
    message: string,
    data: Record<string, unknown> | undefined,
    error: unknown,
  ) {
    const title = sourceTitle(this.source, message);
    if (error === undefined) {
      Sentry.captureMessage(title, this.captureContext(level, message, data));
    } else if (error instanceof Error) {
      Sentry.captureException(
        new Error(title, { cause: error }),
        this.captureContext(level, message, data),
      );
    } else {
      Sentry.captureException(
        new Error(title),
        this.captureContext(level, message, { ...data, cause: error }),
      );
    }
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
