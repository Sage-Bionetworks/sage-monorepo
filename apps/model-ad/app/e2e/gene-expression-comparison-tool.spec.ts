import { expect, test } from '@playwright/test';
import {
  expectPinnedParams,
  expectPinnedRows,
  getPinnedTable,
  getQueryParamFromValues,
  getUnpinnedTable,
  pinByName,
  searchViaFilterbox,
  testFullCaseInsensitiveMatch,
  testPartialCaseInsensitiveSearch,
  testPinLastItemLastPageGoesToPreviousPage,
  testSearchExcludesPinnedItems,
  testTableReturnsToFirstPageWhenCategoriesChanged,
  testTableReturnsToFirstPageWhenFilterSelectedAndRemoved,
  testTableReturnsToFirstPageWhenSearchTermEnteredAndCleared,
  testTableReturnsToFirstPageWhenSortChanged,
  unPinByName,
} from '@sagebionetworks/explorers/testing/e2e';
import { fetchComparisonToolConfig, navigateToComparison } from './helpers/comparison-tool';

const CT_PAGE = 'Gene Expression';
const categories = [
  'RNA - DIFFERENTIAL EXPRESSION',
  'Tissue - Hippocampus',
  'Sex - Females & Males',
];
const categoriesQueryParams = getQueryParamFromValues(categories, 'categories');
const cacul1Matches = [
  'ENSMUSG00000033417~3xTg-AD',
  'ENSMUSG00000033417~5xFAD (UCI)',
  'ENSMUSG00000033417~Abca7*V1599M',
  'ENSMUSG00000033417~Abca7*V1599M.5xFAD',
  'ENSMUSG00000033417~Trem2-R47H_NSS',
  'ENSMUSG00000033417~Trem2-R47H_NSS.5xFAD',
];

