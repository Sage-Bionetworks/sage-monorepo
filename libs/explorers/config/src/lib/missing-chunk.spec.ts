import { isChunkLoadError, STALE_CHUNK_ERROR_MESSAGE } from '@sagebionetworks/explorers/sentry';
import type { Request, Response } from 'express';
import {
  buildStaleChunkModule,
  CHUNK_FILE_PATH_PATTERN,
  createMissingChunkHandler,
  STALE_CHUNK_RELOAD_GUARD_MS,
  STALE_CHUNK_RELOAD_PENDING_KEY,
  STALE_CHUNK_RELOADED_AT_KEY,
} from './missing-chunk';

const missingChunkPath = '/chunk-ABCD1234.js';

describe('CHUNK_FILE_PATH_PATTERN', () => {
  it('matches a chunk file name the production build produces', () => {
    expect(CHUNK_FILE_PATH_PATTERN.test(missingChunkPath)).toBe(true);
  });

  it.each([
    ['the main bundle', '/main-ABCD1234.js'],
    ['a lowercase hash', '/chunk-abcd1234.js'],
    ['a hash of the wrong length', '/chunk-ABC123.js'],
    ['a chunk in a subfolder', '/assets/chunk-ABCD1234.js'],
    ['a chunk source map', '/chunk-ABCD1234.js.map'],
  ])('does not match %s', (_description, path) => {
    expect(CHUNK_FILE_PATH_PATTERN.test(path)).toBe(false);
  });
});

describe('createMissingChunkHandler', () => {
  it('responds with the stale chunk module as uncached JavaScript', () => {
    const res = { set: jest.fn(), type: jest.fn(), send: jest.fn() };
    res.set.mockReturnValue(res);
    res.type.mockReturnValue(res);

    createMissingChunkHandler()({} as Request, res as unknown as Response);

    expect(res.set).toHaveBeenCalledWith('Cache-Control', 'no-store');
    expect(res.type).toHaveBeenCalledWith('text/javascript');
    expect(res.send).toHaveBeenCalledWith(buildStaleChunkModule());
  });
});

describe('the stale chunk module', () => {
  const asyncFunction = Object.getPrototypeOf(async () => undefined).constructor;
  const now = new Date('2026-10-20T12:00:00Z').getTime();
  let storedValues: Map<string, string>;
  let sessionStorage: Pick<Storage, 'getItem' | 'setItem'>;
  let location: { reload: jest.Mock };

  /** Runs the module the way the browser would, with this test's storage and location. */
  function runModule(): Promise<unknown> {
    const runStaleChunkModule = new asyncFunction(
      'sessionStorage',
      'location',
      buildStaleChunkModule(),
    );
    return runStaleChunkModule(sessionStorage, location);
  }

  beforeEach(() => {
    jest.useFakeTimers({ now });
    storedValues = new Map();
    sessionStorage = {
      getItem: (key) => storedValues.get(key) ?? null,
      setItem: (key, value) => storedValues.set(key, value),
    };
    location = { reload: jest.fn() };
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  it('reloads a tab it has not reloaded before and marks the reload for the app to record', () => {
    void runModule();

    expect(location.reload).toHaveBeenCalledTimes(1);
    expect(storedValues.get(STALE_CHUNK_RELOADED_AT_KEY)).toBe(String(now));
    expect(storedValues.get(STALE_CHUNK_RELOAD_PENDING_KEY)).toBe('true');
  });

  it('keeps the import pending while the page reloads, so the app shows no error', async () => {
    const settled = jest.fn();

    runModule().then(settled, settled);
    await Promise.resolve();

    expect(settled).not.toHaveBeenCalled();
  });

  it('throws a chunk-load error instead of reloading again within the guard window', async () => {
    storedValues.set(STALE_CHUNK_RELOADED_AT_KEY, String(now - STALE_CHUNK_RELOAD_GUARD_MS));

    const error = await runModule().catch((thrown: unknown) => thrown);

    expect(location.reload).not.toHaveBeenCalled();
    expect(error).toEqual(new Error(STALE_CHUNK_ERROR_MESSAGE));
    expect(isChunkLoadError(error)).toBe(true);
  });

  it('reloads again once the guard window has passed', () => {
    storedValues.set(STALE_CHUNK_RELOADED_AT_KEY, String(now - STALE_CHUNK_RELOAD_GUARD_MS - 1));

    void runModule();

    expect(location.reload).toHaveBeenCalledTimes(1);
  });

  it('throws instead of reloading when session storage is unavailable', async () => {
    sessionStorage.getItem = () => {
      throw new DOMException('Access is denied', 'SecurityError');
    };

    await expect(runModule()).rejects.toEqual(new Error(STALE_CHUNK_ERROR_MESSAGE));
    expect(location.reload).not.toHaveBeenCalled();
  });
});
