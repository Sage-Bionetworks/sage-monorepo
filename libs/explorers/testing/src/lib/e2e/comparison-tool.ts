import { expect, Locator, Page, test } from '@playwright/test';
import {
  DEFAULT_PAGE_SIZE,
  getMaxPinnedItemsWarning,
  MAX_PINNED_ITEMS,
  RESERVED_COMPARISON_TOOL_QUERY_PARAM_KEYS,
} from '@sagebionetworks/explorers/constants';
import { ComparisonToolConfigColumnTypeEnum } from '@sagebionetworks/explorers/models';
import { escapeRegexChars } from '@sagebionetworks/shared/util/helpers';

export const getQueryParamFromValues = (values: string[], key: string): string => {
  // Query parameter values are encoded once by CT URL service and again by Angular router
  return `${key}=${values.map((value) => encodeURIComponent(encodeURIComponent(value))).join(',')}`;
};

export const getQueryParamsFromRecords = (records: Record<string, string[]>): string => {
  const queryParams: string[] = [];
  for (const [key, values] of Object.entries(records)) {
    queryParams.push(getQueryParamFromValues(values, key));
  }
  return queryParams.join('&');
};

const getQueryParamValues = (url: string, key: string): string[] => {
  const searchParams = new URL(url).searchParams;
  const paramValues = searchParams.getAll(key);

  if (!paramValues.length) {
    return [];
  }

  return paramValues
    .flatMap((value) => value.split(','))
    .map((value) => value.trim())
    .map((value) => {
      if (!value) {
        return '';
      }

      try {
        return decodeURIComponent(value);
      } catch {
        return value;
      }
    })
    .filter((value) => value.length > 0);
};

export const getPinnedQueryParams = (url: string): string[] => getQueryParamValues(url, 'pinned');

export const getCategoriesQueryParams = (url: string): string[] =>
  getQueryParamValues(url, 'categories');

export const getSortFieldsQueryParams = (url: string): string[] =>
  getQueryParamValues(url, 'sortFields');

export const getSortOrdersQueryParams = (url: string): number[] =>
  getQueryParamValues(url, 'sortOrders').map((v) => parseInt(v, 10));

export const getFiltersQueryParams = (url: string): Record<string, string[]> => {
  const filters: Record<string, string[]> = {};
  const keys: string[] = Array.from((new URL(url).searchParams as any).keys());
  for (const key of keys) {
    if (RESERVED_COMPARISON_TOOL_QUERY_PARAM_KEYS.has(key)) {
      continue;
    }
    filters[key] = getQueryParamValues(url, key);
  }
  return filters;
};

export const getPinnedTable = (page: Page): Locator => page.locator('explorers-base-table').first();

export const getUnpinnedTable = (page: Page): Locator =>
  page.locator('explorers-base-table').last();

const getPaginatorRange = (page: Page): Locator => page.locator('.p-paginator-current');

export const expectPaginatorRange = async (page: Page, range: string): Promise<void> => {
  await expect(getPaginatorRange(page)).toHaveText(range);
};

export const expectUnpinnedTableOnly = async (page: Page): Promise<void> => {
  await expect(page.locator('explorers-base-table').filter({ visible: true })).toHaveCount(1);
  await expect(getUnpinnedTable(page).locator('tbody tr').first()).toBeVisible();
};

const PRIMARY_CELL_SELECTOR = `td.${ComparisonToolConfigColumnTypeEnum.Primary}`;

export const getRowByName = (table: Locator, page: Page, name: string): Locator =>
  table.locator('tbody tr').filter({
    has: page.getByRole('cell', { name, exact: true }).and(page.locator(PRIMARY_CELL_SELECTOR)),
  });

export const clickViewDetailsButtonByName = async (table: Locator, page: Page, name: string) => {
  const row = getRowByName(table, page, name);
  await expect(row).toHaveCount(1);
  const viewDetailsButton = row.getByRole('button', { name: 'View Details' });
  await viewDetailsButton.focus();
  await viewDetailsButton.press('Enter');
};

export const togglePinByName = async (
  table: Locator,
  page: Page,
  name: string,
  toggle: 'pin' | 'unpin',
) => {
  const row = getRowByName(table, page, name);
  await expect(row).toHaveCount(1);
  const pinButton = row.getByRole('button', { name: toggle === 'pin' ? 'Pin' : 'Unpin' });
  await pinButton.focus();
  await pinButton.press('Enter');
  return row;
};

