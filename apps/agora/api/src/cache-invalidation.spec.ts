import {
  CACHE_INVALIDATION_POLL_DELAY_MS,
  DataRelease,
  formatDataRelease,
  startCacheInvalidation,
} from './cache-invalidation';

const currentRelease: DataRelease = { dataFile: 'syn13363290', dataVersion: '117' };
const nextVersion: DataRelease = { dataFile: 'syn13363290', dataVersion: '118' };
const nextFile: DataRelease = { dataFile: 'syn13400000', dataVersion: '117' };

describe('startCacheInvalidation', () => {
  let clearCaches: jest.Mock;
  let stopCacheInvalidation: (() => void) | undefined;

  function start(getCurrentDataRelease: () => Promise<DataRelease | null>) {
    stopCacheInvalidation = startCacheInvalidation({ getCurrentDataRelease, clearCaches });
  }

  function releasesInOrder(...releases: (DataRelease | null)[]) {
    const getCurrentDataRelease = jest.fn<Promise<DataRelease | null>, []>();
    for (const release of releases) {
      getCurrentDataRelease.mockResolvedValueOnce(release);
    }
    getCurrentDataRelease.mockResolvedValue(releases[releases.length - 1]);
    return getCurrentDataRelease;
  }

  async function runFirstPoll() {
    await jest.advanceTimersByTimeAsync(0);
  }

  async function runNextPolls(pollCount = 1) {
    await jest.advanceTimersByTimeAsync(CACHE_INVALIDATION_POLL_DELAY_MS * pollCount);
  }

  beforeEach(() => {
    jest.useFakeTimers();
    clearCaches = jest.fn();
    jest.spyOn(console, 'info').mockImplementation();
    jest.spyOn(console, 'warn').mockImplementation();
    jest.spyOn(console, 'error').mockImplementation();
  });

  afterEach(() => {
    stopCacheInvalidation?.();
    jest.useRealTimers();
    jest.restoreAllMocks();
  });

  it('records the current release on the first poll without clearing caches', async () => {
    const getCurrentDataRelease = releasesInOrder(currentRelease);
    start(getCurrentDataRelease);

    await runFirstPoll();

    expect(getCurrentDataRelease).toHaveBeenCalledTimes(1);
    expect(clearCaches).not.toHaveBeenCalled();
  });

  it('does not clear caches while the release is unchanged', async () => {
    start(releasesInOrder(currentRelease));

    await runFirstPoll();
    await runNextPolls(3);

    expect(clearCaches).not.toHaveBeenCalled();
  });

  it('clears caches when the data version changes', async () => {
    start(releasesInOrder(currentRelease, nextVersion));

    await runFirstPoll();
    await runNextPolls();

    expect(clearCaches).toHaveBeenCalledTimes(1);
  });

  it('clears caches when only the data file changes', async () => {
    start(releasesInOrder(currentRelease, nextFile));

    await runFirstPoll();
    await runNextPolls();

    expect(clearCaches).toHaveBeenCalledTimes(1);
  });

  it('clears caches again on the poll after a change, then stops clearing', async () => {
    start(releasesInOrder(currentRelease, nextVersion));

    await runFirstPoll();
    await runNextPolls(4);

    expect(clearCaches).toHaveBeenCalledTimes(2);
  });

  it('skips a poll when the release is unavailable and keeps polling', async () => {
    const getCurrentDataRelease = releasesInOrder(null, currentRelease, nextVersion);
    start(getCurrentDataRelease);

    await runFirstPoll();
    await runNextPolls(2);

    expect(console.warn).toHaveBeenCalledTimes(1);
    expect(getCurrentDataRelease).toHaveBeenCalledTimes(3);
    expect(clearCaches).toHaveBeenCalledTimes(1);
  });

  it('keeps polling after the release cannot be read', async () => {
    const getCurrentDataRelease = jest
      .fn<Promise<DataRelease | null>, []>()
      .mockRejectedValueOnce(new Error('connection reset'))
      .mockResolvedValueOnce(currentRelease)
      .mockResolvedValue(nextVersion);
    start(getCurrentDataRelease);

    await runFirstPoll();
    await runNextPolls(2);

    expect(console.error).toHaveBeenCalledTimes(1);
    expect(clearCaches).toHaveBeenCalledTimes(1);
  });

  it('does not start another poll while a release read is still pending', async () => {
    const getCurrentDataRelease = jest.fn(() => new Promise<DataRelease | null>(() => undefined));
    start(getCurrentDataRelease);

    await runFirstPoll();
    await runNextPolls(3);

    expect(getCurrentDataRelease).toHaveBeenCalledTimes(1);
  });

  it('stops polling once stopped', async () => {
    const getCurrentDataRelease = releasesInOrder(currentRelease);
    start(getCurrentDataRelease);
    await runFirstPoll();

    stopCacheInvalidation?.();
    await runNextPolls(3);

    expect(getCurrentDataRelease).toHaveBeenCalledTimes(1);
  });
});

describe('formatDataRelease', () => {
  it('describes a release the way the footer shows it', () => {
    expect(formatDataRelease(currentRelease)).toBe('syn13363290-v117');
  });
});
