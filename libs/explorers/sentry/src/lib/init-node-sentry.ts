import * as Sentry from '@sentry/node';
import { hasRequiredSentryConfig, SENTRY_DATA_COLLECTION, SentryConfig } from './sentry-options';

/**
 * Must run before the server imports `express` or `http`, so that Sentry can instrument them and
 * attach each request to the events it captures.
 */
export function initNodeSentry(config: SentryConfig): void {
  if (!hasRequiredSentryConfig(config)) return;

  Sentry.init({
    dsn: config.dsn,
    environment: config.environment,
    release: config.release || undefined,
    dataCollection: SENTRY_DATA_COLLECTION,
    // The servers ship as a single webpack bundle with no node_modules, so the module hooks this
    // injection loads from disk at runtime are absent and would fail with a warning on every start.
    enableRuntimeChannelInjection: false,
  });
}
