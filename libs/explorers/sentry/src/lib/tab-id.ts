/**
 * The name of the attribute on every metric and the tag on every error event that identifies the
 * browser tab they came from. Sentry queries filter on it to follow one tab, so it must not change.
 */
export const TAB_ID_KEY = 'tab.id';

/** Where the tab ID is kept in `sessionStorage`, so it survives reloads of the same tab. */
export const TAB_ID_STORAGE_KEY = 'sentryTabId';

const TAB_ID_BYTE_LENGTH = 16;

function createTabId(): string {
  const bytes = crypto.getRandomValues(new Uint8Array(TAB_ID_BYTE_LENGTH));
  return Array.from(bytes, (byte) => byte.toString(16).padStart(2, '0')).join('');
}

/**
 * Returns this tab's ID, creating and storing it on the tab's first page load. Without
 * `sessionStorage` (e.g. blocked by privacy settings), the ID lasts one page load. A duplicated tab
 * starts with a copy of `sessionStorage`, so it shares the ID of the tab it was duplicated from.
 */
export function getTabId(browserWindow: Window): string {
  try {
    const sessionStorage = browserWindow.sessionStorage;
    const storedTabId = sessionStorage.getItem(TAB_ID_STORAGE_KEY);
    if (storedTabId) {
      return storedTabId;
    }
    const tabId = createTabId();
    sessionStorage.setItem(TAB_ID_STORAGE_KEY, tabId);
    return tabId;
  } catch {
    return createTabId();
  }
}
