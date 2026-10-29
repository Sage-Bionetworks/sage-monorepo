import { altCache, cache, clearAllCaches } from './helpers';

const geneCacheKey = 'ENSG00000130203-gene';
const genesCacheKey = 'genes';
const genes = [{ hgnc_symbol: 'APOE' }];

describe('clearAllCaches', () => {
  it('empties both the node cache and the alternative cache', () => {
    cache.set(geneCacheKey, { hgnc_symbol: 'APOE' });
    altCache.set(genesCacheKey, [{ hgnc_symbol: 'APOE' }]);

    clearAllCaches();

    expect(cache.get(geneCacheKey)).toBeUndefined();
    expect(altCache.get(genesCacheKey)).toBeUndefined();
  });
});

describe('altCache.getOrLoad', () => {
  afterEach(() => clearAllCaches());

  it('runs one load for concurrent requests on the same key', async () => {
    const load = jest.fn().mockResolvedValue(genes);

    const results = await Promise.all([
      altCache.getOrLoad(genesCacheKey, load),
      altCache.getOrLoad(genesCacheKey, load),
    ]);

    expect(load).toHaveBeenCalledTimes(1);
    expect(results).toEqual([genes, genes]);
  });

  it('serves later requests from the cached result', async () => {
    const load = jest.fn().mockResolvedValue(genes);

    await altCache.getOrLoad(genesCacheKey, load);
    const result = await altCache.getOrLoad(genesCacheKey, load);

    expect(load).toHaveBeenCalledTimes(1);
    expect(result).toBe(genes);
  });

  it('retries the load on the next request after a load fails', async () => {
    const loadError = new Error('MongoDB connection lost');
    const load = jest.fn().mockRejectedValueOnce(loadError).mockResolvedValueOnce(genes);

    await expect(altCache.getOrLoad(genesCacheKey, load)).rejects.toBe(loadError);
    const result = await altCache.getOrLoad(genesCacheKey, load);

    expect(load).toHaveBeenCalledTimes(2);
    expect(result).toBe(genes);
  });

  it('reloads after the caches are cleared', async () => {
    const load = jest.fn().mockResolvedValue(genes);

    await altCache.getOrLoad(genesCacheKey, load);
    clearAllCaches();
    await altCache.getOrLoad(genesCacheKey, load);

    expect(load).toHaveBeenCalledTimes(2);
  });
});
