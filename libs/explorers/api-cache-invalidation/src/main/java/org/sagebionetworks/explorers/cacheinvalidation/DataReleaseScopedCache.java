package org.sagebionetworks.explorers.cacheinvalidation;

import java.util.concurrent.Callable;
import org.springframework.cache.Cache;

/**
 * A view of one cache, bound to the data release a cached method call started with. Every key is
 * scoped to that release, so after a release switch calls miss the previous release's entries
 * instead of serving them. A value is only stored while that release is still the tracker's latest
 * one: a call that started before a switch, or while the release is unknown, doesn't keep what it
 * read.
 */
class DataReleaseScopedCache implements Cache {

  /** A key scoped to the release it was cached for. */
  record DataReleaseScopedKey(DataRelease release, Object key) {}

  private final Cache target;
  private final DataRelease releaseAtCallStart;
  private final DataReleaseTracker tracker;

  DataReleaseScopedCache(Cache target, DataRelease releaseAtCallStart, DataReleaseTracker tracker) {
    this.target = target;
    this.releaseAtCallStart = releaseAtCallStart;
    this.tracker = tracker;
  }

  @Override
  public String getName() {
    return target.getName();
  }

  @Override
  public Object getNativeCache() {
    return target.getNativeCache();
  }

  @Override
  public ValueWrapper get(Object key) {
    return releaseAtCallStart == null ? null : target.get(scopedKey(key));
  }

  @Override
  public <T> T get(Object key, Class<T> type) {
    return releaseAtCallStart == null ? null : target.get(scopedKey(key), type);
  }

  @Override
  public <T> T get(Object key, Callable<T> valueLoader) {
    if (!isLatestRelease()) {
      return loadWithoutCaching(key, valueLoader);
    }
    T value = target.get(scopedKey(key), valueLoader);
    // The target stores what the loader returns; drop it if the release changed during the load.
    if (!isLatestRelease()) {
      target.evict(scopedKey(key));
    }
    return value;
  }

  @Override
  public void put(Object key, Object value) {
    if (isLatestRelease()) {
      target.put(scopedKey(key), value);
    }
  }

  @Override
  public ValueWrapper putIfAbsent(Object key, Object value) {
    return isLatestRelease() ? target.putIfAbsent(scopedKey(key), value) : null;
  }

  @Override
  public void evict(Object key) {
    if (releaseAtCallStart != null) {
      target.evict(scopedKey(key));
    }
  }

  @Override
  public void clear() {
    target.clear();
  }

  private DataReleaseScopedKey scopedKey(Object key) {
    return new DataReleaseScopedKey(releaseAtCallStart, key);
  }

  /** Never true while either release is unknown. */
  private boolean isLatestRelease() {
    return (
      releaseAtCallStart != null &&
      tracker.latestRelease().map(releaseAtCallStart::equals).orElse(false)
    );
  }

  private static <T> T loadWithoutCaching(Object key, Callable<T> valueLoader) {
    try {
      return valueLoader.call();
    } catch (Exception e) {
      throw new ValueRetrievalException(key, valueLoader, e);
    }
  }
}
