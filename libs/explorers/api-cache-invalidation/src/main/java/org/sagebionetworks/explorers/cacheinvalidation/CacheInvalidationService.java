package org.sagebionetworks.explorers.cacheinvalidation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * Clears the API's caches once each time the tracker reads a new data release. Cache keys are
 * scoped to the release, so the previous release's entries can no longer be served after a switch;
 * the clear only frees the memory they hold, so one clear is enough.
 */
@Slf4j
public class CacheInvalidationService {

  private final CacheManager cacheManager;

  public CacheInvalidationService(DataReleaseTracker tracker, CacheManager cacheManager) {
    this.cacheManager = cacheManager;
    tracker.addChangeListener((previous, current) -> clearAllCaches());
  }

  private void clearAllCaches() {
    // Keeps the wording of the line that EX-164's "Data release cache clears" query matches.
    log.info("Data release changed; clearing caches to free the previous release's entries");
    for (String name : cacheManager.getCacheNames()) {
      Cache cache = cacheManager.getCache(name);
      if (cache != null) {
        cache.clear();
      }
    }
  }
}
