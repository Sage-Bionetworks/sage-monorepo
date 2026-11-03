import type { NextFunction, Request, Response } from 'express';

/** When the legacy chunk module last reloaded this tab, in `sessionStorage` (one per tab). */
export const LEGACY_CHUNK_RELOADED_AT_KEY = 'legacyChunkReloadedAt';
/**
 * How long after a reload the legacy chunk module refuses to reload the tab again. During a rolling
 * deploy, the reloaded page can still be served by an old task and request a chunk a new task does
 * not have, which would otherwise reload the tab over and over.
 */
export const LEGACY_CHUNK_RELOAD_GUARD_MS = 60 * 1000;
/** Thrown by the legacy chunk module when it does not reload, so the import fails as before. */
export const LEGACY_CHUNK_RELOAD_SKIPPED_MESSAGE =
  'A page from an earlier release could not be loaded, and the tab was not reloaded again';
/** Logged each time the module is served, so the count shows when legacy tabs are gone. */
export const LEGACY_CHUNK_RELOAD_LOG_MESSAGE = 'Served the legacy chunk reload module';

/**
 * The module sent in place of a legacy chunk. It reloads the tab, so the tab gets the current
 * build, unless it already did within `LEGACY_CHUNK_RELOAD_GUARD_MS` or `sessionStorage` is
 * unavailable, in which case it throws and the import fails as it would have without it. While the
 * page unloads, the import is kept pending, so the tab shows no error for a navigation the reload
 * is about to replace.
 */
export function buildLegacyChunkModule(): string {
  return `
const reloadedAtKey = ${JSON.stringify(LEGACY_CHUNK_RELOADED_AT_KEY)};
let shouldReload = false;
try {
  const reloadedAt = Number(sessionStorage.getItem(reloadedAtKey));
  shouldReload = !reloadedAt || Date.now() - reloadedAt > ${LEGACY_CHUNK_RELOAD_GUARD_MS};
  if (shouldReload) {
    sessionStorage.setItem(reloadedAtKey, String(Date.now()));
  }
} catch {
  shouldReload = false;
}
if (shouldReload) {
  location.reload();
  await new Promise(() => undefined);
}
throw new Error(${JSON.stringify(LEGACY_CHUNK_RELOAD_SKIPPED_MESSAGE)});
`;
}

/**
 * Answers a request for one of `legacyChunkFileNames` with the legacy chunk module, and passes any
 * other request on, to the missing-chunk 404. A legacy chunk is one from a build that predates the
 * browser's update checks: a tab running such a build cannot recover from a missing chunk, so it
 * is reloaded instead. Builds with the update checks recover on their own by loading the page the
 * user clicked, which a reload would lose, so their chunk names must never be in the list.
 *
 * The module only runs when a legacy tab navigates to a page it has not loaded, since those builds
 * load chunks on navigation alone. Must be registered after the static file handler, so chunks the
 * build has are served normally, and before the missing-chunk handler. The response must never be
 * cached: after a rollback, the same name can be a real chunk again.
 */
export function createLegacyChunkReloadHandler(
  legacyChunkFileNames: ReadonlySet<string>,
  log: (message: string) => void = console.log,
) {
  const legacyChunkModule = buildLegacyChunkModule();
  return (req: Request, res: Response, next: NextFunction) => {
    const fileName = req.path.slice(1);
    if (!legacyChunkFileNames.has(fileName)) {
      next();
      return;
    }
    log(`${LEGACY_CHUNK_RELOAD_LOG_MESSAGE}: ${fileName}`);
    res.set('Cache-Control', 'no-store').type('text/javascript').send(legacyChunkModule);
  };
}
