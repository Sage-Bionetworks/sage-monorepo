import * as Sentry from '@sentry/angular';
import { envelopeContainsItemType, type EnvelopeItemType } from '@sentry/core';
import { isChunkLoadError } from './chunk-load-error';
import {
  hasRequiredSentryConfig,
  SENTRY_DATA_COLLECTION,
  SentryConfig,
  tracingOptions,
} from './sentry-options';

type BrowserOfflineTransportOptions = Partial<
  Parameters<ReturnType<typeof Sentry.makeBrowserOfflineTransport>>[0]
>;

export const OFFLINE_QUEUE_MAX_ENVELOPES = 30;

/**
 * Only metrics are queued: they record what failed, and each 5-second batch is one small envelope.
 * An outage sends an error event for every failed request, which would fill the queue before the
 * metrics that explain the outage.
 */
const OFFLINE_QUEUE_ITEM_TYPES: EnvelopeItemType[] = ['trace_metric'];

/**
 * Saves sends that fail on the network in IndexedDB and retries them, including on the next page
 * load, so a metric recorded while the connection is down still reaches Sentry.
 */
export const OFFLINE_TRANSPORT_OPTIONS: BrowserOfflineTransportOptions = {
  maxQueueSize: OFFLINE_QUEUE_MAX_ENVELOPES,
  flushAtStartup: true,
  shouldStore: (envelope) => envelopeContainsItemType(envelope, OFFLINE_QUEUE_ITEM_TYPES),
};

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
    transport: Sentry.makeBrowserOfflineTransport(Sentry.makeFetchTransport),
    transportOptions: OFFLINE_TRANSPORT_OPTIONS,
    // Adds trace headers to same-origin API requests, so the API's Sentry events share the
    // browser's trace ID. It records page loads, navigations, and requests as spans only when
    // `tracesSampleRate` is set.
    integrations: [Sentry.browserTracingIntegration()],
    ...tracingOptions(config),
    beforeSend: async (event, hint) => {
      if (isStaleClient && isChunkLoadError(hint.originalException) && (await isStaleClient())) {
        return null;
      }
      return event;
    },
  });
}
