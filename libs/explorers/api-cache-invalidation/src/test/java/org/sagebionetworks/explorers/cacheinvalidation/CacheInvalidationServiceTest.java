package org.sagebionetworks.explorers.cacheinvalidation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@ExtendWith(MockitoExtension.class)
class CacheInvalidationServiceTest {

  private static final DataRelease CURRENT_RELEASE = new DataRelease("syn73774820", "191");
  private static final DataRelease NEXT_VERSION = new DataRelease("syn73774820", "192");
  private static final DataRelease NEXT_FILE = new DataRelease("syn74000000", "191");

  @Mock
  private DataVersionProvider dataVersionProvider;

  @Mock
  private CacheManager cacheManager;

  @InjectMocks
  private CacheInvalidationService service;

  private Cache stubSingleCache() {
    Cache cache = mock(Cache.class);
    when(cacheManager.getCacheNames()).thenReturn(List.of("cacheA"));
    when(cacheManager.getCache("cacheA")).thenReturn(cache);
    return cache;
  }

  @Test
  @DisplayName("should record initial release and not clear caches on first poll")
  void shouldRecordInitialReleaseAndNotClearCachesOnFirstPoll() {
    when(dataVersionProvider.getCurrentDataRelease()).thenReturn(Optional.of(CURRENT_RELEASE));

    service.pollForDataVersionChange();

    verify(dataVersionProvider).getCurrentDataRelease();
    verifyNoInteractions(cacheManager);
  }

  @Test
  @DisplayName("should clear all caches when data version changes")
  void shouldClearAllCachesWhenDataVersionChanges() {
    when(dataVersionProvider.getCurrentDataRelease())
      .thenReturn(Optional.of(CURRENT_RELEASE))
      .thenReturn(Optional.of(NEXT_VERSION));
    Cache cacheA = mock(Cache.class);
    Cache cacheB = mock(Cache.class);
    when(cacheManager.getCacheNames()).thenReturn(List.of("cacheA", "cacheB"));
    when(cacheManager.getCache("cacheA")).thenReturn(cacheA);
    when(cacheManager.getCache("cacheB")).thenReturn(cacheB);

    service.pollForDataVersionChange();
    service.pollForDataVersionChange();

    verify(cacheA).clear();
    verify(cacheB).clear();
  }

  @Test
  @DisplayName("should clear caches when only the data file changes")
  void shouldClearCachesWhenOnlyTheDataFileChanges() {
    when(dataVersionProvider.getCurrentDataRelease())
      .thenReturn(Optional.of(CURRENT_RELEASE))
      .thenReturn(Optional.of(NEXT_FILE));
    Cache cache = stubSingleCache();

    service.pollForDataVersionChange();
    service.pollForDataVersionChange();

    verify(cache).clear();
  }

  @Test
  @DisplayName("should not clear caches when data release unchanged")
  void shouldNotClearCachesWhenDataReleaseUnchanged() {
    when(dataVersionProvider.getCurrentDataRelease()).thenReturn(Optional.of(CURRENT_RELEASE));

    service.pollForDataVersionChange();
    service.pollForDataVersionChange();

    verify(dataVersionProvider, times(2)).getCurrentDataRelease();
    verifyNoInteractions(cacheManager);
  }

  @Test
  @DisplayName("should skip when data release is unavailable")
  void shouldSkipWhenDataReleaseIsUnavailable() {
    when(dataVersionProvider.getCurrentDataRelease()).thenReturn(Optional.empty());

    service.pollForDataVersionChange();

    verify(dataVersionProvider).getCurrentDataRelease();
    verifyNoInteractions(cacheManager);
  }

  @Test
  @DisplayName("should clear caches again on the poll after release change")
  void shouldClearCachesAgainOnThePollAfterReleaseChange() {
    when(dataVersionProvider.getCurrentDataRelease())
      .thenReturn(Optional.of(CURRENT_RELEASE))
      .thenReturn(Optional.of(NEXT_VERSION))
      .thenReturn(Optional.of(NEXT_VERSION));
    final Cache cache = stubSingleCache();

    service.pollForDataVersionChange();
    service.pollForDataVersionChange();
    service.pollForDataVersionChange();

    verify(cache, times(2)).clear();
  }

  @Test
  @DisplayName("should stop clearing caches once the follow-up clear has run")
  void shouldStopClearingCachesOnceTheFollowUpClearHasRun() {
    when(dataVersionProvider.getCurrentDataRelease())
      .thenReturn(Optional.of(CURRENT_RELEASE))
      .thenReturn(Optional.of(CURRENT_RELEASE))
      .thenReturn(Optional.of(NEXT_VERSION))
      .thenReturn(Optional.of(NEXT_VERSION))
      .thenReturn(Optional.of(NEXT_VERSION));
    Cache cache = stubSingleCache();

    for (int poll = 0; poll < 5; poll++) {
      service.pollForDataVersionChange();
    }

    verify(cache, times(2)).clear();
  }

  @Test
  @DisplayName("should skip null cache references returned from cache manager")
  void shouldSkipNullCacheReferencesReturnedFromCacheManager() {
    when(dataVersionProvider.getCurrentDataRelease())
      .thenReturn(Optional.of(CURRENT_RELEASE))
      .thenReturn(Optional.of(NEXT_VERSION));
    Cache cacheB = mock(Cache.class);
    when(cacheManager.getCacheNames()).thenReturn(List.of("cacheA", "cacheB"));
    when(cacheManager.getCache("cacheA")).thenReturn(null);
    when(cacheManager.getCache("cacheB")).thenReturn(cacheB);

    service.pollForDataVersionChange();
    service.pollForDataVersionChange();

    verify(cacheB).clear();
  }

  @Test
  @DisplayName("should not propagate exception thrown by data version provider")
  void shouldNotPropagateExceptionThrownByDataVersionProvider() {
    when(dataVersionProvider.getCurrentDataRelease()).thenThrow(new RuntimeException("db error"));

    assertThatCode(() -> service.pollForDataVersionChange()).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("should describe data release the way the footer shows it")
  void shouldDescribeDataReleaseTheWayTheFooterShowsIt() {
    assertThat(CURRENT_RELEASE).hasToString("syn73774820-v191");
  }
}
