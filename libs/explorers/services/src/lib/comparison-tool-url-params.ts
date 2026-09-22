import { Params } from '@angular/router';
import { RESERVED_COMPARISON_TOOL_QUERY_PARAM_KEYS } from '@sagebionetworks/explorers/constants';
import { ComparisonToolUrlParams, SortOrder } from '@sagebionetworks/explorers/models';
import {
  parseCommaSeparatedQueryParam,
  stringifyCommaSeparatedQueryParam,
} from '@sagebionetworks/shared/util';

export function deserializeComparisonToolUrlParams(params: Params): ComparisonToolUrlParams {
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

  const sortOrders = parseSortOrdersParam(params['sortOrders']);
  if (sortOrders.length > 0) {
    result.sortOrders = sortOrders;
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

function parseSortOrdersParam(value: string | string[] | null | undefined): SortOrder[] {
  if (value == null) {
    return [];
  }

  const stringValue = Array.isArray(value) ? value.join(',') : value;
  return stringValue
    .split(',')
    .map((order) => parseInt(order.trim(), 10))
    .filter((order): order is SortOrder => order === 1 || order === -1);
}
