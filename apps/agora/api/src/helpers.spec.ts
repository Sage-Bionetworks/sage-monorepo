import { altCache, cache, clearAllCaches } from './helpers';

const geneCacheKey = 'ENSG00000130203-gene';
const genesCacheKey = 'genes';

describe('clearAllCaches', () => {
  it('empties both the node cache and the alternative cache', () => {
    cache.set(geneCacheKey, { hgnc_symbol: 'APOE' });
    altCache.set(genesCacheKey, [{ hgnc_symbol: 'APOE' }]);

    clearAllCaches();

    expect(cache.get(geneCacheKey)).toBeUndefined();
    expect(altCache.get(genesCacheKey)).toBeUndefined();
  });
});
