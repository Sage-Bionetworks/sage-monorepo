import { getTabId, TAB_ID_STORAGE_KEY } from './tab-id';

const tabIdPattern = /^[0-9a-f]{32}$/;

const blockedStorageWindow = {
  get sessionStorage(): Storage {
    throw new DOMException('Access is denied for this document.', 'SecurityError');
  },
} as unknown as Window;

describe('getTabId', () => {
  afterEach(() => {
    window.sessionStorage.clear();
    jest.restoreAllMocks();
  });

  it('creates a random ID on the first page load and stores it for the tab', () => {
    const tabId = getTabId(window);

    expect(tabId).toMatch(tabIdPattern);
    expect(window.sessionStorage.getItem(TAB_ID_STORAGE_KEY)).toBe(tabId);
  });

  it('keeps the same ID when the tab reloads', () => {
    const firstPageLoadTabId = getTabId(window);

    expect(getTabId(window)).toBe(firstPageLoadTabId);
  });

  it('gives a different tab a different ID', () => {
    const firstTabId = getTabId(window);
    window.sessionStorage.clear();

    expect(getTabId(window)).not.toBe(firstTabId);
  });

  it('creates an ID for each page load when sessionStorage is blocked', () => {
    const firstPageLoadTabId = getTabId(blockedStorageWindow);

    expect(firstPageLoadTabId).toMatch(tabIdPattern);
    expect(getTabId(blockedStorageWindow)).not.toBe(firstPageLoadTabId);
  });

  it('still returns an ID when sessionStorage is full', () => {
    jest.spyOn(Storage.prototype, 'setItem').mockImplementation(() => {
      throw new DOMException('The quota has been exceeded.', 'QuotaExceededError');
    });

    expect(getTabId(window)).toMatch(tabIdPattern);
  });
});
