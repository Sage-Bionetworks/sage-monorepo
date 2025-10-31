package org.sagebionetworks.bixarena.api.configuration;

/**
 * Constants for cache names used in the BixArena API service.
 * Cache names follow the convention: bixarena:api:{cache-type}
 */
public final class CacheNames {

  private CacheNames() {
    // Utility class, prevent instantiation
  }

  /**
   * Cache for public statistics (total users, battles, models evaluated).
   * Key format: bixarena:api:publicStats::stats
   * TTL: 1 minute (to keep battle/model counts fresh during early platform growth)
   * Invalidation: Event-driven on new user registration
   */
  public static final String PUBLIC_STATS = "bixarena:api:publicStats";

  /**
   * Cache key for the global public stats.
   */
  public static final String PUBLIC_STATS_KEY = "stats";

  /**
   * Cache for user ranks based on completed battles.
   * Key format: bixarena:api:userRanks::{userId}
   * TTL: 5 minutes (balances freshness with query performance)
   * Invalidation: Event-driven when user completes a battle
   */
  public static final String USER_RANKS = "bixarena:api:userRanks";
}
