import { Injectable, isDevMode } from '@angular/core';
import * as Sentry from '@sentry/angular';

@Injectable({ providedIn: 'root' })
export class LoggerService {
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

  warn(message: string, data?: Record<string, unknown>) {
    if (data) {
      console.warn('[WARN]', message, data);
    } else {
      console.warn('[WARN]', message);
    }
    Sentry.addBreadcrumb({
      message,
      level: 'warning',
      data,
    });
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
