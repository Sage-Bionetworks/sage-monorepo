/**
 * How often each API re-reads the current data release, so a release switch reaches every API task
 * within this interval. The Spring APIs' copy in `libs/explorers/api-cache-invalidation` must
 * match it.
 */
export const DATA_RELEASE_REFRESH_INTERVAL_MS = 15 * 1000;
