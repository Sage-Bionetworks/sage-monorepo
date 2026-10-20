/**
 * Thrown by the module the app server sends in place of a chunk file the current build does not
 * have, when it has already reloaded the tab once (see `createMissingChunkHandler` in
 * explorers/config). The app then handles it like any other chunk that failed to load.
 */
export const STALE_CHUNK_ERROR_MESSAGE =
  'This chunk belongs to an earlier deploy, and the page was already reloaded once';

/**
 * Messages a chunk that cannot be loaded produces. After a deploy, a tab from the earlier deploy
 * requests chunk files the new server no longer has. Chrome, Firefox, and Safari each word it
 * differently, and the app server's stale chunk module throws its own message.
 */
const CHUNK_LOAD_ERROR_MESSAGES = [
  'Failed to fetch dynamically imported module',
  'error loading dynamically imported module',
  'Importing a module script failed',
  STALE_CHUNK_ERROR_MESSAGE,
];

export function isChunkLoadError(error: unknown): boolean {
  const message = error instanceof Error ? error.message : '';
  return CHUNK_LOAD_ERROR_MESSAGES.some((chunkLoadMessage) => message.includes(chunkLoadMessage));
}
