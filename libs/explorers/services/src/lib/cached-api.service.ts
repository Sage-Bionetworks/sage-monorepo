import { Observable, shareReplay } from 'rxjs';

export class CachedApi<T, K = string> {
  private cache = new Map<K, Observable<T>>();

  constructor(private fetcher: (key: K) => Observable<T>) {}

  get(key: K): Observable<T> {
    let cached = this.cache.get(key);
    if (!cached) {
      cached = this.fetcher(key).pipe(shareReplay(1));
      this.cache.set(key, cached);
    }
    return cached;
  }

  clear(key?: K) {
    if (key !== undefined) {
      this.cache.delete(key);
    } else {
      this.cache.clear();
    }
  }
}
