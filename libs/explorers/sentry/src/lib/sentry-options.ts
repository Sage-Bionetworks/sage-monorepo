import type { Options } from '@sentry/core';

/**
 * PII that Sentry must not collect: HTTP headers and URL query params whose names contain any of
 * these fragments, such as client IP addresses and usernames. The list comes from Sentry's
 * migration guide:
 * https://docs.sentry.io/platforms/javascript/guides/angular/migration/v10-to-v11/#data-collection
 */
const SENSITIVE_KEY_DENYLIST = ['forwarded', '-ip', 'remote-', 'via', '-user'];

/**
 * Sentry collects user info, cookies, and HTTP bodies when `dataCollection` is unset, so every
 * category is set explicitly to keep personal data out of events.
 */
export const SENTRY_DATA_COLLECTION: NonNullable<Options['dataCollection']> = {
  userInfo: false,
  cookies: false,
  httpHeaders: {
    request: { deny: SENSITIVE_KEY_DENYLIST },
    response: { deny: SENSITIVE_KEY_DENYLIST },
  },
  httpBodies: [],
  urlQueryParams: { deny: SENSITIVE_KEY_DENYLIST },
  genAI: { inputs: false, outputs: false },
  databaseQueryData: false,
  queues: false,
  graphQL: { document: false, variables: false },
};

export interface SentryConfig {
  dsn: string;
  environment: string;
  release?: string;
}

export function hasRequiredSentryConfig(config: SentryConfig): boolean {
  if (!config.dsn) {
    console.warn('[Sentry] SENTRY_DSN is empty; skipping init.');
    return false;
  }
  if (!config.environment) {
    console.warn('[Sentry] SENTRY_DSN is set but SENTRY_ENVIRONMENT is empty; skipping init.');
    return false;
  }
  return true;
}
