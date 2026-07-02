export const RESERVED_COMPARISON_TOOL_QUERY_PARAM_KEYS = new Set([
  'pinned',
  'categories',
  'sortFields',
  'sortOrders',
]);

export const VALID_PAGE_SIZES: readonly number[] = [10, 25, 50];
export const DEFAULT_PAGE_SIZE = VALID_PAGE_SIZES[0];
