import { DATA_RELEASE_REFRESH_INTERVAL_MS } from '@sagebionetworks/explorers/constants/node';
import { clearCachesOnReleaseChange } from './cache-invalidation';
import { DataRelease, DataReleaseTracker } from './data-release';

jest.mock('@sentry/node', () => ({ captureException: jest.fn(), captureMessage: jest.fn() }));

const initialRelease: DataRelease = { dataFile: 'syn13363290', dataVersion: '117' };
const nextVersion: DataRelease = { dataFile: 'syn13363290', dataVersion: '118' };

describe('clearCachesOnReleaseChange', () => {
  let tracker: DataReleaseTracker;
  let clearCaches: jest.Mock;

  /** Starts a tracker whose refreshes read `releases` in order, then the last one again. */
  async function startTracker(...releases: (DataRelease | null)[]) {
    const readCurrentRelease = jest.fn<Promise<DataRelease | null>, []>();
    for (const release of releases) {
      readCurrentRelease.mockResolvedValueOnce(release);
    }
    readCurrentRelease.mockResolvedValue(releases[releases.length - 1]);
    tracker = new DataReleaseTracker({ readCurrentRelease });
    const stopClearing = clearCachesOnReleaseChange(tracker, clearCaches);
    await tracker.start();
    return stopClearing;
  }

  async function runNextRefreshes(refreshCount = 1) {
    await jest.advanceTimersByTimeAsync(DATA_RELEASE_REFRESH_INTERVAL_MS * refreshCount);
  }

  beforeEach(() => {
    jest.useFakeTimers();
    clearCaches = jest.fn();
    jest.spyOn(console, 'info').mockImplementation();
    jest.spyOn(console, 'error').mockImplementation();
  });

  afterEach(() => {
    tracker.stop();
    jest.useRealTimers();
    jest.restoreAllMocks();
  });

  it('does not clear the caches when the first release is read', async () => {
    await startTracker(initialRelease);

    expect(clearCaches).not.toHaveBeenCalled();
  });

  it('clears the caches once when the release changes, and not again after', async () => {
    await startTracker(initialRelease, nextVersion);

    await runNextRefreshes(3);

    expect(clearCaches).toHaveBeenCalledTimes(1);
    expect(console.info).toHaveBeenCalledWith(expect.stringContaining('clearing caches'));
  });

  it('does not clear the caches while the release is unchanged', async () => {
    await startTracker(initialRelease);

    await runNextRefreshes(3);

    expect(clearCaches).not.toHaveBeenCalled();
  });

  it('does not clear the caches when the release first becomes known', async () => {
    await startTracker(null, initialRelease);

    await runNextRefreshes();

    expect(clearCaches).not.toHaveBeenCalled();
  });

  it('stops clearing the caches once stopped', async () => {
    const stopClearing = await startTracker(initialRelease, nextVersion);

    stopClearing();
    await runNextRefreshes();

    expect(clearCaches).not.toHaveBeenCalled();
  });
});
