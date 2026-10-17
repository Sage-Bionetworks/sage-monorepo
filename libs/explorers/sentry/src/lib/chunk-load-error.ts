/**
 * Messages browsers use when a lazy-loaded chunk cannot be fetched. After a deploy, a tab from the
 * earlier deploy requests chunk files the new server no longer has. Chrome, Firefox, and Safari
 * each word it differently.
 */
const CHUNK_LOAD_ERROR_MESSAGES = [
  'Failed to fetch dynamically imported module',
  'error loading dynamically imported module',
  'Importing a module script failed',
];

export function isChunkLoadError(error: unknown): boolean {
  const message = error instanceof Error ? error.message : '';
  return CHUNK_LOAD_ERROR_MESSAGES.some((chunkLoadMessage) => message.includes(chunkLoadMessage));
}
