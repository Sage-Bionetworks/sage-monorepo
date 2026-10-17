import * as Sentry from '@sentry/angular';
import { isChunkLoadError } from './chunk-load-error';

export interface SentryConfig {
  dsn: string;
  environment: string;
  release?: string;
  /**
   * Resolves whether this tab runs code from an earlier deploy. Chunk-load errors from such a tab
   * are expected, since the new server no longer has the earlier deploy's chunks, so they are
   * dropped instead of reported.
   */
  isStaleClient?: () => Promise<boolean>;
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

  const { isStaleClient } = config;
  Sentry.init({
    dsn: config.dsn,
    environment: config.environment,
    release: config.release || undefined,
    sendDefaultPii: false,
    beforeSend: async (event, hint) => {
      if (isStaleClient && isChunkLoadError(hint.originalException) && (await isStaleClient())) {
        return null;
      }
      return event;
    },
  });
}
