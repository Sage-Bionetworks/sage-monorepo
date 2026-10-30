import { DATA_RELEASE_REFRESH_INTERVAL_MS } from '@sagebionetworks/explorers/constants/node';
import * as Sentry from '@sentry/node';
import type { NextFunction, Request, Response } from 'express';
import {
  DATA_RELEASE_CHANGE_LISTENER_FAILED_MESSAGE,
  DATA_RELEASE_READ_FAILED_REASON,
  DATA_RELEASE_REFRESH_FAILING_MESSAGE,
  DATA_RELEASE_REFRESH_FAILURE_WARNING_DELAY_MS,
  DATA_RELEASE_TRACKER_SOURCE,
  DATA_RELEASE_UNKNOWN_MESSAGE,
  DATAVERSION_DOCUMENT_INCOMPLETE_REASON,
  DataRelease,
  dataReleaseContext,
  DataReleaseTracker,
  formatDataRelease,
  getReleaseAtRequestStart,
} from './data-release';

jest.mock('@sentry/node', () => ({
  captureException: jest.fn(),
  captureMessage: jest.fn(),
}));

const initialRelease: DataRelease = { dataFile: 'syn13363290', dataVersion: '117' };
const nextVersion: DataRelease = { dataFile: 'syn13363290', dataVersion: '118' };
const nextFile: DataRelease = { dataFile: 'syn13400000', dataVersion: '117' };
const failuresBeforeWarning =
  DATA_RELEASE_REFRESH_FAILURE_WARNING_DELAY_MS / DATA_RELEASE_REFRESH_INTERVAL_MS;
const startTime = new Date('2026-10-29T17:00:00.000Z');
const readError = new Error('MongoDB connection lost');

type ReadResult = DataRelease | null | Error;

/** The capture context the tracker sends for one of its fixed messages. */
function trackerEvent(level: 'warning' | 'error', message: string, extra: Record<string, unknown>) {
  return {
    level,
    fingerprint: [DATA_RELEASE_TRACKER_SOURCE, message],
    tags: { source: DATA_RELEASE_TRACKER_SOURCE },
    extra,
  };
}

/** The exception the tracker captured, and the capture context it sent with it. */
function capturedException(callIndex = 0): [Error, unknown] {
  return (Sentry.captureException as jest.Mock).mock.calls[callIndex];
}

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
    const { tracker } = trackReads(initialRelease);
    const onChange = jest.fn();
    tracker.onChange(onChange);

    await tracker.start();

    expect(tracker.latestRelease()).toEqual(initialRelease);
    expect(tracker.getState()).toEqual({
      release: 'syn13363290-v117',
      lastRefreshedAt: startTime.toISOString(),
      consecutiveFailures: 0,
    });
    expect(onChange).not.toHaveBeenCalled();
  });

  it('re-reads the release every refresh interval', async () => {
    const { tracker, readCurrentRelease } = trackReads(initialRelease);

    await tracker.start();
    await runNextRefreshes(2);

    expect(readCurrentRelease).toHaveBeenCalledTimes(3);
  });

  it('reports a change of data version to listeners', async () => {
    const { tracker } = trackReads(initialRelease, nextVersion);
    const onChange = jest.fn();
    tracker.onChange(onChange);

    await tracker.start();
    await runNextRefreshes();

    expect(onChange).toHaveBeenCalledWith(initialRelease, nextVersion);
    expect(tracker.latestRelease()).toEqual(nextVersion);
  });

  it('reports a change of data file to listeners', async () => {
    const { tracker } = trackReads(initialRelease, nextFile);
    const onChange = jest.fn();
    tracker.onChange(onChange);

    await tracker.start();
    await runNextRefreshes();

    expect(onChange).toHaveBeenCalledWith(initialRelease, nextFile);
  });

  it('does not report a change while the release is unchanged', async () => {
    const { tracker } = trackReads(initialRelease);
    const onChange = jest.fn();
    tracker.onChange(onChange);

    await tracker.start();
    await runNextRefreshes(3);

    expect(onChange).not.toHaveBeenCalled();
  });

  it('stops calling a listener once it is removed', async () => {
    const { tracker } = trackReads(initialRelease, nextVersion);
    const onChange = jest.fn();
    const removeListener = tracker.onChange(onChange);

    await tracker.start();
    removeListener();
    await runNextRefreshes();

    expect(onChange).not.toHaveBeenCalled();
  });

  it('still calls the other listeners when one fails, and reports the failure', async () => {
    const { tracker } = trackReads(initialRelease, nextVersion);
    const listenerError = new Error('cache clear failed');
    tracker.onChange(() => {
      throw listenerError;
    });
    const otherListener = jest.fn();
    tracker.onChange(otherListener);

    await tracker.start();
    await runNextRefreshes();

    expect(otherListener).toHaveBeenCalledWith(initialRelease, nextVersion);
    const [exception, captureContext] = capturedException();
    expect(exception.cause).toBe(listenerError);
    expect(captureContext).toEqual(
      trackerEvent('error', DATA_RELEASE_CHANGE_LISTENER_FAILED_MESSAGE, {
        previous: 'syn13363290-v117',
        current: 'syn13363290-v118',
      }),
    );
  });

  it('keeps the last release and counts failures when refreshes fail', async () => {
    const { tracker } = trackReads(initialRelease, readError, null);

    await tracker.start();
    await runNextRefreshes(2);

    expect(tracker.latestRelease()).toEqual(initialRelease);
    expect(tracker.getState()).toEqual({
      release: 'syn13363290-v117',
      lastRefreshedAt: startTime.toISOString(),
      consecutiveFailures: 2,
    });
  });

  it('warns once when refreshes have kept failing for the warning delay', async () => {
    const { tracker } = trackReads(initialRelease, readError);

    await tracker.start();
    await runNextRefreshes(failuresBeforeWarning - 1);
    expect(console.warn).not.toHaveBeenCalled();

    await runNextRefreshes();
    expect(console.warn).toHaveBeenCalledTimes(1);
    const [exception, captureContext] = capturedException();
    expect(exception.message).toBe(
      `${DATA_RELEASE_TRACKER_SOURCE}: ${DATA_RELEASE_REFRESH_FAILING_MESSAGE}`,
    );
    expect(exception.cause).toBe(readError);
    expect(captureContext).toEqual(
      trackerEvent('warning', DATA_RELEASE_REFRESH_FAILING_MESSAGE, {
        consecutiveFailures: failuresBeforeWarning,
        releaseInUse: 'syn13363290-v117',
        reason: DATA_RELEASE_READ_FAILED_REASON,
      }),
    );

    await runNextRefreshes(failuresBeforeWarning);
    expect(console.warn).toHaveBeenCalledTimes(1);
  });

  it('logs the recovery and resets the failure count after warning', async () => {
    const reads: ReadResult[] = [
      initialRelease,
      ...Array<ReadResult>(failuresBeforeWarning).fill(readError),
      initialRelease,
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

    expect(tracker.latestRelease()).toBeNull();
    expect(console.error).toHaveBeenCalledTimes(1);
    expect(Sentry.captureMessage).toHaveBeenCalledTimes(1);
    expect(Sentry.captureMessage).toHaveBeenCalledWith(
      `${DATA_RELEASE_TRACKER_SOURCE}: ${DATA_RELEASE_UNKNOWN_MESSAGE}`,
      trackerEvent('error', DATA_RELEASE_UNKNOWN_MESSAGE, {
        reason: DATAVERSION_DOCUMENT_INCOMPLETE_REASON,
      }),
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
    const [exception, captureContext] = capturedException();
    expect(exception.cause).toBe(readError);
    expect(captureContext).toEqual(
      trackerEvent('error', DATA_RELEASE_UNKNOWN_MESSAGE, {
        reason: DATA_RELEASE_READ_FAILED_REASON,
      }),
    );
  });

  it('starts using the release once one can be read, without reporting a change', async () => {
    const { tracker } = trackReads(null, initialRelease);
    const onChange = jest.fn();
    tracker.onChange(onChange);

    await tracker.start();
    await runNextRefreshes();

    expect(tracker.latestRelease()).toEqual(initialRelease);
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
    const { tracker, readCurrentRelease } = trackReads(initialRelease);

    await tracker.start();
    tracker.stop();
    await runNextRefreshes(3);

    expect(readCurrentRelease).toHaveBeenCalledTimes(1);
  });
});

