import * as Sentry from '@sentry/angular';
import { hasRequiredSentryConfig, SENTRY_DATA_COLLECTION, SentryConfig } from './sentry-options';

export function initSentry(config: SentryConfig): void {
  if (typeof window === 'undefined') return;
  if (!hasRequiredSentryConfig(config)) return;

  Sentry.init({
    dsn: config.dsn,
    environment: config.environment,
    release: config.release || undefined,
    dataCollection: SENTRY_DATA_COLLECTION,
  });
}
