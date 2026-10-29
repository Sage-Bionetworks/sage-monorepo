import { DATA_RELEASE_REFRESH_INTERVAL_MS } from '@sagebionetworks/explorers/constants/node';
import * as Sentry from '@sentry/node';
import {
  DATA_RELEASE_REFRESH_FAILURE_WARNING_DELAY_MS,
  DataRelease,
  DataReleaseTracker,
  formatDataRelease,
} from './data-release';

jest.mock('@sentry/node', () => ({
  captureException: jest.fn(),
  captureMessage: jest.fn(),
}));

const currentRelease: DataRelease = { dataFile: 'syn13363290', dataVersion: '117' };
const nextVersion: DataRelease = { dataFile: 'syn13363290', dataVersion: '118' };
const nextFile: DataRelease = { dataFile: 'syn13400000', dataVersion: '117' };
const failuresBeforeWarning =
  DATA_RELEASE_REFRESH_FAILURE_WARNING_DELAY_MS / DATA_RELEASE_REFRESH_INTERVAL_MS;
const startTime = new Date('2026-10-29T17:00:00.000Z');
const readError = new Error('MongoDB connection lost');

type ReadResult = DataRelease | null | Error;

describe('DataReleaseTracker', () => {
  let tracker: DataReleaseTracker | undefined;

  function trackReads(...results: ReadResult[]) {
    const readCurrentRelease = jest.fn<Promise<DataRelease | null>, []>();
    for (const result of results) {
      if (result instanceof Error) {
        readCurrentRelease.mockRejectedValueOnce(result);
      } else {
        readCurrentRelease.mockResolvedValueOnce(result);
      }
    }
    const last = results[results.length - 1];
    if (last instanceof Error) {
      readCurrentRelease.mockRejectedValue(last);
    } else {
      readCurrentRelease.mockResolvedValue(last);
    }
    tracker = new DataReleaseTracker({ readCurrentRelease });
    return { tracker, readCurrentRelease };
  }

  async function runNextRefreshes(refreshCount = 1) {
    await jest.advanceTimersByTimeAsync(DATA_RELEASE_REFRESH_INTERVAL_MS * refreshCount);
  }

  beforeEach(() => {
    jest.useFakeTimers({ now: startTime });
    jest.spyOn(console, 'info').mockImplementation();
    jest.spyOn(console, 'warn').mockImplementation();
    jest.spyOn(console, 'error').mockImplementation();
  });

  afterEach(() => {
    tracker?.stop();
    tracker = undefined;
    jest.useRealTimers();
    jest.restoreAllMocks();
    jest.clearAllMocks();
  });

  it('reads the release once start resolves, without reporting a change', async () => {
    const { tracker } = trackReads(currentRelease);
    const onChange = jest.fn();
    tracker.onChange(onChange);

    await tracker.start();

    expect(tracker.currentRelease()).toEqual(currentRelease);
    expect(tracker.getState()).toEqual({
      release: 'syn13363290-v117',
      lastRefreshedAt: startTime.toISOString(),
      consecutiveFailures: 0,
    });
    expect(onChange).not.toHaveBeenCalled();
  });

  it('re-reads the release every refresh interval', async () => {
    const { tracker, readCurrentRelease } = trackReads(currentRelease);

    await tracker.start();
    await runNextRefreshes(2);

    expect(readCurrentRelease).toHaveBeenCalledTimes(3);
  });

  it('reports a change of data version to listeners', async () => {
    const { tracker } = trackReads(currentRelease, nextVersion);
    const onChange = jest.fn();
    tracker.onChange(onChange);

    await tracker.start();
    await runNextRefreshes();

    expect(onChange).toHaveBeenCalledWith(currentRelease, nextVersion);
    expect(tracker.currentRelease()).toEqual(nextVersion);
  });

  it('reports a change of data file to listeners', async () => {
    const { tracker } = trackReads(currentRelease, nextFile);
    const onChange = jest.fn();
    tracker.onChange(onChange);

    await tracker.start();
    await runNextRefreshes();

    expect(onChange).toHaveBeenCalledWith(currentRelease, nextFile);
  });

  it('does not report a change while the release is unchanged', async () => {
    const { tracker } = trackReads(currentRelease);
    const onChange = jest.fn();
    tracker.onChange(onChange);

    await tracker.start();
    await runNextRefreshes(3);

    expect(onChange).not.toHaveBeenCalled();
  });

  it('stops calling a listener once it is removed', async () => {
    const { tracker } = trackReads(currentRelease, nextVersion);
    const onChange = jest.fn();
    const removeListener = tracker.onChange(onChange);

    await tracker.start();
    removeListener();
    await runNextRefreshes();

    expect(onChange).not.toHaveBeenCalled();
  });

  it('still calls the other listeners when one fails, and reports the failure', async () => {
    const { tracker } = trackReads(currentRelease, nextVersion);
    const listenerError = new Error('cache clear failed');
    tracker.onChange(() => {
      throw listenerError;
    });
    const otherListener = jest.fn();
    tracker.onChange(otherListener);

    await tracker.start();
    await runNextRefreshes();

    expect(otherListener).toHaveBeenCalledWith(currentRelease, nextVersion);
    expect(Sentry.captureException).toHaveBeenCalledWith(listenerError);
  });

  it('keeps the last release and counts failures when refreshes fail', async () => {
    const { tracker } = trackReads(currentRelease, readError, null);

    await tracker.start();
    await runNextRefreshes(2);

    expect(tracker.currentRelease()).toEqual(currentRelease);
    expect(tracker.getState()).toEqual({
      release: 'syn13363290-v117',
      lastRefreshedAt: startTime.toISOString(),
      consecutiveFailures: 2,
    });
  });

  it('warns once when refreshes have kept failing for the warning delay', async () => {
    const { tracker } = trackReads(currentRelease, readError);

    await tracker.start();
    await runNextRefreshes(failuresBeforeWarning - 1);
    expect(console.warn).not.toHaveBeenCalled();

    await runNextRefreshes();
    expect(console.warn).toHaveBeenCalledTimes(1);
    expect(Sentry.captureMessage).toHaveBeenCalledWith(
      expect.stringContaining('still using syn13363290-v117'),
      'warning',
    );

    await runNextRefreshes(failuresBeforeWarning);
    expect(console.warn).toHaveBeenCalledTimes(1);
  });

  it('logs the recovery and resets the failure count after warning', async () => {
    const reads: ReadResult[] = [
      currentRelease,
      ...Array<ReadResult>(failuresBeforeWarning).fill(readError),
      currentRelease,
    ];
    const { tracker } = trackReads(...reads);

    await tracker.start();
    await runNextRefreshes(failuresBeforeWarning + 1);

    expect(console.info).toHaveBeenCalledWith(
      `Data release refresh recovered after ${failuresBeforeWarning} failed attempts`,
    );
    expect(tracker.getState().consecutiveFailures).toBe(0);
  });

  it('reports an unknown release once as an error, without a refresh warning', async () => {
    const { tracker } = trackReads(null);

    await tracker.start();
    await runNextRefreshes(failuresBeforeWarning * 2);

    expect(tracker.currentRelease()).toBeNull();
    expect(console.error).toHaveBeenCalledTimes(1);
    expect(Sentry.captureMessage).toHaveBeenCalledTimes(1);
    expect(Sentry.captureMessage).toHaveBeenCalledWith(
      'Data release unknown: The dataversion document is missing or incomplete',
      'error',
    );
    expect(console.warn).not.toHaveBeenCalled();
  });

  it('reports an unreadable release at startup as an unknown release', async () => {
    const { tracker } = trackReads(readError);

    await tracker.start();

    expect(tracker.getState()).toEqual({
      release: null,
      lastRefreshedAt: null,
      consecutiveFailures: 1,
    });
    expect(Sentry.captureMessage).toHaveBeenCalledWith(
      'Data release unknown: The data release could not be read',
      'error',
    );
  });

  it('starts using the release once one can be read, without reporting a change', async () => {
    const { tracker } = trackReads(null, currentRelease);
    const onChange = jest.fn();
    tracker.onChange(onChange);

    await tracker.start();
    await runNextRefreshes();

    expect(tracker.currentRelease()).toEqual(currentRelease);
    expect(onChange).not.toHaveBeenCalled();
  });

  it('does not start another refresh while a read is still pending', async () => {
    const readCurrentRelease = jest.fn(() => new Promise<DataRelease | null>(() => undefined));
    tracker = new DataReleaseTracker({ readCurrentRelease });

    void tracker.start();
    await runNextRefreshes(3);

    expect(readCurrentRelease).toHaveBeenCalledTimes(1);
  });

  it('stops refreshing once stopped', async () => {
    const { tracker, readCurrentRelease } = trackReads(currentRelease);

    await tracker.start();
    tracker.stop();
    await runNextRefreshes(3);

    expect(readCurrentRelease).toHaveBeenCalledTimes(1);
  });
});

describe('formatDataRelease', () => {
  it('describes a release the way the footer shows it', () => {
    expect(formatDataRelease(currentRelease)).toBe('syn13363290-v117');
  });
});
