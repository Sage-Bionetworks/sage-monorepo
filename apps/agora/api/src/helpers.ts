import { Response } from 'express';
import debug from 'debug';
import NodeCache from 'node-cache';
import {
  DataRelease,
  dataReleaseTracker,
  formatDataRelease,
  getReleaseAtRequestStart,
  isSameDataRelease,
} from './data-release';

export function setHeaders(res: Response) {
  res.setHeader('Cache-Control', 'no-cache, no-store, must-revalidate');
  res.setHeader('Pragma', 'no-cache');
  res.setHeader('Expires', 0);
  res.setHeader('Content-Type', 'application/json');
}

// Normalize a port into a number, string, or false.
export function normalizePort(val: any) {
  const tPort = parseInt(val, 10);

  if (isNaN(tPort)) {
    // named pipe
    return val;
  }

  if (tPort >= 0) {
    // port number
    return tPort;
  }

  return false;
}

// Event listener for HTTP server "error" event
export function onError(error: any, port: any) {
  if (error.syscall !== 'listen') {
    throw error;
  }

  const bind = typeof port === 'string' ? 'Pipe ' + port : 'Port ' + port;

  // handle specific listen errors with friendly messages
  switch (error.code) {
    case 'EACCES':
      console.error(bind + ' requires elevated privileges');
      process.exit(1);
      break;
    case 'EADDRINUSE':
      console.error(bind + ' is already in use');
      process.exit(1);
      break;
    default:
      throw error;
  }
}

// Event listener for HTTP server "listening" event
export function onListening(address: any) {
  debug('Listening on ' + typeof address === 'string' ? 'pipe ' + address : 'port ' + address.port);
}

// -------------------------------------------------------------------------- //
// Collation
// -------------------------------------------------------------------------- //
export const CASE_INSENSITIVE_COLLATION = { locale: 'en', strength: 2 } as const;

// -------------------------------------------------------------------------- //
// Cache
// -------------------------------------------------------------------------- //

/** Where the caches get the release to scope their keys to. */
export interface CacheReleaseSource {
  /**
   * The release the current request started with, read once when it arrived, or null if it is
   * unknown. Cache keys are built from it.
   */
  releaseAtRequestStart: () => DataRelease | null;
  /**
   * The release the tracker read most recently, which can change while a request runs, or null
   * if it is unknown. A value is only kept when its request's release is still this one.
   */
  latestRelease: () => DataRelease | null;
}

const trackerReleaseSource: CacheReleaseSource = {
  releaseAtRequestStart: () => {
    const releaseAtRequestStart = getReleaseAtRequestStart();
    return releaseAtRequestStart === undefined
      ? dataReleaseTracker.latestRelease()
      : releaseAtRequestStart;
  },
  latestRelease: () => dataReleaseTracker.latestRelease(),
};

// Real releases are formatted as `<data file>-v<data version>`, so this can't collide with one.
const UNKNOWN_RELEASE_SCOPE = 'unknown-release';

function scopeKey(release: DataRelease | null, key: string): string {
  return `${release ? formatDataRelease(release) : UNKNOWN_RELEASE_SCOPE}:${key}`;
}

/** Whether `release` is the tracker's latest one. Never true while either release is unknown. */
function isLatestRelease(release: DataRelease | null, releases: CacheReleaseSource): boolean {
  const latestRelease = releases.latestRelease();
  if (release === null || latestRelease === null) {
    return false;
  }
  return isSameDataRelease(release, latestRelease);
}

/**
 * Scopes every key to the request's data release, so after a release switch requests miss the
 * previous release's entries instead of serving them. A value is only stored for the release the
 * API is serving now: a request that started before a switch, or while the release is unknown,
 * doesn't keep what it read.
 */
export class ReleaseScopedCache {
  constructor(
    private readonly nodeCache: NodeCache,
    private readonly releases: CacheReleaseSource,
  ) {}

  get<T>(key: string): T | undefined {
    const release = this.releases.releaseAtRequestStart();
    return release ? this.nodeCache.get<T>(scopeKey(release, key)) : undefined;
  }

  set<T>(key: string, value: T): boolean {
    const release = this.releases.releaseAtRequestStart();
    if (!isLatestRelease(release, this.releases)) {
      return false;
    }
    return this.nodeCache.set(scopeKey(release, key), value);
  }

  flushAll(): void {
    this.nodeCache.flushAll();
  }
}

export const cache = new ReleaseScopedCache(new NodeCache(), trackerReleaseSource);

// TODO: Performance issues with node-cache on large object, this should be revisited when possible.
// For now used AlternativeCache (local variables) to store large set of data.

/** Like `ReleaseScopedCache`, but stores values as they are rather than copying them. */
export class AlternativeCache {
  private data: { [key: string]: any } = {};

  constructor(private readonly releases: CacheReleaseSource) {}

  /**
   * Caches the load's promise rather than its result, so concurrent misses on one key share a
   * single load instead of each holding its own copy of a large data set in memory. A rejected
   * load is evicted so the next request retries it. A load is shared even while the release is
   * unknown, but its result is only kept if the release it started with is still the latest.
   */
  getOrLoad<T>(key: string, load: () => Promise<T>): Promise<T> {
    const release = this.releases.releaseAtRequestStart();
    const scopedKey = scopeKey(release, key);
    const cached: Promise<T> | undefined = this.data[scopedKey];
    if (cached) {
      return cached;
    }

    // Stored before the load finishes, with no await in between, so a request that arrives while
    // it runs finds this promise and waits on it instead of starting a second load.
    const pending = load();
    this.data[scopedKey] = pending;

    // Removes this load's entry only if it is still there: a `clear()` or a later load may have
    // replaced it while this one ran, and that entry must stay.
    const evict = () => {
      if (this.data[scopedKey] === pending) {
        delete this.data[scopedKey];
      }
    };

    pending.then(
      // On success, the result is kept only if its release is still the latest. A load that
      // finished after a release switch, or ran while the release was unknown, is never looked
      // up again, so keeping it would only hold memory.
      () => {
        if (!isLatestRelease(release, this.releases)) {
          evict();
        }
      },
      // On failure, the rejected promise is dropped so the next request retries the load.
      evict,
    );
    return pending;
  }

  clear() {
    this.data = {};
  }
}

export const altCache = new AlternativeCache(trackerReleaseSource);

export function clearAllCaches() {
  cache.flushAll();
  altCache.clear();
}
