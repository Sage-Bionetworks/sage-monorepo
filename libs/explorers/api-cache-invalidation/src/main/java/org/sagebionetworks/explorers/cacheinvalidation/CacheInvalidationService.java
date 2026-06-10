package org.sagebionetworks.explorers.cacheinvalidation;

import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
public class CacheInvalidationService {

  private static final long POLL_DELAY_MS = 60_000L;

  private final DataVersionProvider dataVersionProvider;
  private final CacheManager cacheManager;
  private final AtomicReference<String> lastSeenVersion = new AtomicReference<>();

  @Scheduled(fixedDelay = POLL_DELAY_MS)
  public void pollForDataVersionChange() {
    try {
      String currentVersion = dataVersionProvider.getCurrentDataVersion().orElse(null);
      if (currentVersion == null) {
        log.warn("data version unavailable; skipping cache invalidation check");
        return;
      }
      String previous = lastSeenVersion.getAndSet(currentVersion);
      if (previous == null) {
        log.info("Initial data_version observed: {}", currentVersion);
        return;
      }
      if (!previous.equals(currentVersion)) {
        log.info("data_version changed {} -> {}; clearing caches", previous, currentVersion);
        clearAllCaches();
      }
    } catch (Exception e) {
      log.error("Unexpected error during cache invalidation poll; will retry next interval", e);
    }
  }

  // Caches are cleared sequentially, not atomically — a request can observe
  // a partially-invalidated state during this window. Acceptable because
  // data version changes are rare events, and any stale entry is corrected
  // on the next poll cycle defined by POLL_DELAY_MS.
  private void clearAllCaches() {
    for (String name : cacheManager.getCacheNames()) {
      Cache cache = cacheManager.getCache(name);
      if (cache != null) {
        cache.clear();
      }
    }
  }
}
