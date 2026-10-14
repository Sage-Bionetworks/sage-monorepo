package org.sagebionetworks.explorers.cacheinvalidation;

import java.util.concurrent.atomic.AtomicBoolean;
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
  private final AtomicReference<DataRelease> lastSeenRelease = new AtomicReference<>();
  private final AtomicBoolean followUpClearPending = new AtomicBoolean(false);

  @Scheduled(fixedDelay = POLL_DELAY_MS)
  public void pollForDataVersionChange() {
    try {
      if (followUpClearPending.getAndSet(false)) {
        log.info("clearing caches again after the data release change");
        clearAllCaches();
      }
      DataRelease currentRelease = dataVersionProvider.getCurrentDataRelease().orElse(null);
      if (currentRelease == null) {
        log.warn("data release unavailable; skipping cache invalidation check");
        return;
      }
      DataRelease previous = lastSeenRelease.getAndSet(currentRelease);
      if (previous == null) {
        log.info("Initial data release observed: {}", currentRelease);
        return;
      }
      if (!previous.equals(currentRelease)) {
        log.info("data release changed {} -> {}; clearing caches", previous, currentRelease);
        clearAllCaches();
        followUpClearPending.set(true);
      }
    } catch (Exception e) {
      log.error("Unexpected error during cache invalidation poll; will retry next interval", e);
    }
  }

  // Caches are cleared sequentially, not atomically, so a request can observe a partially
  // invalidated state, and a request that read the database before the change can cache its
  // result after the clear. The clear is therefore repeated on the poll after a change, which
  // drops any such entry within POLL_DELAY_MS.
  private void clearAllCaches() {
    for (String name : cacheManager.getCacheNames()) {
      Cache cache = cacheManager.getCache(name);
      if (cache != null) {
        cache.clear();
      }
    }
  }
}
