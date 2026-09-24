import { Injectable, isDevMode } from '@angular/core';
import { Logger } from '@sagebionetworks/web-shared/angular/logger';
import * as Sentry from '@sentry/angular';

@Injectable({ providedIn: 'root' })
export class LoggerService implements Logger {
  log(message: string, data?: Record<string, unknown>) {
    if (isDevMode()) {
      if (data) {
        console.log('[LOG]', message, data);
      } else {
        console.log('[LOG]', message);
      }
    }
    Sentry.addBreadcrumb({
      message,
      level: 'info',
      data,
    });
  }

  /**
   * Log a warning and send it to Sentry as a warning-level event, with `data`
   * attached as extra context. Use for unexpected conditions the caller
   * recovers from but that should still be investigated.
   */
  warn(message: string, data?: Record<string, unknown>) {
    if (data) {
      console.warn('[WARN]', message, data);
    } else {
      console.warn('[WARN]', message);
    }
    Sentry.captureMessage(message, { level: 'warning', extra: data });
  }

  /**
   * Log an error message and send it to Sentry. If an error object is provided,
   * it will be captured as an exception. Otherwise, the message is captured
   * as a Sentry event.
   */
  error(message: string, error?: unknown) {
    if (error) {
      console.error('[ERROR]', message, error);
    } else {
      console.error('[ERROR]', message);
    }

    if (error) {
      Sentry.captureException(error);
    } else {
      Sentry.captureMessage(message, 'error');
    }
  }
}
