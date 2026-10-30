package org.sagebionetworks.explorers.cacheinvalidation;

import java.util.Collection;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.SimpleCacheResolver;

/**
 * Resolves the caches for each cached method call, bound to the data release the tracker reports
 * when the call starts. Spring resolves the caches once per call and uses them for both the lookup
 * and the store, so a release switch during the call can't store the previous release's data under
 * the new release's key. Resolving per call rather than wrapping the `CacheManager` keeps the
 * manager's own caches, and the metrics bound to them, as they are.
 */
public class DataReleaseCacheResolver implements CacheResolver {

  private final CacheResolver delegate;
  private final DataReleaseTracker tracker;

  public DataReleaseCacheResolver(CacheManager cacheManager, DataReleaseTracker tracker) {
    this.delegate = new SimpleCacheResolver(cacheManager);
    this.tracker = tracker;
  }

  @Override
  public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
    DataRelease releaseAtCallStart = tracker.latestRelease().orElse(null);
    return delegate
      .resolveCaches(context)
      .stream()
      .map(cache -> new DataReleaseScopedCache(cache, releaseAtCallStart, tracker))
      .toList();
  }
}
