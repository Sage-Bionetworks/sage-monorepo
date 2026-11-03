import type { Request, Response } from 'express';
import {
  buildLegacyChunkModule,
  createLegacyChunkReloadHandler,
  LEGACY_CHUNK_RELOAD_GUARD_MS,
  LEGACY_CHUNK_RELOAD_LOG_MESSAGE,
  LEGACY_CHUNK_RELOAD_SKIPPED_MESSAGE,
  LEGACY_CHUNK_RELOADED_AT_KEY,
} from './legacy-chunk-reload';

const legacyChunkFileName = 'chunk-ABCD1234.js';
const currentChunkFileName = 'chunk-WXYZ5678.js';

describe('createLegacyChunkReloadHandler', () => {
  let log: jest.Mock;
  let next: jest.Mock;
  let res: { set: jest.Mock; type: jest.Mock; send: jest.Mock };

  function handle(fileName: string) {
    createLegacyChunkReloadHandler(new Set([legacyChunkFileName]), log)(
      { path: `/${fileName}` } as Request,
      res as unknown as Response,
      next,
    );
  }

  beforeEach(() => {
    log = jest.fn();
    next = jest.fn();
    res = { set: jest.fn(), type: jest.fn(), send: jest.fn() };
    res.set.mockReturnValue(res);
    res.type.mockReturnValue(res);
  });

  it('responds to a legacy chunk with the reload module as uncached JavaScript', () => {
    handle(legacyChunkFileName);

    expect(res.set).toHaveBeenCalledWith('Cache-Control', 'no-store');
    expect(res.type).toHaveBeenCalledWith('text/javascript');
    expect(res.send).toHaveBeenCalledWith(buildLegacyChunkModule());
    expect(next).not.toHaveBeenCalled();
  });

  it('logs each legacy chunk it serves, with the chunk name', () => {
    handle(legacyChunkFileName);

    expect(log).toHaveBeenCalledWith(`${LEGACY_CHUNK_RELOAD_LOG_MESSAGE}: ${legacyChunkFileName}`);
  });

  it('passes any other chunk on to the next handler without logging', () => {
    handle(currentChunkFileName);

    expect(next).toHaveBeenCalledTimes(1);
    expect(res.send).not.toHaveBeenCalled();
    expect(log).not.toHaveBeenCalled();
  });
});

describe('the legacy chunk module', () => {
  const asyncFunction = Object.getPrototypeOf(async () => undefined).constructor;
  const now = new Date('2026-11-03T12:00:00Z').getTime();
  let storedValues: Map<string, string>;
  let sessionStorage: Pick<Storage, 'getItem' | 'setItem'>;
  let location: { reload: jest.Mock };

  /** Runs the module the way the browser would, with this test's storage and location. */
  function runModule(): Promise<unknown> {
    const runLegacyChunkModule = new asyncFunction(
      'sessionStorage',
      'location',
      buildLegacyChunkModule(),
    );
    return runLegacyChunkModule(sessionStorage, location);
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

  it('reloads a tab it has not reloaded before and records when', () => {
    void runModule();

    expect(location.reload).toHaveBeenCalledTimes(1);
    expect(storedValues.get(LEGACY_CHUNK_RELOADED_AT_KEY)).toBe(String(now));
  });

  it('keeps the import pending while the page reloads, so the tab shows no error', async () => {
    const settled = jest.fn();

    runModule().then(settled, settled);
    await Promise.resolve();

    expect(settled).not.toHaveBeenCalled();
  });

  it('fails the import instead of reloading again within the guard window', async () => {
    storedValues.set(LEGACY_CHUNK_RELOADED_AT_KEY, String(now - LEGACY_CHUNK_RELOAD_GUARD_MS));

    await expect(runModule()).rejects.toEqual(new Error(LEGACY_CHUNK_RELOAD_SKIPPED_MESSAGE));
    expect(location.reload).not.toHaveBeenCalled();
  });

  it('reloads again once the guard window has passed', () => {
    storedValues.set(LEGACY_CHUNK_RELOADED_AT_KEY, String(now - LEGACY_CHUNK_RELOAD_GUARD_MS - 1));

    void runModule();

    expect(location.reload).toHaveBeenCalledTimes(1);
  });

  it('fails the import instead of reloading when session storage is unavailable', async () => {
    sessionStorage.getItem = () => {
      throw new DOMException('Access is denied', 'SecurityError');
    };

    await expect(runModule()).rejects.toEqual(new Error(LEGACY_CHUNK_RELOAD_SKIPPED_MESSAGE));
    expect(location.reload).not.toHaveBeenCalled();
  });
});
