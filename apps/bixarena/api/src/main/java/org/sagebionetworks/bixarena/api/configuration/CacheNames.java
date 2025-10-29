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
   * TTL: 5 minutes
   */
  public static final String PUBLIC_STATS = "bixarena:api:publicStats";

  /**
   * Cache key for the global public stats.
   */
  public static final String PUBLIC_STATS_KEY = "stats";
}