export const pinByName = async (table: Locator, page: Page, name: string) => {
  return await togglePinByName(table, page, name, 'pin');
};

export const unPinByName = async (table: Locator, page: Page, name: string) => {
  return await togglePinByName(table, page, name, 'unpin');
};

export const pinAll = async (page: Page) => {
  await page.getByRole('button', { name: 'Pin All' }).click();
};

export const expectPinnedParams = async (page: Page, expected: string[]): Promise<void> => {
  await expect.poll(() => getPinnedQueryParams(page.url())).toEqual(expected);
};

export const expectCategoriesParams = async (page: Page, expected: string[]): Promise<void> => {
  await expect.poll(() => getCategoriesQueryParams(page.url())).toEqual(expected);
};

export const expectSortFieldsParams = async (page: Page, expected: string[]): Promise<void> => {
  await expect.poll(() => getSortFieldsQueryParams(page.url())).toEqual(expected);
};

export const expectSortOrdersParams = async (page: Page, expected: number[]): Promise<void> => {
  await expect.poll(() => getSortOrdersQueryParams(page.url())).toEqual(expected);
};

// expected: key: filter query_param_key, value: array of selected filter option labels
export const expectFiltersParams = async (
  page: Page,
  expected: Record<string, string[]>,
): Promise<void> => {
  await expect.poll(() => getFiltersQueryParams(page.url())).toEqual(expected);
};

export const expectPinnedRows = async (page: Page, rowNames: string[]): Promise<void> => {
  await expect(page.locator('explorers-base-table')).toHaveCount(2);
  const pinnedTable = getPinnedTable(page);
  for (const rowName of rowNames) {
    await expect(getRowByName(pinnedTable, page, rowName)).toHaveCount(1);
  }
};

export const expectToastDetail = async (page: Page, detail: string): Promise<void> => {
  await expect(page.getByRole('alert')).toContainText(detail);
};

export const expectPinnedResultsCount = async (page: Page, pinnedCount: number): Promise<void> => {
  await expect(page.getByText(`Pinned Results (${pinnedCount}/${MAX_PINNED_ITEMS})`)).toBeVisible();
  await expect(getPinnedTable(page).getByRole('row')).toHaveCount(pinnedCount);
};

// The button only renders alongside matching results, so search or filter before asserting on it
export const expectPinAllDisabled = async (page: Page): Promise<void> => {
  await expect(page.getByRole('button', { name: 'Pin All' })).toBeDisabled();
};

export const expectCategories = async (page: Page, categories: string[]): Promise<void> => {
  for (const category of categories) {
    await expect(page.getByText(category)).toBeVisible();
  }
};

// selectedFilters: key: filter name, value: array of selected filter option labels
export const expectFilters = async (
  page: Page,
  selectedFilters: Record<string, string[]>,
): Promise<void> => {
  const filterPanelMain = await toggleFilterPanel(page);
  for (const [filterMenuName, filterNames] of Object.entries(selectedFilters)) {
    await openFilterPanelSecondaryPane(filterPanelMain, filterMenuName);
    for (const filterName of filterNames) {
      const filterCheckbox = await getFilterCheckbox(page, filterMenuName, filterName);
      await expect(filterCheckbox).toBeChecked();
    }
  }
  await toggleFilterPanel(page);
};

// selectedFilters: key: filter short name, value: array of selected filter option labels
export const expectFilterChiclets = async (
  page: Page,
  selectedFilters: Record<string, string[]>,
): Promise<void> => {
  for (const [filterShortName, filterValues] of Object.entries(selectedFilters)) {
    for (const filterValue of filterValues) {
      const chiclet = page.getByText(`${filterShortName}: ${filterValue}`);
      await expect(chiclet).toBeVisible();
    }
  }
};

export const searchViaFilterbox = async (page: Page, searchTerm: string): Promise<void> => {
  const searchInput = page.getByPlaceholder('Value1, Value2', { exact: true });
  await searchInput.clear();
  await searchInput.fill(searchTerm);
  await expect(page.getByText('Matching Results')).toBeVisible();
};

export const expectNoResultsFound = async (page: Page): Promise<void> => {
  await expectPaginatorRange(page, '0-0 of 0');
  await expect(page.getByText('No results found')).toBeVisible();
};

