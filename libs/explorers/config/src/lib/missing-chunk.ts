import { STALE_CHUNK_ERROR_MESSAGE } from '@sagebionetworks/explorers/sentry';
import type { Request, Response } from 'express';

/**
 * The file name the production build gives every lazy-loaded chunk. Each build gives its chunks
 * new names, so a tab opened before a deploy requests names the new build does not have.
 */
export const CHUNK_FILE_PATH_PATTERN = /^\/chunk-[A-Z0-9]{8}\.js$/;

/** When the stale chunk module last reloaded this tab, in `sessionStorage` (one per tab). */
export const STALE_CHUNK_RELOADED_AT_KEY = 'staleChunkReloadedAt';
/** Set by the stale chunk module just before it reloads, so the reloaded app can record it. */
export const STALE_CHUNK_RELOAD_PENDING_KEY = 'staleChunkReloadPending';
/**
 * How long after a reload the stale chunk module refuses to reload the tab again. During a rolling
 * deploy, the reloaded page can still be served by an old task and request a chunk a new task does
 * not have, which would otherwise reload the tab over and over.
 */
export const STALE_CHUNK_RELOAD_GUARD_MS = 60 * 1000;

/**
 * The module sent in place of a missing chunk. It reloads the tab, so the tab gets the current
 * build, unless it already did within `STALE_CHUNK_RELOAD_GUARD_MS` or `sessionStorage` is
 * unavailable, in which case it throws, and the app handles the failure like any chunk that did
 * not load. While the page unloads, the import is kept pending, so the app does not show an error
 * for a navigation the reload is about to replace.
 */
export function buildStaleChunkModule(): string {
  return `
const reloadedAtKey = ${JSON.stringify(STALE_CHUNK_RELOADED_AT_KEY)};
const reloadPendingKey = ${JSON.stringify(STALE_CHUNK_RELOAD_PENDING_KEY)};
let shouldReload = false;
try {
  const reloadedAt = Number(sessionStorage.getItem(reloadedAtKey));
  shouldReload = !reloadedAt || Date.now() - reloadedAt > ${STALE_CHUNK_RELOAD_GUARD_MS};
  if (shouldReload) {
    sessionStorage.setItem(reloadedAtKey, String(Date.now()));
    sessionStorage.setItem(reloadPendingKey, 'true');
  }
} catch {
  shouldReload = false;
}
if (shouldReload) {
  location.reload();
  await new Promise(() => undefined);
}
throw new Error(${JSON.stringify(STALE_CHUNK_ERROR_MESSAGE)});
`;
}

/**
 * Answers a request for a chunk file the current build does not have with the stale chunk module,
 * instead of the HTML page the server would otherwise render, which the browser cannot run as a
 * module. Must be registered after the static file handler, so chunks the build has are served
 * normally. The response must never be cached: after a rollback, the same name can be a real chunk
 * again.
 *
 * Only chunks loaded when the user navigates are covered. A tab that sits idle, or only revisits
 * pages it already loaded, never requests a missing chunk; the browser's update checks cover those.
 * Loading chunks in the background, such as route preloading or `@defer` prefetching, would let
 * this reload a page the user is reading or typing in, so check that before adding either.
 */
export function createMissingChunkHandler() {
  const staleChunkModule = buildStaleChunkModule();
  return (_req: Request, res: Response) => {
    res.set('Cache-Control', 'no-store').type('text/javascript').send(staleChunkModule);
  };
}
