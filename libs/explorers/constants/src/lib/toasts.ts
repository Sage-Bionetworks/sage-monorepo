export const TOAST_DURATION_MS = 5000; // milliseconds

export const getPinLimitWarning = (pinnedCount: number, pinLimit: number): string => {
  const rows = pinnedCount === 1 ? 'row was' : 'rows were';
  return `Only ${pinnedCount} ${rows} pinned, because you reached the maximum of ${pinLimit} pinned items.`;
};
