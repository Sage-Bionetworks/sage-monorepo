package org.sagebionetworks.explorers.cacheinvalidation;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

  @Bean
  public DataReleaseTracker dataReleaseTracker(DataVersionProvider dataVersionProvider) {
    return new DataReleaseTracker(dataVersionProvider);
  }

  /**
   * Gives every `@Cacheable` call caches bound to the data release it started with. An application
   * that defines its own `CachingConfigurer` must return a `DataReleaseCacheResolver` from it.
   */
  @Bean
  public CachingConfigurer dataReleaseCachingConfigurer(
    CacheManager cacheManager,
    DataReleaseTracker dataReleaseTracker
  ) {
    return new CachingConfigurer() {
      @Override
      public CacheResolver cacheResolver() {
        return new DataReleaseCacheResolver(cacheManager, dataReleaseTracker);
      }
    };
  }

  /** Only when the application has the actuator, which the library doesn't require. */
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnClass(HealthIndicator.class)
  static class DataReleaseHealthConfiguration {

    @Bean
    public DataReleaseHealthIndicator dataReleaseHealthIndicator(DataReleaseTracker tracker) {
      return new DataReleaseHealthIndicator(tracker);
    }
  }
}
