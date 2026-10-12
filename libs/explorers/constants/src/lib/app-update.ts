export const APP_UPDATE_POLL_INTERVAL_MS = 10 * 60 * 1000;
export const APP_UPDATE_MIN_CHECK_INTERVAL_MS = 60 * 1000;
export const STALE_CLIENT_METRIC = 'app.stale_client_detected';
export const VERSION_CHECK_FAILED_METRIC = 'app.version_check_failed';
export const VERSION_ENDPOINT_MISCONFIGURED_WARNING = 'App version endpoint is misconfigured';

export const getAppUpdateMessage = (siteName: string): string =>
  `A new version of ${siteName} is available. Please reload the page to continue.`;
