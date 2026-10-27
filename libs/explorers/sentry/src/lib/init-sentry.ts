import * as Sentry from '@sentry/angular';
import { isChunkLoadError } from './chunk-load-error';
import { hasRequiredSentryConfig, SENTRY_DATA_COLLECTION, SentryConfig } from './sentry-options';

export interface BrowserSentryConfig extends SentryConfig {
  /**
   * Resolves whether this tab runs code from an earlier deploy. Chunk-load errors from such a tab
   * are expected, since the new server no longer has the earlier deploy's chunks, so they are
   * dropped instead of reported.
   */
  isStaleClient?: () => Promise<boolean>;
}

export function initSentry(config: BrowserSentryConfig): void {
  if (typeof window === 'undefined') return;
  if (!hasRequiredSentryConfig(config)) return;

  const { isStaleClient } = config;
  Sentry.init({
    dsn: config.dsn,
    environment: config.environment,
    release: config.release || undefined,
    dataCollection: SENTRY_DATA_COLLECTION,
    // With `tracesSampleRate` unset, this sends no performance data. It only adds trace headers to
    // same-origin API requests, so the API's Sentry events share the browser's trace ID.
    integrations: [Sentry.browserTracingIntegration()],
    beforeSend: async (event, hint) => {
      if (isStaleClient && isChunkLoadError(hint.originalException) && (await isStaleClient())) {
        return null;
      }
      return event;
    },
  });
}
