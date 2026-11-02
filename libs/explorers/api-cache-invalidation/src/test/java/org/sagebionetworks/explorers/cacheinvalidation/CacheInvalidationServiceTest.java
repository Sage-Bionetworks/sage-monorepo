package org.sagebionetworks.explorers.cacheinvalidation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.BiConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@ExtendWith(MockitoExtension.class)
class CacheInvalidationServiceTest {

  private static final DataRelease INITIAL_RELEASE = new DataRelease("syn73774820", "191");
  private static final DataRelease NEXT_VERSION = new DataRelease("syn73774820", "192");

  @Mock
  private DataReleaseTracker tracker;

  @Mock
  private CacheManager cacheManager;

  private BiConsumer<DataRelease, DataRelease> changeListener;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    new CacheInvalidationService(tracker, cacheManager);
    ArgumentCaptor<BiConsumer<DataRelease, DataRelease>> listener = ArgumentCaptor.forClass(
      BiConsumer.class
    );
    verify(tracker).addChangeListener(listener.capture());
    changeListener = listener.getValue();
  }

  @Test
  @DisplayName("should clear every cache once when the release changes")
  void shouldClearEveryCacheOnceWhenTheReleaseChanges() {
    Cache modelCache = mock(Cache.class);
    Cache searchCache = mock(Cache.class);
    when(cacheManager.getCacheNames()).thenReturn(List.of("model", "modelSearch"));
    when(cacheManager.getCache("model")).thenReturn(modelCache);
    when(cacheManager.getCache("modelSearch")).thenReturn(searchCache);

    changeListener.accept(INITIAL_RELEASE, NEXT_VERSION);

    verify(modelCache, times(1)).clear();
    verify(searchCache, times(1)).clear();
  }

  @Test
  @DisplayName("should not clear the caches until the release changes")
  void shouldNotClearTheCachesUntilTheReleaseChanges() {
    verify(cacheManager, never()).getCacheNames();
  }

  @Test
  @DisplayName("should skip a cache the cache manager no longer has")
  void shouldSkipCachesTheCacheManagerNoLongerHas() {
    Cache searchCache = mock(Cache.class);
    when(cacheManager.getCacheNames()).thenReturn(List.of("model", "modelSearch"));
    when(cacheManager.getCache("model")).thenReturn(null);
    when(cacheManager.getCache("modelSearch")).thenReturn(searchCache);

    changeListener.accept(INITIAL_RELEASE, NEXT_VERSION);

    verify(searchCache).clear();
  }
}
