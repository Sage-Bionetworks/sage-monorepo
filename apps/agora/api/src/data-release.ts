import { DATA_RELEASE_REFRESH_INTERVAL_MS } from '@sagebionetworks/explorers/constants/node';
import * as Sentry from '@sentry/node';
import type { RequestHandler } from 'express';
import mongoose from 'mongoose';
import { AsyncLocalStorage } from 'node:async_hooks';

const DATA_VERSION_COLLECTION = 'dataversion';

/** How long refreshes can keep failing before the tracker warns, once. */
export const DATA_RELEASE_REFRESH_FAILURE_WARNING_DELAY_MS = 2 * 60 * 1000;

/** The source the tracker's Sentry events are titled, grouped, and tagged with. */
export const DATA_RELEASE_TRACKER_SOURCE = 'DataReleaseTracker';
export const DATA_RELEASE_UNKNOWN_MESSAGE = 'Data release unknown';
export const DATA_RELEASE_REFRESH_FAILING_MESSAGE = 'Data release refresh keeps failing';
export const DATA_RELEASE_CHANGE_LISTENER_FAILED_MESSAGE = 'A data release change listener failed';
export const DATAVERSION_DOCUMENT_INCOMPLETE_REASON =
  'The dataversion document is missing or incomplete';
export const DATA_RELEASE_READ_FAILED_REASON = 'The data release could not be read';

/**
 * Sends a tracker event to Sentry the way the frontend's `SourceLogger` does: titled
 * `<source>: <message>` and grouped by source and message, which stay fixed, with the values that
 * vary per occurrence in `extra`. A caught `Error` is attached as the event's cause.
 */
function reportToSentry(
  level: 'warning' | 'error',
  message: string,
  extra: Record<string, unknown>,
  error?: unknown,
): void {
  const title = `${DATA_RELEASE_TRACKER_SOURCE}: ${message}`;
  const captureContext = {
    level,
    fingerprint: [DATA_RELEASE_TRACKER_SOURCE, message],
    tags: { source: DATA_RELEASE_TRACKER_SOURCE },
    extra: error === undefined || error instanceof Error ? extra : { ...extra, cause: error },
  };
  if (error instanceof Error) {
    Sentry.captureException(new Error(title, { cause: error }), captureContext);
  } else {
    Sentry.captureMessage(title, captureContext);
  }
}

export interface DataRelease {
  /** Synapse ID of the data file, e.g. `syn13363290`. */
  dataFile: string;
  /** Synapse version number of the data file. */
  dataVersion: string;
}

/** The tracker's state, as reported in the API's health endpoint. */
export interface DataReleaseState {
  release: string | null;
  lastRefreshedAt: string | null;
  consecutiveFailures: number;
}

export type DataReleaseChangeListener = (previous: DataRelease, current: DataRelease) => void;

export interface DataReleaseTrackerOptions {
  readCurrentRelease: () => Promise<DataRelease | null>;
  refreshIntervalMs?: number;
}

export function formatDataRelease({ dataFile, dataVersion }: DataRelease): string {
  return `${dataFile}-v${dataVersion}`;
}

export function isSameDataRelease(first: DataRelease, second: DataRelease): boolean {
  return first.dataFile === second.dataFile && first.dataVersion === second.dataVersion;
}

/**
 * Keeps this API task's view of the current data release, re-reading it every refresh interval so a
 * release switch reaches the task within that interval. A failed refresh keeps the last release
 * read, so a database outage doesn't change which release the task serves.
 *
 * Refreshes are chained rather than run on an interval, so a slow read never overlaps the next
 * refresh, and the timer does not keep the process alive.
 */
export class DataReleaseTracker {
  private readonly readCurrentRelease: () => Promise<DataRelease | null>;
  private readonly refreshIntervalMs: number;
  private readonly failureWarningThreshold: number;
  private readonly changeListeners = new Set<DataReleaseChangeListener>();
  private release: DataRelease | null = null;
  private lastRefreshedAt: Date | null = null;
  private consecutiveFailures = 0;
  private hasReportedUnknownRelease = false;
  private hasWarnedOfFailingRefresh = false;
  private isStopped = false;
  private nextRefresh: NodeJS.Timeout | undefined;

  constructor({
    readCurrentRelease,
    refreshIntervalMs = DATA_RELEASE_REFRESH_INTERVAL_MS,
  }: DataReleaseTrackerOptions) {
    this.readCurrentRelease = readCurrentRelease;
    this.refreshIntervalMs = refreshIntervalMs;
    this.failureWarningThreshold = Math.ceil(
      DATA_RELEASE_REFRESH_FAILURE_WARNING_DELAY_MS / refreshIntervalMs,
    );
  }

  /** Runs the first refresh, then keeps refreshing until stopped. Resolves after the first one. */
  start(): Promise<void> {
    this.isStopped = false;
    return this.refresh();
  }