test.describe('gene expression', () => {
  test('filterbox search without comma returns partial case-insensitive matches', async ({
    page,
  }) => {
    await navigateToComparison(page, CT_PAGE, true, 'url', categoriesQueryParams);
    await testPartialCaseInsensitiveSearch(page, 'acul', cacul1Matches);
  });

  test('filterbox search excludes pinned items from results', async ({ page }) => {
    const queryParameters = [
      categoriesQueryParams,
      getQueryParamFromValues(cacul1Matches, 'pinned'),
    ].join('&');
    await navigateToComparison(page, CT_PAGE, true, 'url', queryParameters);
    await testSearchExcludesPinnedItems(page, cacul1Matches, 'acul', 'cacul1,');
  });

  test('filterbox search with commas returns full, case-insensitive matches', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true, 'url', categoriesQueryParams);
    await testFullCaseInsensitiveMatch(
      page,
      'fer,',
      [
        'ENSMUSG00000000127~3xTg-AD',
        'ENSMUSG00000000127~5xFAD (UCI)',
        'ENSMUSG00000000127~Abca7*V1599M',
        'ENSMUSG00000000127~Abca7*V1599M.5xFAD',
        'ENSMUSG00000000127~Trem2-R47H_NSS',
        'ENSMUSG00000000127~Trem2-R47H_NSS.5xFAD',
      ], // Fer
      'ENSMUSG00000027356~3xTg-AD', // Fermt1
    );
  });

  test('filterbox search partial case-insensitive matches with special characters', async ({
    page,
  }) => {
    await navigateToComparison(page, CT_PAGE, true, 'url', categoriesQueryParams);
    await testPartialCaseInsensitiveSearch(page, '(r', [
      'ENSMUSG00000086429~3xTg-AD',
      'ENSMUSG00000086429~5xFAD (UCI)',
      'ENSMUSG00000086429~Abca7*V1599M',
      'ENSMUSG00000086429~Abca7*V1599M.5xFAD',
      'ENSMUSG00000086429~Trem2-R47H_NSS',
      'ENSMUSG00000086429~Trem2-R47H_NSS.5xFAD',
    ]); // Gt(ROSA)26Sor
  });

  test('pinned items are cached when switching between categories', async ({ page }) => {
    const firstCategories = [
      'RNA - DIFFERENTIAL EXPRESSION',
      'Tissue - Hippocampus',
      'Sex - Females & Males',
    ];
    const pinnedItems = ['ENSMUSG00000000001~3xTg-AD', 'ENSMUSG00000000001~5xFAD (UCI)']; // Gnai3
    const queryParameters = [
      getQueryParamFromValues(firstCategories, 'categories'),
      getQueryParamFromValues(pinnedItems, 'pinned'),
    ].join('&');

    await navigateToComparison(page, CT_PAGE, true, 'url', queryParameters);
    await expectPinnedRows(page, pinnedItems);
    await expectPinnedParams(page, pinnedItems);

    const dropdown = page.getByRole('combobox').last();
    await dropdown.click();
    const malesOption = page.getByRole('option', { name: /sex - males/i });
    await malesOption.click();
    await expect(malesOption).toBeHidden();

    await expectPinnedRows(page, pinnedItems);
    await expectPinnedParams(page, pinnedItems);

    await dropdown.click();
    const femalesAndMalesOption = page.getByRole('option', { name: /sex - females & males/i });
    await femalesAndMalesOption.click();
    await expect(femalesAndMalesOption).toBeHidden();

    await expectPinnedRows(page, pinnedItems);
    await expectPinnedParams(page, pinnedItems);
  });

  test('pinned items cache is reset when new item is pinned', async ({ page }) => {
    const firstCategories = [
      'RNA - DIFFERENTIAL EXPRESSION',
      'Tissue - Hippocampus',
      'Sex - Females & Males',
    ];
    const initialPinned = ['ENSMUSG00000000001~3xTg-AD']; // Gnai3
    const afterPinPinned = ['ENSMUSG00000000001~3xTg-AD', 'ENSMUSG00000000001~5xFAD (UCI)'];

    const queryParameters = [
      getQueryParamFromValues(firstCategories, 'categories'),
      getQueryParamFromValues(initialPinned, 'pinned'),
    ].join('&');

    await navigateToComparison(page, CT_PAGE, true, 'url', queryParameters);
    await expectPinnedRows(page, initialPinned);
    await expectPinnedParams(page, initialPinned);

    const dropdown = page.getByRole('combobox').last();
    await dropdown.click();
    const malesOption = page.getByRole('option', { name: /sex - males/i });
    await malesOption.click();
    await expect(malesOption).toBeHidden();

    await expectPinnedRows(page, initialPinned);
    await expectPinnedParams(page, initialPinned);

    await searchViaFilterbox(page, 'gnai3');
    await pinByName(getUnpinnedTable(page), page, 'ENSMUSG00000000001~5xFAD (UCI)');
    await expectPinnedRows(page, afterPinPinned);
    await expectPinnedParams(page, afterPinPinned);

    await dropdown.click();
    const femalesAndMalesOption = page.getByRole('option', { name: /sex - females & males/i });
    await femalesAndMalesOption.click();
    await expect(femalesAndMalesOption).toBeHidden();

    await expectPinnedRows(page, afterPinPinned);
    await expectPinnedParams(page, afterPinPinned);
  });

  test('pinned items cache is reset when new item is unpinned', async ({ page }) => {
    const firstCategories = [
      'RNA - DIFFERENTIAL EXPRESSION',
      'Tissue - Hippocampus',
      'Sex - Females & Males',
    ];
    const initialPinned = ['ENSMUSG00000000001~3xTg-AD', 'ENSMUSG00000000001~5xFAD (UCI)']; // Gnai3
    const afterUnpinPinned = ['ENSMUSG00000000001~3xTg-AD'];

    const queryParameters = [
      getQueryParamFromValues(firstCategories, 'categories'),
      getQueryParamFromValues(initialPinned, 'pinned'),
    ].join('&');

    await navigateToComparison(page, CT_PAGE, true, 'url', queryParameters);
    await expectPinnedRows(page, initialPinned);
    await expectPinnedParams(page, initialPinned);

    const dropdown = page.getByRole('combobox').last();
    await dropdown.click();
    const malesOption = page.getByRole('option', { name: /sex - males/i });
    await malesOption.click();
    await expect(malesOption).toBeHidden();

    await expectPinnedRows(page, initialPinned);
    await expectPinnedParams(page, initialPinned);

    await unPinByName(getPinnedTable(page), page, 'ENSMUSG00000000001~5xFAD (UCI)');
    await expectPinnedRows(page, afterUnpinPinned);
    await expectPinnedParams(page, afterUnpinPinned);

    await dropdown.click();
    const femalesAndMalesOption = page.getByRole('option', { name: /sex - females & males/i });
    await femalesAndMalesOption.click();
    await expect(femalesAndMalesOption).toBeHidden();

    await expectPinnedRows(page, afterUnpinPinned);
    await expectPinnedParams(page, afterUnpinPinned);
  });

  test('pinned table and URL should only include currently visible pinned items from cache', async ({
    page,
  }) => {
    const firstCategories = [
      'RNA - DIFFERENTIAL EXPRESSION',
      'Tissue - Hippocampus',
      'Sex - Females & Males',
    ];
    const firstPinned = [
      'ENSMUSG00000000001~3xTg-AD',
      'ENSMUSG00000000001~5xFAD (UCI)',
      'ENSMUSG00000000001~Abca7*V1599M',
    ]; // Gnai3
    const queryParameters = [
      getQueryParamFromValues(firstCategories, 'categories'),
      getQueryParamFromValues(firstPinned, 'pinned'),
    ].join('&');
    const expectedSecondPinned = ['ENSMUSG00000000001~5xFAD (UCI)'];

    await navigateToComparison(page, CT_PAGE, true, 'url', queryParameters);
    await expectPinnedRows(page, firstPinned);
    await expectPinnedParams(page, firstPinned);

    const dropdown = page.getByRole('combobox').first();
    await dropdown.click();
    const cerebralCortexOption = page.getByRole('option', { name: /tissue - cerebral cortex/i });
    await cerebralCortexOption.click();
    await expect(cerebralCortexOption).toBeHidden();

    await expectPinnedRows(page, expectedSecondPinned);
    await expectPinnedParams(page, expectedSecondPinned);

    await dropdown.click();
    const hippocampusOption = page.getByRole('option', { name: /tissue - hippocampus/i });
    await hippocampusOption.click();
    await expect(hippocampusOption).toBeHidden();

    await expectPinnedRows(page, firstPinned);
    await expectPinnedParams(page, firstPinned);
  });

  test('table loads previous page when last item on last page is pinned', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true, 'url', categoriesQueryParams);
    await testPinLastItemLastPageGoesToPreviousPage(page);
  });

  test('table returns to first page when filter selected and removed', async ({ page }) => {
    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const config = configs[0];
    const categories = config.dropdowns;
    const filters = config.filters;
    expect(filters.length).toBeGreaterThan(1);
    const filter = filters[0];

    await navigateToComparison(
      page,
      CT_PAGE,
      true,
      'url',
      getQueryParamFromValues(categories, 'categories'),
    );
    await testTableReturnsToFirstPageWhenFilterSelectedAndRemoved(
      page,
      filter.name,
      filter.values[0],
    );
  });

  test('table returns to first page when search term entered and cleared', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true);
    await testTableReturnsToFirstPageWhenSearchTermEnteredAndCleared(page);
  });

  test('table returns to first page when categories changed', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true);
    await testTableReturnsToFirstPageWhenCategoriesChanged(page);
  });

  test('table returns to first page when sort changed', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true);
    await testTableReturnsToFirstPageWhenSortChanged(page);
  });
});
