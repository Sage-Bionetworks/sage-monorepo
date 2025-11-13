import { expect, Page } from '@playwright/test';
import { getUnpinnedTable } from '@sagebionetworks/explorers/testing/e2e';
import { COMPARISON_PATHS } from './constants';

export const headerSearchPlaceholder = 'Search models';

export const searchAndGetSearchListItems = async (
  query: string,
  page: Page,
  searchPlaceholder = headerSearchPlaceholder,
) => {
  const responsePromise = page.waitForResponse(`**/models/search?q=${query}`);
  const input = page.getByPlaceholder(searchPlaceholder);
  await input.pressSequentially(query);
  await responsePromise;

  const searchList = page.getByRole('list').filter({ hasText: query });
  const searchListItems = searchList.getByRole('listitem');
  return { input, searchListItems };
};

export const navigateToComparison = async (page: Page, name: string, queryParameters?: string) => {
  const path = COMPARISON_PATHS[name];
  const url = queryParameters ? `${path}?${queryParameters}` : path;
  await page.goto(url);
  await expect(page.getByRole('heading', { level: 1, name })).toBeVisible();
  await expect(page.locator('explorers-base-table')).toHaveCount(2);
  await expect(getUnpinnedTable(page).locator('tbody tr').first()).toBeVisible();
};
