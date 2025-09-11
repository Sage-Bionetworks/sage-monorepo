package org.sagebionetworks.openchallenges.auth.service.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
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
   * Configure simple in-memory cache manager for user lookups.
   *
   * Uses ConcurrentMapCacheManager for simplicity and to avoid external dependencies.
   * In a production environment, you might want to use a more sophisticated cache
   * like Caffeine or Redis with expiration policies.
   */
  @Bean
  public CacheManager cacheManager() {
    ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();

    // Define the cache names used by UserLookupService
    cacheManager.setCacheNames(
      java.util.Arrays.asList("userBySubject", "userById", "userByUsername", "userByClientId")
    );

    return cacheManager;
  }
}
