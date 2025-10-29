package org.sagebionetworks.bixarena.api.service;

import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.configuration.CacheNames;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * Service for managing cache invalidation of public stats.
 * Call invalidatePublicStats() when data changes that affect the stats
 * (e.g., new battles, evaluations, or user registrations).
 */
@Slf4j
@Service
public class StatsCacheService {

  /**
   * Invalidate the public stats cache.
   * Called when data changes that affect stats (battles, evaluations, users).
   */
  @CacheEvict(value = CacheNames.PUBLIC_STATS, key = "'" + CacheNames.PUBLIC_STATS_KEY + "'")
  public void invalidatePublicStats() {
    log.info("Public stats cache invalidated");
  }
}
