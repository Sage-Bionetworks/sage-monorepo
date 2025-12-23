import { expect, Locator, Page } from '@playwright/test';

export const getQueryParamFromValues = (values: string[], key: string): string => {
  // Query parameter values are encoded once by CT URL service and again by Angular router
  return `${key}=${values.map((value) => encodeURIComponent(encodeURIComponent(value))).join(',')}`;
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
}

export async function clickFilterCheckbox(page: Page, filterMenuName: string, filterName: string) {
  const filterButton = page.getByRole('button', { name: 'Filter Results' });
  await filterButton.click();

  const filterPanelMain = page.locator('.filter-panel-main');
  await expect(filterPanelMain).toBeVisible();

  const filterMenuButtonNameRegex = new RegExp(filterMenuName, 'i');
  const filterMenuButton = filterPanelMain.getByRole('button', {
    name: filterMenuButtonNameRegex,
  });
  await filterMenuButton.click();

  const filterPanelSecondary = page.locator('.filter-panel-pane').filter({ visible: true });
  await expect(filterPanelSecondary.getByText(filterMenuName)).toBeVisible();
  const filterCheckboxNameRegex = new RegExp(filterName, 'i');
  const filterCheckbox = filterPanelSecondary.getByRole('checkbox', {
    name: filterCheckboxNameRegex,
  });
  await filterCheckbox.click();
}

export async function expectFirstPage(page: Page) {
  const firstPageBtn = page.getByRole('button', { name: /first page/i });
  await expect(firstPageBtn).toBeDisabled();
  await expect(page.getByRole('button', { name: /previous page/i })).toBeDisabled();
}

export async function testPinLastItemLastPageGoesToPreviousPage(page: Page) {
  const lastPageBtn = page.getByRole('button', { name: /last page/i });
  await expect(lastPageBtn).toBeEnabled();

  await lastPageBtn.click();
  await expect(lastPageBtn).toBeDisabled();

  const unpinnedTable = getUnpinnedTable(page);
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

  await clickFilterCheckbox(page, filterName, filterValue);
  await expectFirstPage(page);

  await goToLastPage(page);

  await clickFilterCheckbox(page, filterName, filterValue);
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

  const dropdown = page.getByRole('combobox').last();
  await dropdown.click();
  const options = page.getByRole('option');
  const secondOption = options.nth(1);
  await secondOption.click();
  await expect(secondOption).toBeHidden();

  await expectFirstPage(page);
}

export async function testTableReturnsToFirstPageWhenSortChanged(page: Page) {
  await goToLastPage(page);

  const columnHeader = page.getByRole('columnheader').nth(2);
  await columnHeader.click();

  await expectFirstPage(page);
}
