package org.sagebionetworks.model.ad.api.next.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for the Model-AD API service.
 *
 * <p>Configures in-memory caching for comparison tool data queries to reduce MongoDB load and
 * improve response times. Uses Caffeine cache for high-performance local caching.
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

  /**
   * Configure cache manager with size limits for comparison tool data.
   */
  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();

    // Configure cache with expiration and size limits
    cacheManager.setCaffeine(
      Caffeine.newBuilder()
        .maximumSize(1000) // Limit each cache to prevent memory issues
        .recordStats()
    ); // Enable cache statistics for monitoring

    // Define the cache names used by query services
    cacheManager.setCacheNames(java.util.Arrays.asList("diseaseCorrelation", "modelOverview"));

    return cacheManager;
  }
}
