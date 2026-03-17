import * as Sentry from '@sentry/angular';

export interface SentryConfig {
  dsn: string;
  hostEnvironmentMap: Record<string, string>;
  release?: string;
}

export function getSentryEnvironment(
  hostEnvironmentMap: Record<string, string>,
  hostname = typeof window !== 'undefined' ? window.location.hostname : undefined,
): string {
  if (!hostname) return 'server';
  if (hostname === 'localhost' || hostname === '127.0.0.1') return 'localhost';

  return hostEnvironmentMap[hostname] ?? hostname;
}

export function initSentry(config: SentryConfig): void {
  if (typeof window === 'undefined') return;

  Sentry.init({
    dsn: config.dsn,
    environment: getSentryEnvironment(config.hostEnvironmentMap),
    release: config.release || undefined,
    sendDefaultPii: false,
  });
}
