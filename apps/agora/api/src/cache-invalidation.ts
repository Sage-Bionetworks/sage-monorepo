import mongoose from 'mongoose';

/** Matches the Spring APIs' cache invalidation poll in libs/explorers/api-cache-invalidation. */
export const CACHE_INVALIDATION_POLL_DELAY_MS = 60_000;

const DATA_VERSION_COLLECTION = 'dataversion';

/**
 * Identifies a data release by both fields of the `dataversion` document, since a release can
 * change the data file without changing the version number.
 */
export interface DataRelease {
  dataFile: string;
  dataVersion: string;
}

export interface CacheInvalidationOptions {
  getCurrentDataRelease: () => Promise<DataRelease | null>;
  clearCaches: () => void;
  pollDelayMs?: number;
}

export function formatDataRelease({ dataFile, dataVersion }: DataRelease): string {
  return `${dataFile}-v${dataVersion}`;
}

function isSameDataRelease(first: DataRelease, second: DataRelease): boolean {
  return first.dataFile === second.dataFile && first.dataVersion === second.dataVersion;
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

/**
 * Reads the current release from the `dataversion` collection. The loader may store the version as
 * a number, so both fields are compared as strings.
 */
export async function getAgoraDataRelease(): Promise<DataRelease | null> {
  const document = await mongoose.connection.db
    ?.collection(DATA_VERSION_COLLECTION)
    .findOne({}, { projection: { data_file: 1, data_version: 1 } });
  if (document?.['data_file'] == null || document?.['data_version'] == null) {
    return null;
  }
  return { dataFile: String(document['data_file']), dataVersion: String(document['data_version']) };
}
