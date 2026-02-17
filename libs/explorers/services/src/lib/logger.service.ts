import { Injectable, isDevMode } from '@angular/core';
import * as Sentry from '@sentry/angular';

@Injectable({ providedIn: 'root' })
export class LoggerService {
  log(message: string, ...args: any[]) {
    if (isDevMode()) {
      console.log('[LOG]', message, ...args);
    }
    Sentry.addBreadcrumb({
      message,
      level: 'info',
      data: args.length ? { args } : undefined,
    });
  }

  warn(message: string, ...args: any[]) {
    console.warn('[WARN]', message, ...args);
    Sentry.addBreadcrumb({
      message,
      level: 'warning',
      data: args.length ? { args } : undefined,
    });
  }

  error(message: string, ...args: any[]) {
    console.error('[ERROR]', message, ...args);
    Sentry.addBreadcrumb({
      message,
      level: 'error',
      data: args.length ? { args } : undefined,
    });
  }

  trackError(err: any) {
    Sentry.captureException(err);
  }
}
