import { DataReleaseTracker } from './data-release';

/**
 * Clears the API's in-memory caches once each time the tracker reads a new data release. Cache keys
 * are scoped to the release, so the previous release's entries can no longer be served after a
 * switch; the clear only frees the memory they hold, so one clear is enough. Returns a function
 * that stops clearing.
 */
export function clearCachesOnReleaseChange(
  tracker: DataReleaseTracker,
  clearCaches: () => void,
): () => void {
  return tracker.onChange(() => {
    // Keeps the wording of the line that EX-164's "Data release cache clears" query matches.
    console.info("Data release changed; clearing caches to free the previous release's entries");
    clearCaches();
  });
}
