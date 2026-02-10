import { expect, Locator, Page } from '@playwright/test';
import { RESERVED_COMPARISON_TOOL_QUERY_PARAM_KEYS } from '@sagebionetworks/explorers/constants';

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

export const expectUnpinnedTableOnly = async (page: Page): Promise<void> => {
  await expect(page.locator('explorers-base-table').filter({ visible: true })).toHaveCount(1);
  await expect(getUnpinnedTable(page).locator('tbody tr').first()).toBeVisible();
};

export const getRowByName = (table: Locator, page: Page, name: string): Locator =>
  table.locator('tbody tr').filter({
    has: page.getByRole('cell', { name, exact: true }),
  });

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
  await expect(page.getByText('0-0 of 0')).toBeVisible();
  await expect(page.getByText('No results found')).toBeVisible();
};

export async function goToLastPage(page: Page) {
  const lastPageBtn = page.getByRole('button', { name: /last page/i });
  await expect(lastPageBtn).toBeEnabled();

  await lastPageBtn.click();
  await expect(lastPageBtn).toBeDisabled();

  // Wait for the paginator to show a non-first page (text should NOT start with "1-")
  await expect(page.locator('.p-paginator-current')).not.toContainText(/^1-/);
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
  await expect(firstPageBtn).toBeDisabled();
  await expect(page.getByRole('button', { name: /previous page/i })).toBeDisabled();
  // Wait for paginator to show first page range (e.g., "1-10 of X")
  await expect(page.locator('.p-paginator-current')).toContainText(/^1-/);
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
  await expect(lastPageBtn).toBeEnabled();

  const unpinnedTable = getUnpinnedTable(page);

  const firstCellName = await unpinnedTable.getByRole('group').first().getAttribute('aria-label');
  const firstRow = getRowByName(unpinnedTable, page, firstCellName || '');
  await expect(firstRow).toBeVisible();

  await lastPageBtn.click();
  await expect(lastPageBtn).toBeDisabled();
  await expect(firstRow).not.toBeVisible(); // wait for first row to no longer be visible, so data has loaded

  const pinButtonsCount = await unpinnedTable.getByRole('button', { name: 'Pin' }).count();

  for (let i = 0; i < pinButtonsCount; i++) {
    await expect(unpinnedTable.getByRole('row')).toHaveCount(pinButtonsCount - i);
    const pinButton = unpinnedTable.getByRole('button', { name: 'Pin' }).first();
    await pinButton.focus();
    await pinButton.press('Enter');
  }

  await expect(unpinnedTable.getByRole('row')).toHaveCount(10); // previous full page loaded
  await expect(lastPageBtn).toBeDisabled();
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
  const filterPanel2 = await openFilterMenuAndClickCheckbox(page, filterName, filterValue);
  // Close filter panel to ensure UI has settled
  await toggleFilterPanel(page);
  await expect(filterPanel2).toBeHidden();
  await expectFirstPage(page);
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
 * Clicks a column header to sort by that column.
 * @param page - Playwright Page object
 * @param columnName - The accessible name of the column header to click
 * @param multiSort - If true, uses Meta+click to add to existing sort (multi-column sort).
 *                    If false (default), performs a regular click to replace the current sort.
 */
export async function sortColumn(page: Page, columnName: string, multiSort = false): Promise<void> {
  const columnHeader = page.getByRole('columnheader', { name: columnName });
  if (multiSort) {
    await columnHeader.click({ modifiers: ['Meta'] });
  } else {
    await columnHeader.click();
  }
}
