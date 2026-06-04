package org.sagebionetworks.qtl.api.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.sagebionetworks.explorers.cacheinvalidation.DataVersionProvider;
import org.sagebionetworks.qtl.api.model.document.DataVersionDocument;
import org.sagebionetworks.qtl.api.model.repository.DataVersionRepository;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for the QTL API service.
 * Configures in-memory caching to reduce MongoDB load and improve response times.
 * Uses Caffeine cache for high-performance local caching.
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

  /**
   * Configure cache manager with size limits.
   */
  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();

    cacheManager.setCaffeine(Caffeine.newBuilder().recordStats());

    return cacheManager;
  }

  @Bean
  public DataVersionProvider dataVersionProvider(DataVersionRepository repository) {
    return () -> repository.findFirstBy().map(DataVersionDocument::getDataVersion);
  }
}