  stop(): void {
    this.isStopped = true;
    clearTimeout(this.nextRefresh);
  }

  /** The last release read, or null if none has been read since the API started. */
  latestRelease(): DataRelease | null {
    return this.release;
  }

  /**
   * Calls the listener each time a refresh reads a release different from the last one. Reading the
   * first release is not a change. Returns a function that removes the listener.
   */
  onChange(listener: DataReleaseChangeListener): () => void {
    this.changeListeners.add(listener);
    return () => this.changeListeners.delete(listener);
  }

  getState(): DataReleaseState {
    return {
      release: this.release ? formatDataRelease(this.release) : null,
      lastRefreshedAt: this.lastRefreshedAt?.toISOString() ?? null,
      consecutiveFailures: this.consecutiveFailures,
    };
  }

  private async refresh(): Promise<void> {
    try {
      const release = await this.readCurrentRelease();
      if (release) {
        this.recordRelease(release);
      } else {
        this.recordFailure(DATAVERSION_DOCUMENT_INCOMPLETE_REASON);
      }
    } catch (error) {
      this.recordFailure(DATA_RELEASE_READ_FAILED_REASON, error);
    } finally {
      this.scheduleNextRefresh();
    }
  }

  private scheduleNextRefresh(): void {
    if (this.isStopped) {
      return;
    }
    this.nextRefresh = setTimeout(() => void this.refresh(), this.refreshIntervalMs);
    this.nextRefresh.unref();
  }

  private recordRelease(release: DataRelease): void {
    if (this.hasWarnedOfFailingRefresh) {
      console.info(
        `Data release refresh recovered after ${this.consecutiveFailures} failed attempts`,
      );
    }
    const previous = this.release;
    this.release = release;
    this.lastRefreshedAt = new Date();
    this.consecutiveFailures = 0;
    this.hasWarnedOfFailingRefresh = false;

    if (!previous) {
      console.info(`Initial data release observed: ${formatDataRelease(release)}`);
      return;
    }
    if (!isSameDataRelease(previous, release)) {
      console.info(
        `Data release changed ${formatDataRelease(previous)} -> ${formatDataRelease(release)}`,
      );
      this.notifyChange(previous, release);
    }
  }

  private notifyChange(previous: DataRelease, current: DataRelease): void {
    for (const listener of this.changeListeners) {
      try {
        listener(previous, current);
      } catch (error) {
        console.error(DATA_RELEASE_CHANGE_LISTENER_FAILED_MESSAGE, error);
        reportToSentry(
          'error',
          DATA_RELEASE_CHANGE_LISTENER_FAILED_MESSAGE,
          { previous: formatDataRelease(previous), current: formatDataRelease(current) },
          error,
        );
      }
    }
  }

  // An unknown release and a failing refresh are reported separately: the first means no release
  // has been read since the API started, the second that refreshes have kept failing after one was.
  private recordFailure(reason: string, error?: unknown): void {
    this.consecutiveFailures++;

    if (!this.release) {
      if (!this.hasReportedUnknownRelease) {
        this.hasReportedUnknownRelease = true;
        console.error(`${DATA_RELEASE_UNKNOWN_MESSAGE}: ${reason}`, error ?? '');
        reportToSentry('error', DATA_RELEASE_UNKNOWN_MESSAGE, { reason }, error);
      }
      return;
    }

    if (
      this.consecutiveFailures >= this.failureWarningThreshold &&
      !this.hasWarnedOfFailingRefresh
    ) {
      this.hasWarnedOfFailingRefresh = true;
      const releaseInUse = formatDataRelease(this.release);
      console.warn(
        `${DATA_RELEASE_REFRESH_FAILING_MESSAGE}: ${this.consecutiveFailures} failures in a row, ` +
          `still using ${releaseInUse}: ${reason}`,
        error ?? '',
      );
      reportToSentry(
        'warning',
        DATA_RELEASE_REFRESH_FAILING_MESSAGE,
        { consecutiveFailures: this.consecutiveFailures, releaseInUse, reason },
        error,
      );
    }
  }
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

export const dataReleaseTracker = new DataReleaseTracker({
  readCurrentRelease: getAgoraDataRelease,
});

const requestDataRelease = new AsyncLocalStorage<DataRelease | null>();

/**
 * Express middleware that reads the tracker's release once per request and keeps it for the rest
 * of the request, so every cache lookup and store the request makes uses the same release, even if
 * the tracker moves on to a new one while the request is still running.
 */
export function dataReleaseContext(tracker: DataReleaseTracker): RequestHandler {
  return (_request, _response, next) => requestDataRelease.run(tracker.latestRelease(), next);
}

/**
 * The release the current request started with, read once by `dataReleaseContext` when it arrived:
 * null while the release is unknown, and undefined outside a request.
 */
export function getReleaseAtRequestStart(): DataRelease | null | undefined {
  return requestDataRelease.getStore();
}
