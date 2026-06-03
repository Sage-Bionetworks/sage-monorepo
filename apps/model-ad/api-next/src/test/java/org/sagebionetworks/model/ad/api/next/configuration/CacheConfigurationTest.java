package org.sagebionetworks.model.ad.api.next.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;

class CacheConfigurationTest {

  private static final long EXPECTED_MAXIMUM_SIZE = 10_000L;

  @Test
  @DisplayName("should configure maximumSize for every registered cache")
  void shouldConfigureMaximumSizeForEveryRegisteredCache() {
    CacheManager cacheManager = new CacheConfiguration().cacheManager();

    for (String name : new String[] {
      CacheNames.DISEASE_CORRELATION,
      CacheNames.MODEL_OVERVIEW,
      CacheNames.TRANSCRIPTOMICS,
      CacheNames.TRANSCRIPTOMICS_INDIVIDUAL,
      CacheNames.MODEL,
      CacheNames.COMPARISON_TOOL_CONFIG,
    }) {
      CaffeineCache springCache = (CaffeineCache) cacheManager.getCache(name);
      assertThat(springCache).as("cache '%s' should be registered", name).isNotNull();

      Cache<Object, Object> nativeCache = springCache.getNativeCache();
      long maximum = nativeCache.policy().eviction().orElseThrow().getMaximum();
      assertThat(maximum).as("cache '%s' maximumSize", name).isEqualTo(EXPECTED_MAXIMUM_SIZE);
    }
  }
}