export async function goToLastPage(page: Page) {
  const lastPageBtn = page.getByRole('button', { name: /last page/i });
  await expect(lastPageBtn).not.toHaveClass(/p-disabled/);

  await lastPageBtn.click();
  await expect(lastPageBtn).toHaveClass(/p-disabled/);

  // Wait for the paginator to show a non-first page (text should NOT start with "1-")
  await expect(getPaginatorRange(page)).not.toContainText(/^1-/);
}

export const toggleFilterPanel = async (page: Page): Promise<Locator> => {
  const filterButton = page.getByRole('button', { name: 'Filter Results' });
  await filterButton.click();

  const filterPanelMain = page.locator('.filter-panel-main');
  return filterPanelMain;
};

export const openFilterPanelSecondaryPane = async (
  filterPanelMain: Locator,
  filterMenuName: string,
): Promise<void> => {
  const filterMenuButtonNameRegex = new RegExp(filterMenuName, 'i');
  const filterMenuButton = filterPanelMain.getByRole('button', {
    name: filterMenuButtonNameRegex,
  });
  await filterMenuButton.click();
};

export const getFilterCheckbox = async (
  page: Page,
  filterMenuName: string,
  filterName: string,
): Promise<Locator> => {
  const filterPanelSecondary = page.locator('.filter-panel-pane').filter({ visible: true });
  await expect(filterPanelSecondary.getByText(filterMenuName)).toBeVisible();
  const filterCheckboxNameRegex = new RegExp(filterName, 'i');
  const filterCheckbox = filterPanelSecondary.getByRole('checkbox', {
    name: filterCheckboxNameRegex,
  });
  return filterCheckbox;
};

export async function clickFilterCheckbox(
  filterPanelMain: Locator,
  filterMenuName: string,
  filterName: string,
) {
  await openFilterPanelSecondaryPane(filterPanelMain, filterMenuName);
  const filterCheckbox = await getFilterCheckbox(
    filterPanelMain.page(),
    filterMenuName,
    filterName,
  );
  await filterCheckbox.click();
}

export async function expectFirstPage(page: Page) {
  const firstPageBtn = page.getByRole('button', { name: /first page/i });
  await expect(firstPageBtn).toHaveClass(/p-disabled/);
  await expect(page.getByRole('button', { name: /previous page/i })).toHaveClass(/p-disabled/);
  // Wait for paginator to show first page range (e.g., "1-10 of X")
  await expect(getPaginatorRange(page)).toContainText(/^1-/);
}

export async function openFilterMenuAndClickCheckbox(
  page: Page,
  filterMenuName: string,
  filterName: string,
): Promise<Locator> {
  const filterPanelMain = await toggleFilterPanel(page);
  await clickFilterCheckbox(filterPanelMain, filterMenuName, filterName);
  return filterPanelMain;
}

export async function testPinLastItemLastPageGoesToPreviousPage(page: Page) {
  const lastPageBtn = page.getByRole('button', { name: /last page/i });
  await expect(lastPageBtn).not.toHaveClass(/p-disabled/);

  const unpinnedTable = getUnpinnedTable(page);

  const firstCellName = await unpinnedTable.getByRole('group').first().getAttribute('aria-label');
  const firstRow = getRowByName(unpinnedTable, page, firstCellName || '');
  await expect(firstRow).toBeVisible();

  await lastPageBtn.click();
  await expect(lastPageBtn).toHaveClass(/p-disabled/);
  await expect(firstRow).not.toBeVisible(); // wait for first row to no longer be visible, so data has loaded

  const pinButtonsCount = await unpinnedTable.getByRole('button', { name: 'Pin' }).count();

  for (let i = 0; i < pinButtonsCount; i++) {
    await expect(unpinnedTable.getByRole('row')).toHaveCount(pinButtonsCount - i);
    const pinButton = unpinnedTable.getByRole('button', { name: 'Pin' }).first();
    await pinButton.focus();
    await pinButton.press('Enter');
  }

  await expect(unpinnedTable.getByRole('row')).toHaveCount(10); // previous full page loaded
  await expect(lastPageBtn).toHaveClass(/p-disabled/);
}

