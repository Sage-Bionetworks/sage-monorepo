export const APP_VERSION_PATH = '/app-version';
export const DATA_VERSION_PATH = '/data-version';
export const APP_UPDATE_POLL_INTERVAL_MS = 10 * 60 * 1000;
export const APP_UPDATE_MIN_CHECK_INTERVAL_MS = 60 * 1000;
export const APP_UPDATE_CONFIRM_DELAY_MS = 5 * 60 * 1000;
/**
 * How long a version request may take before it counts as failed. Without it, a request the
 * network never answers would keep the check in progress, which blocks every later check.
 */
export const VERSION_CHECK_TIMEOUT_MS = 10 * 1000;
/**
 * A data release switches the data version in one step, so this only has to outlast the APIs'
 * cache invalidation, which polls every minute and clears again on the following poll.
 */
export const DATA_UPDATE_CONFIRM_DELAY_MS = 2 * 60 * 1000;
export const CHUNK_LOAD_FAILED_METRIC = 'app.chunk_load_failed';
export const STALE_APP_METRIC = 'app.stale_app_detected';
export const STALE_DATA_METRIC = 'app.stale_data_detected';
export const TRANSIENT_APP_VERSION_MISMATCH_METRIC = 'app.transient_app_version_mismatch';
export const TRANSIENT_DATA_VERSION_MISMATCH_METRIC = 'app.transient_data_version_mismatch';
export const VERSION_CHECK_FAILED_METRIC = 'app.version_check_failed';
export const APP_VERSION_ENDPOINT_MISCONFIGURED_WARNING = 'App version endpoint is misconfigured';
export const DATA_VERSION_ENDPOINT_MISCONFIGURED_WARNING = 'Data version endpoint is misconfigured';

export const CHUNK_LOAD_ERROR_MESSAGE =
  'Part of this page could not be loaded. Please check your connection and reload the page.';

export const getAppUpdateMessage = (siteName: string): string =>
  `A new version of ${siteName} is available. Please reload the page to continue.`;

export const getDataUpdateMessage = (siteName: string): string =>
  `New data is available for ${siteName}. Please reload the page to continue.`;
