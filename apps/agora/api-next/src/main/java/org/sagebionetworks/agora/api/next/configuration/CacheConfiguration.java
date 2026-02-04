package org.sagebionetworks.agora.api.next.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for the Agora API service.
 * Configures in-memory caching for comparison tool data queries to reduce MongoDB load and
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

    // Configure cache
    // Enable cache statistics for monitoring
    cacheManager.setCaffeine(Caffeine.newBuilder().recordStats());

    // Define the cache names used by query services
    cacheManager.setCacheNames(List.of(CacheNames.DATA_VERSION));

    return cacheManager;
  }
}
