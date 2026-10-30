package org.sagebionetworks.explorers.cacheinvalidation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Tag("integration")
class DataReleaseCacheResolverIntegrationTest {

  private static final DataRelease INITIAL_RELEASE = new DataRelease("syn73774820", "221");
  private static final DataRelease NEXT_VERSION = new DataRelease("syn73774820", "222");
  private static final String CACHE_NAME = "model";
  private static final String MODEL_NAME = "LOAD1";

  /** The release the test's `dataversion` document holds, which the tracker reads. */
  static final AtomicReference<DataRelease> storedRelease = new AtomicReference<>();

  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
    .withConfiguration(AutoConfigurations.of(CacheInvalidationAutoConfiguration.class))
    .withUserConfiguration(ApplicationConfiguration.class);

  @Configuration(proxyBeanMethods = false)
  @EnableCaching
  static class ApplicationConfiguration {

    @Bean
    CacheManager cacheManager() {
      return new ConcurrentMapCacheManager(CACHE_NAME);
    }

    @Bean
    DataVersionProvider dataVersionProvider() {
      return () -> Optional.ofNullable(storedRelease.get());
    }

    @Bean
    ModelService modelService() {
      return new ModelService();
    }
  }

  /**
   * A cached service whose `beforeReturning` hook runs inside the cached call. The bean is a caching
   * proxy, which forwards method calls but not field access, so its state is read through methods.
   */
  static class ModelService {

    private final AtomicInteger loads = new AtomicInteger();
    private Runnable beforeReturning = () -> {};

    @Cacheable(cacheNames = CACHE_NAME, key = "#p0")
    public String getModel(String name) {
      loads.incrementAndGet();
      beforeReturning.run();
      return name + " model";
    }

    public int loadCount() {
      return loads.get();
    }

    public void runBeforeReturning(Runnable hook) {
      beforeReturning = hook;
    }
  }

  private static void switchStoredReleaseTo(DataRelease release, DataReleaseTracker tracker) {
    storedRelease.set(release);
    tracker.refresh();
  }

  @Test
  @DisplayName("should serve a repeated call from the cache within one release")
  void shouldServeARepeatedCallFromTheCacheWithinOneRelease() {
    storedRelease.set(INITIAL_RELEASE);
    contextRunner.run(context -> {
      ModelService modelService = context.getBean(ModelService.class);

      modelService.getModel(MODEL_NAME);
      modelService.getModel(MODEL_NAME);

      assertThat(modelService.loadCount()).isEqualTo(1);
    });
  }

  @Test
  @DisplayName("should load again in a new release instead of serving the previous one")
  void shouldLoadAgainInANewReleaseInsteadOfServingThePreviousOne() {
    storedRelease.set(INITIAL_RELEASE);
    contextRunner.run(context -> {
      ModelService modelService = context.getBean(ModelService.class);
      modelService.getModel(MODEL_NAME);

      switchStoredReleaseTo(NEXT_VERSION, context.getBean(DataReleaseTracker.class));
      modelService.getModel(MODEL_NAME);

      assertThat(modelService.loadCount()).isEqualTo(2);
    });
  }

  @Test
  @DisplayName("should not store a call's result when the release switches during the call")
  void shouldNotStoreACallsResultWhenTheReleaseSwitchesDuringTheCall() {
    storedRelease.set(INITIAL_RELEASE);
    contextRunner.run(context -> {
      ModelService modelService = context.getBean(ModelService.class);
      DataReleaseTracker tracker = context.getBean(DataReleaseTracker.class);
      modelService.runBeforeReturning(() -> switchStoredReleaseTo(NEXT_VERSION, tracker));

      modelService.getModel(MODEL_NAME);

      ConcurrentMapCache cache = (ConcurrentMapCache) context
        .getBean(CacheManager.class)
        .getCache(CACHE_NAME);
      assertThat(cache.getNativeCache()).isEmpty();
    });
  }

  @Test
  @DisplayName("should keep the application's cache manager unwrapped so cache metrics still bind")
  void shouldKeepTheApplicationsCacheManagerUnwrappedSoCacheMetricsStillBind() {
    storedRelease.set(INITIAL_RELEASE);
    contextRunner.run(context ->
      assertThat(context.getBean(CacheManager.class).getCache(CACHE_NAME)).isInstanceOf(
        ConcurrentMapCache.class
      )
    );
  }
}
