import NodeCache from 'node-cache';
import { DataRelease, dataReleaseTracker } from './data-release';
import {
  altCache,
  AlternativeCache,
  cache,
  CacheReleaseSource,
  clearAllCaches,
  ReleaseScopedCache,
} from './helpers';

const geneCacheKey = 'ENSG00000130203-gene';
const genesCacheKey = 'genes';
const genes = [{ hgnc_symbol: 'APOE' }];
const nextReleaseGenes = [{ hgnc_symbol: 'APOE' }, { hgnc_symbol: 'TREM2' }];
const initialRelease: DataRelease = { dataFile: 'syn13363290', dataVersion: '117' };
const nextRelease: DataRelease = { dataFile: 'syn13363290', dataVersion: '118' };

/** A release source the test switches by hand, like a request and the tracker would. */
function controlledReleases(release: DataRelease | null = initialRelease) {
  const releases = { atRequestStart: release, latest: release };
  const source: CacheReleaseSource = {
    releaseAtRequestStart: () => releases.atRequestStart,
    latestRelease: () => releases.latest,
  };
  return { releases, source };
}

function deferred<T>() {
  let resolve!: (value: T) => void;
  const promise = new Promise<T>((resolvePromise) => (resolve = resolvePromise));
  return { promise, resolve };
}

describe('clearAllCaches', () => {
  afterEach(() => jest.restoreAllMocks());

  it('empties both the node cache and the alternative cache', async () => {
    jest.spyOn(dataReleaseTracker, 'latestRelease').mockReturnValue(initialRelease);
    const loadGenes = jest.fn().mockResolvedValue(genes);
    cache.set(geneCacheKey, { hgnc_symbol: 'APOE' });
    await altCache.getOrLoad(genesCacheKey, loadGenes);

    clearAllCaches();
    await altCache.getOrLoad(genesCacheKey, loadGenes);

    expect(cache.get(geneCacheKey)).toBeUndefined();
    expect(loadGenes).toHaveBeenCalledTimes(2);
  });
});

describe('ReleaseScopedCache', () => {
  it('serves a value to requests in the release it was stored for', () => {
    const { source } = controlledReleases();
    const releaseScopedCache = new ReleaseScopedCache(new NodeCache(), source);

    releaseScopedCache.set(genesCacheKey, genes);

    expect(releaseScopedCache.get(genesCacheKey)).toEqual(genes);
  });

  it('misses the previous release once requests use a new one', () => {
    const { releases, source } = controlledReleases();
    const releaseScopedCache = new ReleaseScopedCache(new NodeCache(), source);
    releaseScopedCache.set(genesCacheKey, genes);

    releases.atRequestStart = nextRelease;
    releases.latest = nextRelease;

    expect(releaseScopedCache.get(genesCacheKey)).toBeUndefined();
  });

  it('does not store a value read by a request that started before a switch', () => {
    const { releases, source } = controlledReleases();
    const releaseScopedCache = new ReleaseScopedCache(new NodeCache(), source);
    releases.latest = nextRelease;

    expect(releaseScopedCache.set(genesCacheKey, genes)).toBe(false);
    expect(releaseScopedCache.get(genesCacheKey)).toBeUndefined();
  });

  it('neither stores nor serves values while the release is unknown', () => {
    const { source } = controlledReleases(null);
    const releaseScopedCache = new ReleaseScopedCache(new NodeCache(), source);

    expect(releaseScopedCache.set(genesCacheKey, genes)).toBe(false);
    expect(releaseScopedCache.get(genesCacheKey)).toBeUndefined();
  });
});

describe('AlternativeCache.getOrLoad', () => {
  it('runs one load for concurrent requests on the same key', async () => {
    const alternativeCache = new AlternativeCache(controlledReleases().source);
    const load = jest.fn().mockResolvedValue(genes);

    const results = await Promise.all([
      alternativeCache.getOrLoad(genesCacheKey, load),
      alternativeCache.getOrLoad(genesCacheKey, load),
    ]);

    expect(load).toHaveBeenCalledTimes(1);
    expect(results).toEqual([genes, genes]);
  });

  it('serves later requests from the cached result', async () => {
    const alternativeCache = new AlternativeCache(controlledReleases().source);
    const load = jest.fn().mockResolvedValue(genes);

    await alternativeCache.getOrLoad(genesCacheKey, load);
    const result = await alternativeCache.getOrLoad(genesCacheKey, load);

    expect(load).toHaveBeenCalledTimes(1);
    expect(result).toBe(genes);
  });

  it('retries the load on the next request after a load fails', async () => {
    const alternativeCache = new AlternativeCache(controlledReleases().source);
    const loadError = new Error('MongoDB connection lost');
    const load = jest.fn().mockRejectedValueOnce(loadError).mockResolvedValueOnce(genes);

    await expect(alternativeCache.getOrLoad(genesCacheKey, load)).rejects.toBe(loadError);
    const result = await alternativeCache.getOrLoad(genesCacheKey, load);

    expect(load).toHaveBeenCalledTimes(2);
    expect(result).toBe(genes);
  });

  it('reloads after the cache is cleared', async () => {
    const alternativeCache = new AlternativeCache(controlledReleases().source);
    const load = jest.fn().mockResolvedValue(genes);

    await alternativeCache.getOrLoad(genesCacheKey, load);
    alternativeCache.clear();
    await alternativeCache.getOrLoad(genesCacheKey, load);

    expect(load).toHaveBeenCalledTimes(2);
  });

  it('loads again for a request in a new release', async () => {
    const { releases, source } = controlledReleases();
    const alternativeCache = new AlternativeCache(source);
    const load = jest.fn().mockResolvedValueOnce(genes).mockResolvedValueOnce(nextReleaseGenes);

    await alternativeCache.getOrLoad(genesCacheKey, load);
    releases.atRequestStart = nextRelease;
    releases.latest = nextRelease;
    const result = await alternativeCache.getOrLoad(genesCacheKey, load);

    expect(load).toHaveBeenCalledTimes(2);
    expect(result).toBe(nextReleaseGenes);
  });

  it('does not keep the result of a load that finishes after a switch', async () => {
    const { releases, source } = controlledReleases();
    const alternativeCache = new AlternativeCache(source);
    const pendingLoad = deferred<typeof genes>();

    const result = alternativeCache.getOrLoad(genesCacheKey, () => pendingLoad.promise);
    releases.latest = nextRelease;
    pendingLoad.resolve(genes);
    await expect(result).resolves.toBe(genes);

    releases.latest = initialRelease;
    const reload = jest.fn().mockResolvedValue(genes);
    await alternativeCache.getOrLoad(genesCacheKey, reload);

    expect(reload).toHaveBeenCalledTimes(1);
  });

  it('shares a load while the release is unknown, without keeping its result', async () => {
    const alternativeCache = new AlternativeCache(controlledReleases(null).source);
    const load = jest.fn().mockResolvedValue(genes);

    await Promise.all([
      alternativeCache.getOrLoad(genesCacheKey, load),
      alternativeCache.getOrLoad(genesCacheKey, load),
    ]);
    await alternativeCache.getOrLoad(genesCacheKey, load);

    expect(load).toHaveBeenCalledTimes(2);
  });
});
