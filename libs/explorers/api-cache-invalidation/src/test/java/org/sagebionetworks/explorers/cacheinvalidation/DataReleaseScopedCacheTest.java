package org.sagebionetworks.explorers.cacheinvalidation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.explorers.cacheinvalidation.DataReleaseScopedCache.DataReleaseScopedKey;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

@ExtendWith(MockitoExtension.class)
class DataReleaseScopedCacheTest {

  private static final DataRelease INITIAL_RELEASE = new DataRelease("syn73774820", "221");
  private static final DataRelease NEXT_VERSION = new DataRelease("syn73774820", "222");
  private static final String MODEL_KEY = "LOAD1";
  private static final String MODEL = "LOAD1 model";

  @Mock
  private DataReleaseTracker tracker;

  private ConcurrentMapCache target;

  @BeforeEach
  void setUp() {
    target = new ConcurrentMapCache("model");
  }

  private Cache scopedTo(DataRelease releaseAtCallStart) {
    return new DataReleaseScopedCache(target, releaseAtCallStart, tracker);
  }

  private Map<Object, Object> storedEntries() {
    return target.getNativeCache();
  }

  @Test
  @DisplayName("should store and serve a value under the release the call started with")
  void shouldStoreAndServeValuesUnderTheReleaseTheCallStartedWith() {
    when(tracker.latestRelease()).thenReturn(Optional.of(INITIAL_RELEASE));
    Cache cache = scopedTo(INITIAL_RELEASE);

    cache.put(MODEL_KEY, MODEL);

    assertThat(cache.get(MODEL_KEY, String.class)).isEqualTo(MODEL);
    assertThat(storedEntries()).containsOnlyKeys(
      new DataReleaseScopedKey(INITIAL_RELEASE, MODEL_KEY)
    );
  }

  @Test
  @DisplayName("should miss the previous release's value when a call starts in a new release")
  void shouldMissThePreviousReleasesValueWhenCallStartsInNewRelease() {
    when(tracker.latestRelease()).thenReturn(Optional.of(INITIAL_RELEASE));
    scopedTo(INITIAL_RELEASE).put(MODEL_KEY, MODEL);

    Cache cacheInNextRelease = scopedTo(NEXT_VERSION);

    assertThat(cacheInNextRelease.get(MODEL_KEY)).isNull();
  }

  @Test
  @DisplayName("should not store a value when the release changed after the call started")
  void shouldNotStoreValueWhenTheReleaseChangedAfterTheCallStarted() {
    when(tracker.latestRelease()).thenReturn(Optional.of(NEXT_VERSION));

    scopedTo(INITIAL_RELEASE).put(MODEL_KEY, MODEL);

    assertThat(storedEntries()).isEmpty();
  }

  @Test
  @DisplayName("should neither store nor serve values when the release is unknown")
  void shouldNeitherStoreNorServeValuesWhenTheReleaseIsUnknown() {
    Cache cache = scopedTo(null);

    cache.put(MODEL_KEY, MODEL);

    assertThat(cache.get(MODEL_KEY)).isNull();
    assertThat(storedEntries()).isEmpty();
  }

  @Test
  @DisplayName("should load once and keep the value when loading in the latest release")
  void shouldLoadOnceAndKeepTheValueWhenLoadingInTheLatestRelease() {
    when(tracker.latestRelease()).thenReturn(Optional.of(INITIAL_RELEASE));
    AtomicInteger loads = new AtomicInteger();
    Callable<String> loader = () -> {
      loads.incrementAndGet();
      return MODEL;
    };
    Cache cache = scopedTo(INITIAL_RELEASE);

    cache.get(MODEL_KEY, loader);
    String value = cache.get(MODEL_KEY, loader);

    assertThat(value).isEqualTo(MODEL);
    assertThat(loads).hasValue(1);
  }

  @Test
  @DisplayName("should not keep a loaded value when the release changes during the load")
  void shouldNotKeepLoadedValueWhenTheReleaseChangesDuringTheLoad() {
    when(tracker.latestRelease()).thenReturn(
      Optional.of(INITIAL_RELEASE),
      Optional.of(NEXT_VERSION)
    );

    String value = scopedTo(INITIAL_RELEASE).get(MODEL_KEY, () -> MODEL);

    assertThat(value).isEqualTo(MODEL);
    assertThat(storedEntries()).isEmpty();
  }

  @Test
  @DisplayName("should load without caching when the release is unknown")
  void shouldLoadWithoutCachingWhenTheReleaseIsUnknown() {
    String value = scopedTo(null).get(MODEL_KEY, () -> MODEL);

    assertThat(value).isEqualTo(MODEL);
    assertThat(storedEntries()).isEmpty();
  }

  @Test
  @DisplayName("should rethrow a failed load as a value retrieval exception when not caching")
  void shouldRethrowFailedLoadAsValueRetrievalExceptionWhenNotCaching() {
    Callable<String> failingLoader = () -> {
      throw new IllegalStateException("MongoDB connection lost");
    };

    assertThatThrownBy(() -> scopedTo(null).get(MODEL_KEY, failingLoader)).isInstanceOf(
      Cache.ValueRetrievalException.class
    );
  }

  @Test
  @DisplayName("should clear every release's entries when cleared")
  void shouldClearEveryReleasesEntriesWhenCleared() {
    target.put(new DataReleaseScopedKey(INITIAL_RELEASE, MODEL_KEY), MODEL);
    target.put(new DataReleaseScopedKey(NEXT_VERSION, MODEL_KEY), MODEL);

    scopedTo(NEXT_VERSION).clear();

    assertThat(storedEntries()).isEmpty();
  }
}
