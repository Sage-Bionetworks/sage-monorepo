export const TOAST_DURATION_MS = 5000; // milliseconds

export const getMaxPinnedItemsWarning = (pinnedCount: number, maxPinnedItems: number): string => {
  const rows = pinnedCount === 1 ? 'row was' : 'rows were';
  return `Only ${pinnedCount} ${rows} pinned, because you reached the maximum of ${maxPinnedItems} pinned items.`;
};
