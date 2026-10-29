import { DataRelease, formatDataRelease, isSameDataRelease } from './data-release';

/** Matches the Spring APIs' cache invalidation poll in libs/explorers/api-cache-invalidation. */
export const CACHE_INVALIDATION_POLL_DELAY_MS = 60_000;

export interface CacheInvalidationOptions {
  getCurrentDataRelease: () => Promise<DataRelease | null>;
  clearCaches: () => void;
  pollDelayMs?: number;
}

/**
 * Clears the API's in-memory caches when a new data release is loaded into the database, so the
 * API stops serving the previous release without a restart. The first poll only records the
 * current release. After a change, the caches are cleared again on the next poll, since a request
 * that read the database before the change can cache its result after the first clear.
 *
 * Polls are chained rather than run on an interval, so a slow database read never overlaps the next
 * poll, and the timer does not keep the process alive. Returns a function that stops polling.
 */
export function startCacheInvalidation({
  getCurrentDataRelease,
  clearCaches,
  pollDelayMs = CACHE_INVALIDATION_POLL_DELAY_MS,
}: CacheInvalidationOptions): () => void {
  let lastSeenRelease: DataRelease | null = null;
  let followUpClearPending = false;
  let isStopped = false;
  let nextPoll: NodeJS.Timeout | undefined;

  const scheduleNextPoll = () => {
    if (isStopped) {
      return;
    }
    nextPoll = setTimeout(poll, pollDelayMs);
    nextPoll.unref();
  };

  async function poll() {
    try {
      if (followUpClearPending) {
        followUpClearPending = false;
        console.info('Clearing caches again after the data release change');
        clearCaches();
      }
      const currentRelease = await getCurrentDataRelease();
      if (!currentRelease) {
        console.warn('Data release unavailable; skipping cache invalidation check');
        return;
      }
      const previousRelease = lastSeenRelease;
      lastSeenRelease = currentRelease;
      if (!previousRelease) {
        console.info(`Initial data release observed: ${formatDataRelease(currentRelease)}`);
        return;
      }
      if (!isSameDataRelease(previousRelease, currentRelease)) {
        console.info(
          `Data release changed ${formatDataRelease(previousRelease)} -> ` +
            `${formatDataRelease(currentRelease)}; clearing caches`,
        );
        clearCaches();
        followUpClearPending = true;
      }
    } catch (error) {
      console.error(
        'Unexpected error during cache invalidation poll; will retry next interval',
        error,
      );
    } finally {
      scheduleNextPoll();
    }
  }

  void poll();

  return () => {
    isStopped = true;
    clearTimeout(nextPoll);
  };
}
