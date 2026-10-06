import { SortOrder } from '@sagebionetworks/explorers/models';

export const RESERVED_COMPARISON_TOOL_QUERY_PARAM_KEYS = new Set([
  'pinned',
  'categories',
  'sortFields',
  'sortOrders',
]);

/**
 * Router navigation state for a link into a comparison tool that should keep the tool's pins,
 * filters, and sort, the way changing the tool's dropdown does. The link's categories, if any, are
 * still applied, and categories naming only the leading levels select that branch's first config,
 * so a link to a main category resets the deeper levels to their defaults.
 */
export const KEEP_COMPARISON_TOOL_STATE_KEY = 'keepComparisonToolState';
export const KEEP_COMPARISON_TOOL_STATE = { [KEEP_COMPARISON_TOOL_STATE_KEY]: true };

// The order the comparison tool gives a sort field that has none.
export const DEFAULT_SORT_ORDER: SortOrder = 1;

export const VALID_PAGE_SIZES: readonly number[] = [10, 25, 50];
export const DEFAULT_PAGE_SIZE = VALID_PAGE_SIZES[0];

export const MAX_PIN_LIMIT = 50;
