package org.sagebionetworks.qtl.api.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;

class CacheConfigurationTest {

  @Test
  @DisplayName("should configure maximumSize for every registered cache")
  void shouldConfigureMaximumSizeForEveryRegisteredCache() {
    // when
    CacheManager cacheManager = new CacheConfiguration().cacheManager();

    // then
    for (String name : new String[] {
      CacheNames.COMPARISON_TOOL_CONFIG,
      CacheNames.EQTL,
      CacheNames.EQTL_PHENOTYPE,
    }) {
      CaffeineCache springCache = (CaffeineCache) cacheManager.getCache(name);
      assertThat(springCache).as("cache '%s' should be registered", name).isNotNull();

      Cache<Object, Object> nativeCache = springCache.getNativeCache();
      long maximum = nativeCache.policy().eviction().orElseThrow().getMaximum();
      assertThat(maximum)
        .as("cache '%s' maximumSize", name)
        .isEqualTo(CacheConfiguration.MAXIMUM_CACHE_SIZE);
    }
  }
}
