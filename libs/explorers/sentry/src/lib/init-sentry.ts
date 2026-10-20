import * as Sentry from '@sentry/angular';

const SENSITIVE_KEY_DENYLIST = ['forwarded', '-ip', 'remote-', 'via', '-user'];

/**
 * Sentry collects user info, cookies, and HTTP bodies when `dataCollection` is unset, so every
 * category is set explicitly to keep personal data out of events.
 */
export const SENTRY_DATA_COLLECTION: NonNullable<Sentry.BrowserOptions['dataCollection']> = {
  userInfo: false,
  cookies: false,
  httpHeaders: { deny: SENSITIVE_KEY_DENYLIST },
  httpBodies: [],
  urlQueryParams: { deny: SENSITIVE_KEY_DENYLIST },
  graphQL: { document: false, variables: false },
  genAI: { inputs: false, outputs: false },
  databaseQueryData: false,
  queues: false,
};

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
    dataCollection: SENTRY_DATA_COLLECTION,
  });
}
