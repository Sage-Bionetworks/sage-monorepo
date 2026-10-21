export const APP_VERSION_PATH = '/app-version';
export const DATA_VERSION_PATH = '/data-version';

export const APP_UPDATE_POLL_INTERVAL_MS = 10 * 60 * 1000;
export const APP_UPDATE_MIN_CHECK_INTERVAL_MS = 60 * 1000;
/**
 * A rolling deploy sends requests to both old and new app tasks for a few minutes, so this has to
 * outlast that cutover: a mismatch still there afterwards reflects the version every task serves.
 */
export const APP_UPDATE_CONFIRM_DELAY_MS = 5 * 60 * 1000;
/**
 * How many times a confirmation check that could not compare a version is tried before the
 * background checks take over, so a tab whose version endpoint keeps failing stops sending a
 * request every minute.
 */
export const APP_UPDATE_CONFIRMATION_RETRY_LIMIT = 3;
/**
 * A data release switches the data version in one step, so this only has to outlast the APIs'
 * cache invalidation, which polls every minute and clears again on the following poll. That second
 * clear can land just over two minutes after the switch, so a third minute is added as margin.
 */
export const DATA_UPDATE_CONFIRM_DELAY_MS = 3 * 60 * 1000;
export const VERSION_CHECK_TIMEOUT_MS = 10 * 1000;

/** When this tab last loaded a new page to replace a stale chunk, in `sessionStorage`. */
export const STALE_CHUNK_PAGE_LOADED_AT_KEY = 'staleChunkPageLoadedAt';
/** Set just before that page load, so the loaded app can count it. */
export const STALE_CHUNK_PAGE_LOAD_PENDING_KEY = 'staleChunkPageLoadPending';
/**
 * How long after loading a new page for a stale chunk the tab shows the update overlay instead of
 * loading another. During a rolling deploy, the new page can still be served by an old task and
 * request a chunk a new task does not have, which would otherwise load page after page.
 */
export const STALE_CHUNK_PAGE_LOAD_GUARD_MS = 60 * 1000;

export const CHUNK_LOAD_FAILURES_METRIC = 'app_update.chunk_load.failures';
export const STALE_CHUNK_PAGE_LOADS_METRIC = 'app_update.stale_chunk.page_loads';
export const VERSION_CHECK_FAILURES_METRIC = 'app_update.version_check.failures';
export const VERSION_CONFIRMED_MISMATCHES_METRIC = 'app_update.version.confirmed_mismatches';
export const VERSION_UNCONFIRMED_MISMATCHES_METRIC = 'app_update.version.unconfirmed_mismatches';
/** The attribute name for an HTTP response status code, aligned with OpenTelemetry's naming. */
export const HTTP_RESPONSE_STATUS_CODE_ATTRIBUTE = 'http.response.status_code';

export const APP_VERSION_ENDPOINT_MISCONFIGURED_WARNING = 'App version endpoint is misconfigured';
export const DATA_VERSION_ENDPOINT_MISCONFIGURED_WARNING = 'Data version endpoint is misconfigured';

export const CHUNK_LOAD_ERROR_MESSAGE =
  'Part of this page could not be loaded. Please check your connection and reload the page.';

export const getAppUpdateMessage = (siteName: string): string =>
  `A new version of ${siteName} is available. Please reload the page to continue.`;

export const getDataUpdateMessage = (siteName: string): string =>
  `New data is available for ${siteName}. Please reload the page to continue.`;
