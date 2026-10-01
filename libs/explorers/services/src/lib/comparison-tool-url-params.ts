import { Params } from '@angular/router';
import {
  DEFAULT_SORT_ORDER,
  RESERVED_COMPARISON_TOOL_QUERY_PARAM_KEYS,
} from '@sagebionetworks/explorers/constants';
import { ComparisonToolUrlParams, SortOrder } from '@sagebionetworks/explorers/models';
import {
  parseCommaSeparatedQueryParam,
  stringifyCommaSeparatedQueryParam,
} from '@sagebionetworks/shared/util';
import { Logger } from '@sagebionetworks/web-shared/angular/logger';

export const INVALID_SORT_ORDERS_MESSAGE =
  'deserializeComparisonToolUrlParams: sort orders do not match sort fields';

/**
 * Pass `logger` to report URL params the comparison tool never writes itself, such as sort orders
 * that don't pair up with their sort fields. Callers that only read the params leave it out, so one
 * URL is reported once.
 */
export function deserializeComparisonToolUrlParams(
  params: Params,
  logger?: Pick<Logger, 'warn'>,
): ComparisonToolUrlParams {
  const result: ComparisonToolUrlParams = {};

  const pinnedItems = parseCommaSeparatedQueryParam(params['pinned']);
  if (pinnedItems.length > 0) {
    result.pinnedItems = pinnedItems;
  }

  const categories = parseCommaSeparatedQueryParam(params['categories']);
  if (categories.length > 0) {
    result.categories = categories;
  }

  const sortFields = parseCommaSeparatedQueryParam(params['sortFields']);
  if (sortFields.length > 0) {
    result.sortFields = sortFields;
  }

  const { sortOrders, hasInvalidSortOrder } = parseSortOrdersParam(params['sortOrders']);
  if (sortOrders.length > 0) {
    result.sortOrders = sortOrders;
  }

  // The tool writes one order per sort field, so an unreadable order or a count mismatch means the
  // URL was edited or built elsewhere, and the sort it shows may not be the one intended.
  const hasSortOrderCountMismatch = sortOrders.length !== sortFields.length;
  if (hasInvalidSortOrder || hasSortOrderCountMismatch) {
    logger?.warn(INVALID_SORT_ORDERS_MESSAGE, {
      sortFields: params['sortFields'] ?? null,
      sortOrders: params['sortOrders'] ?? null,
      hasInvalidSortOrder,
      hasSortOrderCountMismatch,
    });
  }

  const filterSelections = deserializeFilterSelections(params);
  if (Object.keys(filterSelections).length > 0) {
    result.filterSelections = filterSelections;
  }

  return result;
}

/**
 * Serializes comparison tool state into query params, as a patch over `currentQueryParams`: a param
 * present in `state` is written, `null` or an empty value is emitted as `null` to remove the key, and
 * an omitted param is left out so its current value stands.
 */
export function serializeComparisonToolUrlParams(
  state: ComparisonToolUrlParams,
  currentQueryParams: Params,
): Params {
  const params: Params = {};

  serializeArrayParam(params, 'categories', state.categories);
  serializeArrayParam(params, 'pinned', state.pinnedItems);
  serializeArrayParam(params, 'sortFields', state.sortFields);
  serializeNumberArrayParam(params, 'sortOrders', state.sortOrders);
  serializeFilterSelections(params, state.filterSelections, currentQueryParams);

  return params;
}

function serializeArrayParam(
  params: Params,
  key: string,
  value: string[] | null | undefined,
): void {
  if (value && value.length > 0) {
    params[key] = stringifyCommaSeparatedQueryParam(value);
  } else if (value !== undefined) {
    params[key] = null;
  }
}

function serializeNumberArrayParam(
  params: Params,
  key: string,
  value: number[] | null | undefined,
): void {
  if (value && value.length > 0) {
    params[key] = value.join(',');
  } else if (value !== undefined) {
    params[key] = null;
  }
}

function serializeFilterSelections(
  params: Params,
  filterSelections: Record<string, string[]> | null | undefined,
  currentQueryParams: Params,
): void {
  if (filterSelections === undefined) {
    return;
  }

  // Filter keys are open-ended, so the ones to remove can only be found in the current URL.
  const currentFilterKeys = Object.keys(currentQueryParams).filter(
    (key) => !RESERVED_COMPARISON_TOOL_QUERY_PARAM_KEYS.has(key),
  );

  for (const key of currentFilterKeys) {
    params[key] = null;
  }

  if (filterSelections) {
    for (const [queryParamKey, values] of Object.entries(filterSelections)) {
      if (values && values.length > 0) {
        params[queryParamKey] = stringifyCommaSeparatedQueryParam(values);
      }
    }
  }
}

function deserializeFilterSelections(params: Params): Record<string, string[]> {
  const filterSelections: Record<string, string[]> = {};

  for (const [key, value] of Object.entries(params)) {
    if (RESERVED_COMPARISON_TOOL_QUERY_PARAM_KEYS.has(key)) {
      continue;
    }

    const values = parseCommaSeparatedQueryParam(value);
    if (values.length > 0) {
      filterSelections[key] = values;
    }
  }

  return filterSelections;
}

// An unreadable entry keeps its position, with the default order, so every later order still lines
// up with its own sort field.
function parseSortOrdersParam(value: string | string[] | null | undefined): {
  sortOrders: SortOrder[];
  hasInvalidSortOrder: boolean;
} {
  const stringValue = Array.isArray(value) ? value.join(',') : (value ?? '');
  if (stringValue.trim() === '') {
    return { sortOrders: [], hasInvalidSortOrder: false };
  }

  let hasInvalidSortOrder = false;
  const sortOrders = stringValue.split(',').map((entry): SortOrder => {
    const order = Number(entry.trim());
    if (order === 1 || order === -1) {
      return order;
    }
    hasInvalidSortOrder = true;
    return DEFAULT_SORT_ORDER;
  });

  return { sortOrders, hasInvalidSortOrder };
}
