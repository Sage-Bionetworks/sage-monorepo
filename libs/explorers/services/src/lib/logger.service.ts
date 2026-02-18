import { Injectable, isDevMode } from '@angular/core';
import * as Sentry from '@sentry/angular';

@Injectable({ providedIn: 'root' })
export class LoggerService {
  log(message: string, data?: Record<string, unknown>) {
    if (isDevMode()) {
      console.log('[LOG]', message, data);
    }
    Sentry.addBreadcrumb({
      message,
      level: 'info',
      data,
    });
  }

  warn(message: string, data?: Record<string, unknown>) {
    console.warn('[WARN]', message, data);
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
    console.error('[ERROR]', message, error);

    if (error) {
      Sentry.captureException(error);
    } else {
      Sentry.captureMessage(message, 'error');
    }
  }
}
