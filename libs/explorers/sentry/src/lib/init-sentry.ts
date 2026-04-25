import * as Sentry from '@sentry/angular';

export interface SentryConfig {
  dsn: string;
  environment: string;
  release?: string;
}

export function initSentry(config: SentryConfig): void {
  if (typeof window === 'undefined') return;
  if (!config.dsn) return;

  Sentry.init({
    dsn: config.dsn,
    environment: config.environment || undefined,
    release: config.release || undefined,
    sendDefaultPii: false,
  });
}
