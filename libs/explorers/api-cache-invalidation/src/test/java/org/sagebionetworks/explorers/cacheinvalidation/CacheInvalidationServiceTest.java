package org.sagebionetworks.explorers.cacheinvalidation;

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

  @Mock
  private DataVersionProvider dataVersionProvider;

  @Mock
  private CacheManager cacheManager;

  @InjectMocks
  private CacheInvalidationService service;

  @Test
  @DisplayName("should record initial version and not clear caches on first poll")
  void shouldRecordInitialVersionAndNotClearCachesOnFirstPoll() {
    when(dataVersionProvider.getCurrentDataVersion()).thenReturn(Optional.of("1.0.0"));

    service.pollForDataVersionChange();

    verify(dataVersionProvider).getCurrentDataVersion();
    verifyNoInteractions(cacheManager);
  }

  @Test
  @DisplayName("should clear all caches when data version changes")
  void shouldClearAllCachesWhenDataVersionChanges() {
    when(dataVersionProvider.getCurrentDataVersion())
      .thenReturn(Optional.of("1.0.0"))
      .thenReturn(Optional.of("2.0.0"));
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
  @DisplayName("should not clear caches when data version unchanged")
  void shouldNotClearCachesWhenDataVersionUnchanged() {
    when(dataVersionProvider.getCurrentDataVersion()).thenReturn(Optional.of("1.0.0"));

    service.pollForDataVersionChange();
    service.pollForDataVersionChange();

    verify(dataVersionProvider, times(2)).getCurrentDataVersion();
    verifyNoInteractions(cacheManager);
  }

  @Test
  @DisplayName("should skip when data version is unavailable")
  void shouldSkipWhenDataVersionIsUnavailable() {
    when(dataVersionProvider.getCurrentDataVersion()).thenReturn(Optional.empty());

    service.pollForDataVersionChange();

    verify(dataVersionProvider).getCurrentDataVersion();
    verifyNoInteractions(cacheManager);
  }

  @Test
  @DisplayName("should clear caches only on the poll where version changes")
  void shouldClearCachesOnlyOnThePollWhereVersionChanges() {
    when(dataVersionProvider.getCurrentDataVersion())
      .thenReturn(Optional.of("1.0.0"))
      .thenReturn(Optional.of("1.0.0"))
      .thenReturn(Optional.of("2.0.0"));
    Cache cache = mock(Cache.class);
    when(cacheManager.getCacheNames()).thenReturn(List.of("cacheA"));
    when(cacheManager.getCache("cacheA")).thenReturn(cache);

    service.pollForDataVersionChange();
    service.pollForDataVersionChange();
    service.pollForDataVersionChange();

    verify(cache, times(1)).clear();
  }

  @Test
  @DisplayName("should skip null cache references returned from cache manager")
  void shouldSkipNullCacheReferencesReturnedFromCacheManager() {
    when(dataVersionProvider.getCurrentDataVersion())
      .thenReturn(Optional.of("1.0.0"))
      .thenReturn(Optional.of("2.0.0"));
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
    when(dataVersionProvider.getCurrentDataVersion()).thenThrow(new RuntimeException("db error"));

    assertThatCode(() -> service.pollForDataVersionChange()).doesNotThrowAnyException();
  }
}
