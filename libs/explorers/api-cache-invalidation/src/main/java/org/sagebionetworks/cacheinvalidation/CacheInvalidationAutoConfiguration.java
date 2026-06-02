package org.sagebionetworks.cacheinvalidation;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@AutoConfiguration
@ConditionalOnBean({ DataVersionProvider.class, CacheManager.class })
@EnableScheduling
public class CacheInvalidationAutoConfiguration {

  @Bean
  public CacheInvalidationService cacheInvalidationService(
    DataVersionProvider dataVersionProvider,
    CacheManager cacheManager
  ) {
    return new CacheInvalidationService(dataVersionProvider, cacheManager);
  }
}
