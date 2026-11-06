package org.sagebionetworks.qtl.api.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import org.sagebionetworks.explorers.cacheinvalidation.DataRelease;
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

    cacheManager.setCaffeine(Caffeine.newBuilder().maximumSize(10_000).recordStats());

    cacheManager.setCacheNames(List.of(CacheNames.COMPARISON_TOOL_CONFIG));

    return cacheManager;
  }

  @Bean
  public DataVersionProvider dataVersionProvider(DataVersionRepository repository) {
    return () ->
      repository
        .findFirstBy()
        .map(document -> new DataRelease(document.getDataFile(), document.getDataVersion()));
  }
}
