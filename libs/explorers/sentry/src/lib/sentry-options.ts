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
  /**
   * The performance data sample rate, from 0.0 to 1.0 (0.1 = 10% of traces), for transactions and
   * spans. Unset turns performance tracing off; trace IDs still propagate, so events from one
   * request link up. On a backend, any configured rate only turns tracing on; see
   * `backendTracingOptions`.
   */
  tracesSampleRate?: number | null;
}

/**
 * Sentry options that turn on performance tracing, only when a sample rate is configured. An unset
 * rate leaves the option out rather than passing `undefined`, which keeps the SDK from creating
 * spans at all; it then only propagates trace IDs.
 */
export function tracingOptions({
  tracesSampleRate,
}: SentryConfig): Pick<Options, 'tracesSampleRate'> {
  return tracesSampleRate == null ? {} : { tracesSampleRate };
}

/**
 * Backend tracing options. When tracing is on, a backend traces a request only if the browser's
 * trace for it was sampled, so traces stay whole and requests with no browser trace, such as
 * server-side rendering, load tests, and direct API calls, use no span quota. The browser's rate
 * decides how much is traced.
 */
export function backendTracingOptions({
  tracesSampleRate,
}: SentryConfig): Pick<Options, 'tracesSampler'> {
  return tracesSampleRate == null
    ? {}
    : { tracesSampler: ({ inheritOrSampleWith }) => inheritOrSampleWith(0) };
}

/**
 * Reads a `SENTRY_TRACES_SAMPLE_RATE` environment variable. Unset or empty leaves tracing off; a
 * value that isn't a sample rate from 0.0 to 1.0 also leaves it off, with a warning, rather than
 * failing the server's start.
 */
export function parseTracesSampleRate(value: string | undefined): number | undefined {
  if (value === undefined || value.trim() === '') return undefined;
  const rate = Number(value);
  if (Number.isNaN(rate) || rate < 0 || rate > 1) {
    console.warn(
      `[Sentry] SENTRY_TRACES_SAMPLE_RATE must be a sample rate from 0.0 to 1.0, got "${value}"; performance tracing is off.`,
    );
    return undefined;
  }
  return rate;
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