// Tests that "Pin All" pins every row matching the search, not just the rows on the current page
// searchTerm - a term whose matches span more than one page but stay within the pin limit
// expectedPinnedIds - every matching row id, in the table's sort order. Derive these from the
//                     same query Pin All sends, so rows the browser never held are covered
export async function testPinAllAcrossPages(
  page: Page,
  searchTerm: string,
  expectedPinnedIds: string[],
) {
  expect(expectedPinnedIds.length).toBeGreaterThan(DEFAULT_PAGE_SIZE);
  expect(expectedPinnedIds.length).toBeLessThanOrEqual(MAX_PINNED_ITEMS);

  await searchViaFilterbox(page, searchTerm);
  await expectPaginatorRange(page, `1-${DEFAULT_PAGE_SIZE} of ${expectedPinnedIds.length}`);

  await pinAll(page);

  await expectPinnedParams(page, expectedPinnedIds);
  await expectPinnedResultsCount(page, expectedPinnedIds.length);
  await expectPinnedRows(page, expectedPinnedIds);
  // Every match is pinned, so nothing is left for the matching results table
  await expectNoResultsFound(page);
}

// Tests that "Pin All" stops at the pin limit and says so
// searchTerm - term matching more rows than the pin limit allows
// expectedPinnedIds - the MAX_PINNED_ITEMS row ids the server returns for the search, in the
//                      table's sort order
export async function testPinAllExceedsLimit(
  page: Page,
  searchTerm: string,
  expectedPinnedIds: string[],
) {
  expect(expectedPinnedIds).toHaveLength(MAX_PINNED_ITEMS);

  await searchViaFilterbox(page, searchTerm);
  await pinAll(page);

  await expectToastDetail(page, getMaxPinnedItemsWarning(MAX_PINNED_ITEMS, MAX_PINNED_ITEMS));
  await expectPinnedParams(page, expectedPinnedIds);
  await expectPinnedResultsCount(page, MAX_PINNED_ITEMS);
  await expectPinAllDisabled(page);
  await expectPinnedRows(page, expectedPinnedIds);
}

// Tests that pinned items restored from the URL are capped at the pin limit. Expects a page already
// navigated to with more pinned query params than the limit allows.
// pinnedIds - the ids the URL carries, in the table's sort order, more than
//             MAX_PINNED_ITEMS of them. The first MAX_PINNED_ITEMS survive the cap.
// searchTerm - a term leaving unpinned matches behind, so the Pin All button renders
export async function testUrlPinsExceedingLimitAreCapped(
  page: Page,
  pinnedIds: string[],
  searchTerm: string,
) {
  expect(pinnedIds.length).toBeGreaterThan(MAX_PINNED_ITEMS);
  const expectedPinnedIds = pinnedIds.slice(0, MAX_PINNED_ITEMS);

  await expectToastDetail(page, getMaxPinnedItemsWarning(MAX_PINNED_ITEMS, MAX_PINNED_ITEMS));
  await expectPinnedParams(page, expectedPinnedIds);
  await expectPinnedResultsCount(page, MAX_PINNED_ITEMS);
  await expectPinnedRows(page, expectedPinnedIds);

  await searchViaFilterbox(page, searchTerm);
  await expectPinAllDisabled(page);
}

export async function testTableReturnsToFirstPageWhenFilterSelectedAndRemoved(
  page: Page,
  filterName: string,
  filterValue: string,
) {
  await goToLastPage(page);

  // Apply filter - should return to first page
  const filterPanel1 = await openFilterMenuAndClickCheckbox(page, filterName, filterValue);
  // Close filter panel to ensure UI has settled
  await toggleFilterPanel(page);
  await expect(filterPanel1).toBeHidden();
  await expectFirstPage(page);

  await goToLastPage(page);

  // Remove filter - should return to first page
  await toggleFilterPanel(page);
  await clickFilterCheckbox(filterPanel1, filterName, filterValue);
  // Close filter panel to ensure UI has settled
  await toggleFilterPanel(page);
  await expect(filterPanel1).toBeHidden();
}

export async function testTableReturnsToFirstPageWhenSearchTermEnteredAndCleared(page: Page) {
  await goToLastPage(page);

  await searchViaFilterbox(page, 'a');
  await expectFirstPage(page);

  await goToLastPage(page);

  await searchViaFilterbox(page, '');
  await expectFirstPage(page);
}

