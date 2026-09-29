import { SortOrder } from '@sagebionetworks/explorers/models';

export const RESERVED_COMPARISON_TOOL_QUERY_PARAM_KEYS = new Set([
  'pinned',
  'categories',
  'sortFields',
  'sortOrders',
]);

// The order the comparison tool gives a sort field that has none.
export const DEFAULT_SORT_ORDER: SortOrder = 1;

export const VALID_PAGE_SIZES: readonly number[] = [10, 25, 50];
export const DEFAULT_PAGE_SIZE = VALID_PAGE_SIZES[0];

export const MAX_PINNED_ITEMS = 50;
