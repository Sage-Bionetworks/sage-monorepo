import type { Request, Response } from 'express';

/**
 * The file name the production build gives every lazy-loaded chunk. Each build gives its chunks
 * new names, so a tab opened before a deploy requests names the new build does not have.
 */
export const CHUNK_FILE_PATH_PATTERN = /^\/chunk-[A-Z0-9]{8}\.js$/;

/**
 * Answers a request for a chunk file the current build does not have with a 404, instead of the
 * HTML page the server would otherwise render for it. The browser rejects the import either way,
 * and the app recovers from that (see `AppUpdateService`), but a 404 skips rendering a page no one
 * will see. Must be registered after the static file handler, so chunks the build has are served
 * normally. The response must never be cached: after a rollback, the same name can be a real chunk
 * again.
 */
export function createMissingChunkHandler() {
  return (_req: Request, res: Response) => {
    res.set('Cache-Control', 'no-store').sendStatus(404);
  };
}
