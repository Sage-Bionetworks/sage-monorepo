package org.sagebionetworks.openchallenges.auth.service.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for the auth service.
 *
 * Configures caching for frequently accessed data like user lookups
 * to reduce database queries and improve performance.
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

  /**
   * Configure cache manager with expiration policy for user lookups.
   *
   * Uses Caffeine cache with automatic expiration to prevent stale data issues.
   * Cache entries expire after 15 minutes to balance performance with data freshness.
   */
  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();

    // Configure cache with expiration and size limits
    cacheManager.setCaffeine(
      Caffeine.newBuilder()
        .expireAfterWrite(15, TimeUnit.MINUTES) // Data expires after 15 minutes
        .maximumSize(1000) // Limit cache size to prevent memory issues
        .recordStats()
    ); // Enable cache statistics

    // Define the cache names used by UserLookupService
    cacheManager.setCacheNames(
      java.util.Arrays.asList("userBySubject", "userById", "userByUsername", "userByClientId")
    );

    return cacheManager;
  }
}