describe('formatDataRelease', () => {
  it('describes a release the way the footer shows it', () => {
    expect(formatDataRelease(initialRelease)).toBe('syn13363290-v117');
  });
});

describe('dataReleaseContext', () => {
  function trackerAt(release: DataRelease | null) {
    const tracker = new DataReleaseTracker({ readCurrentRelease: jest.fn() });
    const latestReleaseSpy = jest.spyOn(tracker, 'latestRelease').mockReturnValue(release);
    return { tracker, latestReleaseSpy };
  }

  function runRequest<T>(tracker: DataReleaseTracker, handler: () => Promise<T>): Promise<T> {
    return new Promise<T>((resolve, reject) => {
      const next: NextFunction = () => handler().then(resolve, reject);
      dataReleaseContext(tracker)({} as Request, {} as Response, next);
    });
  }

  afterEach(() => jest.restoreAllMocks());

  it('keeps the release a request started with after the tracker moves on', async () => {
    const { tracker, latestReleaseSpy } = trackerAt(initialRelease);

    const releaseAtRequestStart = await runRequest(tracker, async () => {
      latestReleaseSpy.mockReturnValue(nextVersion);
      await Promise.resolve();
      return getReleaseAtRequestStart();
    });

    expect(releaseAtRequestStart).toEqual(initialRelease);
  });

  it('keeps each concurrent request on its own release', async () => {
    const { tracker, latestReleaseSpy } = trackerAt(initialRelease);
    let releaseFirstRequest!: () => void;
    const firstRequestCanFinish = new Promise<void>((resolve) => (releaseFirstRequest = resolve));

    const firstRequest = runRequest(tracker, async () => {
      await firstRequestCanFinish;
      return getReleaseAtRequestStart();
    });
    latestReleaseSpy.mockReturnValue(nextVersion);
    const secondRequest = runRequest(tracker, async () => getReleaseAtRequestStart());
    releaseFirstRequest();

    await expect(firstRequest).resolves.toEqual(initialRelease);
    await expect(secondRequest).resolves.toEqual(nextVersion);
  });

  it('marks a request made while the release is unknown', async () => {
    const { tracker } = trackerAt(null);

    await expect(runRequest(tracker, async () => getReleaseAtRequestStart())).resolves.toBeNull();
  });

  it('has no release outside a request', () => {
    expect(getReleaseAtRequestStart()).toBeUndefined();
  });
});
