import * as Sentry from '@sentry/angular';

export interface SentryConfig {
  dsn: string;
  environment: string;
  release?: string;
}

export function initSentry(config: SentryConfig): void {
  if (typeof window === 'undefined') return;
  if (!config.dsn) {
    console.warn('[Sentry] SENTRY_DSN is empty; skipping init.');
    return;
  }
  if (!config.environment) {
    console.warn('[Sentry] SENTRY_DSN is set but SENTRY_ENVIRONMENT is empty; skipping init.');
    return;
  }

  Sentry.init({
    dsn: config.dsn,
    environment: config.environment,
    release: config.release || undefined,
    sendDefaultPii: false,
  });
}
