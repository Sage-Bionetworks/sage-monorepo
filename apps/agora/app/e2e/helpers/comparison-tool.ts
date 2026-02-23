import { Page, expect } from '@playwright/test';
import { expectComparisonToolTableLoaded } from '@sagebionetworks/explorers/testing/e2e';
import { ComparisonToolConfig } from '@sagebionetworks/model-ad/api-client';
import { baseURL } from '../../playwright.config';
import {
  COMPARISON_TOOL_API_PATHS,
  COMPARISON_TOOL_CONFIG_PATH,
  COMPARISON_TOOL_DEFAULT_SORTS,
  COMPARISON_TOOL_PATHS,
} from './constants';

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
