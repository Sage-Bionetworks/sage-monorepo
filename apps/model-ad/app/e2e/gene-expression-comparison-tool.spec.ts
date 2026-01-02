import { expect, test } from '@playwright/test';
import {
  ColumnConfig,
  getQueryParamFromValues,
  testClickColumnTogglesSortOrder,
  testClickColumnUpdatesSortUrl,
  testClickDifferentColumnsReplacesSingleSort,
  testFullCaseInsensitiveMatch,
  testMetaClickBuildsMultiColumnSort,
  testMetaClickTogglesExistingSortOrder,
  testMultiColumnSortRestoredFromUrl,
  testPartialCaseInsensitiveSearch,
  testPinLastItemLastPageGoesToPreviousPage,
  testSearchExcludesPinnedItems,
  testSortRestoredFromUrl,
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

  test.describe('sort URL sync', () => {
    // Columns: 0=Gene (gene_symbol), 1=Model (name), 2=Control, 3=4 months, 4=12 months
    // Default sort: gene_symbol ASC, name ASC

    const sortColumns: ColumnConfig[] = [
      { name: '4 months', field: '4 months' },
      { name: '12 months', field: '12 months' },
      { name: 'Control', field: 'matched_control' },
    ];

    test('clicking column updates URL with sortFields and sortOrders', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testClickColumnUpdatesSortUrl(page, sortColumns[0].name, sortColumns[0].field);
    });

    test('clicking same column toggles between descending and ascending', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testClickColumnTogglesSortOrder(page, sortColumns[0].name, sortColumns[0].field);
    });

    test('sort is restored from URL on page load', async ({ page }) => {
      await navigateToComparison(
        page,
        CT_PAGE,
        true,
        'url',
        `${categoriesQueryParams}&sortFields=4%20months&sortOrders=1`,
      );
      await testSortRestoredFromUrl(page, sortColumns[0].name, sortColumns[0].field);
    });

    test('clicking different columns in sequence replaces single-column sort', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testClickDifferentColumnsReplacesSingleSort(page, sortColumns);
    });

    test('Meta+click builds multi-column sort and regular click resets', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testMetaClickBuildsMultiColumnSort(page, sortColumns);
    });

    test('multi-column sort is restored from URL', async ({ page }) => {
      await navigateToComparison(
        page,
        CT_PAGE,
        true,
        'url',
        `${categoriesQueryParams}&sortFields=4%20months,12%20months&sortOrders=-1,1`,
      );
      await testMultiColumnSortRestoredFromUrl(page, sortColumns.slice(0, 2), sortColumns[2]);
    });

    test('Meta+click on existing sort columns toggles their order', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testMetaClickTogglesExistingSortOrder(page, sortColumns.slice(0, 2));
    });
  });
});
