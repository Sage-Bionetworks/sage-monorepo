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

// TODO: remove test.fail when MG-602 is resolved
const testFailDescription =
  'Test will fail until data is updated to remove duplicate objects with the same row id (MG-602)';
test.describe('gene expression', () => {
  test.fail(
    'filterbox search without comma returns partial case-insensitive matches',
    { annotation: { type: 'fail', description: testFailDescription } },
    async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);

      await testPartialCaseInsensitiveSearch(page, 'acul', [
        'ENSMUSG00000033417~5xFAD (IU/Jax/Pitt)',
        'ENSMUSG00000033417~LOAD1',
      ]); // Cacul1
    },
  );

  test.fail(
    'filterbox search excludes pinned items from results',
    { annotation: { type: 'fail', description: testFailDescription } },
    async ({ page }) => {
      const pinnedCorrelations = [
        'ENSMUSG00000033417~5xFAD (IU/Jax/Pitt)',
        'ENSMUSG00000033417~LOAD1',
      ]; // Cacul1

      await navigateToComparison(
        page,
        CT_PAGE,
        true,
        'url',
        getQueryParamFromValues(pinnedCorrelations, 'pinned'),
      );

      await testSearchExcludesPinnedItems(page, pinnedCorrelations, 'acul', 'cacul1,');
    },
  );

  test.fail(
    'filterbox search with commas returns full, case-insensitive matches',
    { annotation: { type: 'fail', description: testFailDescription } },
    async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);

      await testFullCaseInsensitiveMatch(
        page,
        'fer,',
        ['ENSMUSG00000000127~5xFAD (IU/Jax/Pitt)', 'ENSMUSG00000000127~LOAD1'], // Fer
        'ENSMUSG00000024965~5xFAD (IU/Jax/Pitt)', // Fermt3
      );
    },
  );

  test.fail(
    'filterbox search partial case-insensitive matches with special characters',
    { annotation: { type: 'fail', description: testFailDescription } },
    async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);

      await testPartialCaseInsensitiveSearch(page, '(r', [
        'ENSMUSG00000086429~5xFAD (IU/Jax/Pitt)',
        'ENSMUSG00000086429~LOAD1',
      ]); // Gt(ROSA)26Sor
    },
  );

  test('table loads previous page when last item on last page is pinned', async ({ page }) => {
    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const categories = configs[1]?.dropdowns;
    expect(categories.length).toBeGreaterThan(1);

    await navigateToComparison(
      page,
      CT_PAGE,
      true,
      'url',
      getQueryParamFromValues(categories, 'categories'),
    );
    await testPinLastItemLastPageGoesToPreviousPage(page);
  });

  test('table returns to first page when filter selected and removed', async ({ page }) => {
    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const filters = configs[0]?.filters;
    expect(filters.length).toBeGreaterThan(1);
    const filter = filters[0];

    await navigateToComparison(page, CT_PAGE, true);
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