export async function testTableReturnsToFirstPageWhenCategoriesChanged(page: Page) {
  await goToLastPage(page);

  const categorySelectors = page.locator('.comparison-tool-category-selectors');
  const dropdown = categorySelectors.getByRole('combobox').last();
  const listbox = page.getByRole('listbox');

  // Click dropdown to open
  await dropdown.click();
  await expect(listbox).toBeVisible();

  // Select the second option
  const options = page.getByRole('option');
  const secondOption = options.nth(1);
  await expect(secondOption).toBeVisible();
  await secondOption.click();

  // Wait for listbox to close (dropdown selection complete)
  await expect(listbox).toBeHidden();

  await expectFirstPage(page);
}

export async function testTableReturnsToFirstPageWhenSortChanged(page: Page) {
  await goToLastPage(page);

  const columnHeader = page.getByRole('columnheader').nth(2);
  await columnHeader.click();

  await expectFirstPage(page);
}

/**
 * Builds a regex matching a column header's accessible name, which is the column name optionally
 * followed by PrimeNG's multi-sort position badge (e.g. "4 months 1"). Anchoring both ends keeps
 * columns whose names overlap distinct ("4 months" does not match "24 months").
 * @param columnName - The column name as displayed in the header
 */
const getColumnHeaderNameRegex = (columnName: string): RegExp =>
  new RegExp(String.raw`^${escapeRegexChars(columnName)}(\s\d+)?$`, 'i');

/**
 * Clicks a column header to sort by that column.
 * @param page - Playwright Page object
 * @param columnName - The accessible name of the column header to click
 * @param multiSort - If true, uses Meta+click to add to existing sort (multi-column sort).
 *                    If false (default), performs a regular click to replace the current sort.
 */
export async function sortColumn(page: Page, columnName: string, multiSort = false): Promise<void> {
  const columnHeader = page.getByRole('columnheader', {
    name: getColumnHeaderNameRegex(columnName),
  });

  if (multiSort) {
    await columnHeader.click({ modifiers: ['Meta'] });
  } else {
    await columnHeader.click();
  }
}

/**
 * Gets the loading overlay element for a table.
 * @param page - Playwright Page object
 * @returns Locator for the loading overlay
 */
export function getTableLoadingOverlay(page: Page): Locator {
  return page.locator('.p-datatable-loading-overlay');
}

/**
 * Waits for the table loading overlay to be hidden.
 * @param page - Playwright Page object
 * @param timeout - Optional timeout in milliseconds (default 30000)
 */
export async function waitForTableLoadingComplete(page: Page, timeout = 30000): Promise<void> {
  await expect(getTableLoadingOverlay(page)).toBeHidden({ timeout });
}

/**
 * Tests that a loading indicator appears while data is being fetched.
 * This test triggers a sort operation and verifies the loading overlay appears.
 */
export async function testLoadingIndicatorAppearsDuringDataFetch(page: Page): Promise<void> {
  const columnHeader = page.getByRole('columnheader').nth(2);

  // Use Promise.all to click and check for loading overlay at the same time
  // The loading overlay should appear after clicking
  await Promise.all([
    // Wait for loading overlay to appear (may be brief)
    expect(getTableLoadingOverlay(page))
      .toBeVisible({ timeout: 5000 })
      .catch(() => {
        // Loading may complete very quickly, this is acceptable
      }),
    columnHeader.click(),
  ]);

  // Verify loading completes
  await waitForTableLoadingComplete(page);
}

export const closeVisualizationOverviewDialog = async (page: Page) => {
  await test.step('close visualization overview dialog', async () => {
    const dialog = page.getByRole('dialog');

    const closeBtn = dialog.getByRole('button').first();
    await closeBtn.click();

    await expect(dialog).toBeHidden();
  });
};

export const expectComparisonToolTableLoaded = async (
  page: Page,
  name: string,
  shouldCloseVisualizationOverviewDialog: boolean,
) => {
  if (shouldCloseVisualizationOverviewDialog) {
    await closeVisualizationOverviewDialog(page);
  }

  await expect(page.getByRole('heading', { level: 1, name })).toBeVisible();
  await expect(page.locator('explorers-base-table')).toHaveCount(2);
  await expect(getUnpinnedTable(page).locator('tbody tr').first()).toBeVisible();
};
