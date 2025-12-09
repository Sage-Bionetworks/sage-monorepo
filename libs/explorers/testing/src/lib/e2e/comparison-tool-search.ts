import { expect, Page } from '@playwright/test';
import {
  expectNoResultsFound,
  expectPinnedParams,
  expectPinnedRows,
  getRowByName,
  getUnpinnedTable,
  pinAll,
  searchViaFilterbox,
} from './comparison-tool';

export async function testPartialCaseInsensitiveSearch(
  page: Page,
  searchTerm: string,
  expectedMatchNames: string[],
) {
  await searchViaFilterbox(page, searchTerm);

  const unpinnedTable = getUnpinnedTable(page);

  for (const name of expectedMatchNames) {
    const row = getRowByName(unpinnedTable, page, name);
    await expect(row).toBeVisible();
  }

  const nResults = expectedMatchNames.length;
  await expect(page.getByText(`1-${nResults} of ${nResults}`)).toBeVisible();
}

export async function testSearchExcludesPinnedItems(
  page: Page,
  pinnedItems: string[],
  partialSearchTerm: string,
  fullMatchSearchTerms: string,
) {
  await expectPinnedParams(page, pinnedItems);
  await expectPinnedRows(page, pinnedItems);

  await searchViaFilterbox(page, partialSearchTerm);
  await expectNoResultsFound(page);

  await searchViaFilterbox(page, fullMatchSearchTerms);
  await expectNoResultsFound(page);
}

export async function testFullCaseInsensitiveMatch(
  page: Page,
  searchTerms: string,
  expectedVisibleItems: string[],
  expectedHiddenItem?: string,
) {
  await searchViaFilterbox(page, searchTerms);

  const unpinnedTable = getUnpinnedTable(page);

  for (const itemName of expectedVisibleItems) {
    await expect(getRowByName(unpinnedTable, page, itemName)).toBeVisible();
  }

  if (expectedHiddenItem) {
    await expect(getRowByName(unpinnedTable, page, expectedHiddenItem)).toBeHidden();
  }

  await pinAll(page);

  await expectPinnedRows(page, expectedVisibleItems);
  await expectPinnedParams(page, expectedVisibleItems);
}
