package org.sagebionetworks.bixarena.api.service;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.configuration.CacheNames;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

/**
 * Service for managing cache invalidation.
 * Call invalidateStatsForValidation() when a battle's effective validation changes,
 * as this affects public stats, user ranks, and quest contributors.
 */
@Slf4j
@Service
public class StatsCacheService {

  /**
   * Invalidate the public stats cache.
   * Called when data changes that affect stats (e.g., new user registrations).
   */
  @CacheEvict(value = CacheNames.PUBLIC_STATS, key = "'" + CacheNames.PUBLIC_STATS_KEY + "'")
  public void invalidatePublicStats() {
    log.info("Public stats cache invalidated");
  }

  /**
   * Invalidate all caches affected by a battle's effective validation changing.
   * This is the moment a battle starts (or stops) counting in stats queries,
   * so public stats, the user's rank, and quest contributors all become stale.
   *
   * @param userId the owner of the battle whose validation changed
   */
  @Caching(evict = {
    @CacheEvict(value = CacheNames.PUBLIC_STATS, key = "'" + CacheNames.PUBLIC_STATS_KEY + "'"),
    @CacheEvict(value = CacheNames.USER_RANKS, key = "#userId"),
    @CacheEvict(value = CacheNames.QUEST_CONTRIBUTORS, allEntries = true)
  })
  public void invalidateStatsForValidation(UUID userId) {
    log.info("Invalidated stats caches for validation change (userId={})", userId);
  }
}
