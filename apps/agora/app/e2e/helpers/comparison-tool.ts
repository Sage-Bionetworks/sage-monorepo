import { Page, expect } from '@playwright/test';
import {
  ComparisonToolConfig,
  ComparisonToolConfigFilter,
} from '@sagebionetworks/agora/api-client';
import { expectComparisonToolTableLoaded } from '@sagebionetworks/explorers/testing/e2e';
import { baseURL } from '../../playwright.config';
import {
  COMPARISON_TOOL_API_PATHS,
  COMPARISON_TOOL_CONFIG_PATH,
  COMPARISON_TOOL_DEFAULT_PAGE_SIZE,
  COMPARISON_TOOL_DEFAULT_SORTS,
  COMPARISON_TOOL_PATHS,
} from './constants';

const snakeToCamel = (value: string): string =>
  value.replace(/_([a-z])/g, (_match, char: string) => char.toUpperCase());

export const navigateToComparison = async (
  page: Page,
  name: string,
  shouldCloseVisualizationOverviewDialog = false,
  navigateBy: 'url' | 'link' = 'url',
  queryParameters?: string,
) => {
  if (navigateBy === 'url') {
    const path = COMPARISON_TOOL_PATHS[name];
    const urlPath = queryParameters ? `${path}?${queryParameters}` : path;
    await page.goto(urlPath);
  } else {
    // Open the hamburger menu if the button is visible (mobile breakpoint)
    const menuButton = page.locator('.hamburger-menu-button');
    if (await menuButton.isVisible().catch(() => false)) {
      await menuButton.click();
    }
    await page.getByRole('link', { name: name }).click();
  }

  await expectComparisonToolTableLoaded(page, name, shouldCloseVisualizationOverviewDialog);
};

export const fetchComparisonToolData = async <T>(
  page: Page,
  name: string,
  categories: string[] = [],
): Promise<T> => {
  const params = new URLSearchParams();
  params.append('itemFilterType', 'exclude');
  for (const category of categories) {
    params.append('categories', category);
  }

  // sortFields and sortOrders are required by the API
  const defaultSort = COMPARISON_TOOL_DEFAULT_SORTS[name];
  for (const sort of defaultSort) {
    params.append('sortFields', sort.field);
    params.append('sortOrders', sort.order.toString());
  }

  const response = await page.request.get(`${baseURL}/api/v1/${COMPARISON_TOOL_API_PATHS[name]}`, {
    params,
  });
  expect(response.ok()).toBeTruthy();
  const data = (await response.json()) as T;
  return data;
};

export const fetchComparisonToolConfig = async (
  page: Page,
  name: string,
): Promise<ComparisonToolConfig[]> => {
  const response = await page.request.get(`${baseURL}/api/v1/${COMPARISON_TOOL_CONFIG_PATH}`, {
    params: { page: name },
  });
  expect(response.ok()).toBeTruthy();
  const data = (await response.json()) as ComparisonToolConfig[];
  return data;
};

const fetchFilteredPageCount = async (
  page: Page,
  name: string,
  filter: ComparisonToolConfigFilter,
  value: string,
): Promise<number> => {
  const params = new URLSearchParams();
  params.append('itemFilterType', 'exclude');
  params.append('pageSize', COMPARISON_TOOL_DEFAULT_PAGE_SIZE.toString());

  // sortFields and sortOrders are required by the API
  const defaultSort = COMPARISON_TOOL_DEFAULT_SORTS[name];
  for (const sort of defaultSort) {
    params.append('sortFields', sort.field);
    params.append('sortOrders', sort.order.toString());
  }

  // The config's query_param_key targets the frontend route; the API filters on the
  // camelCased data_key (e.g. maximum_clinical_trial_phase -> maximumClinicalTrialPhase).
  params.append(snakeToCamel(filter.data_key), value);

  const response = await page.request.get(`${baseURL}/api/v1/${COMPARISON_TOOL_API_PATHS[name]}`, {
    params,
  });
  expect(response.ok()).toBeTruthy();
  const data = (await response.json()) as { page: { totalPages: number } };
  return data.page.totalPages;
};

/**
 * Finds the first (filter, value) pair whose applied result still spans more than one
 * page. Pagination-reset tests need such a pair; some filters (e.g. Max Clinical Trial
 * Phase) have no single value large enough to paginate.
 */
export const findFilterValueSpanningMultiplePages = async (
  page: Page,
  name: string,
  filters: ComparisonToolConfigFilter[],
): Promise<{ name: string; value: string }> => {
  for (const filter of filters) {
    for (const value of filter.values) {
      if ((await fetchFilteredPageCount(page, name, filter, value)) > 1) {
        return { name: filter.name, value };
      }
    }
  }
  throw new Error(`No filter value spans multiple pages for "${name}"`);
};
