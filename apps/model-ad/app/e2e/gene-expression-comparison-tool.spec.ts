import { expect, test } from '@playwright/test';
import {
  getQueryParamFromValues,
  testFullCaseInsensitiveMatch,
  testPartialCaseInsensitiveSearch,
  testPinLastItemLastPageGoesToPreviousPage,
  testSearchExcludesPinnedItems,
  testTableReturnsToFirstPageWhenCategoriesChanged,
  testTableReturnsToFirstPageWhenFilterSelectedAndRemoved,
  testTableReturnsToFirstPageWhenSearchTermEnteredAndCleared,
  testTableReturnsToFirstPageWhenSortChanged,
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
