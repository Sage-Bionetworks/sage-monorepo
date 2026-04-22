import { expect, test } from '@playwright/test';
import {
  ColumnConfig,
  getQueryParamFromValues,
  getQueryParamsFromRecords,
  runFilterPanelTests,
  testClickColumnTogglesSortOrder,
  testClickColumnUpdatesSortUrl,
  testClickDifferentColumnsReplacesSingleSort,
  testFilterSelectionRestoredFromUrl,
  testFilterSelectionUpdatesUrl,
  testFiltersRemovedFromUrlOnClearAll,
  testFullCaseInsensitiveMatch,
  testMetaClickBuildsMultiColumnSort,
  testMetaClickTogglesExistingSortOrder,
  testMultiColumnSortRestoredFromUrl,
  testPartialCaseInsensitiveSearch,
  testPinLastItemLastPageGoesToPreviousPage,
  testSearchExcludesPinnedItems,
  testSortRestoredFromUrl,
  testTableReturnsToFirstPageWhenFilterSelectedAndRemoved,
  testTableReturnsToFirstPageWhenSearchTermEnteredAndCleared,
  testTableReturnsToFirstPageWhenSortChanged,
} from '@sagebionetworks/explorers/testing/e2e';
import { fetchComparisonToolConfig, navigateToComparison } from './helpers/comparison-tool';

test.describe('specific viewport block', () => {
  test.use({ viewport: { width: 1600, height: 1200 } });

  test('has title', async ({ page }) => {
    await page.goto('/comparison/drugs');

    // Expect a title "to contain" a substring.
    await expect(page).toHaveTitle(
      "Nominated Drugs | Candidate drugs for Alzheimer's Disease treatment or prevention",
    );
  });
});

test.describe('nominated drugs - comparison tool', () => {
  const CT_PAGE = 'Nominated Drugs';

  runFilterPanelTests(async (page) => navigateToComparison(page, CT_PAGE, true, 'url'));

  test.describe('filterbox search', () => {
    const pinnedItems = [
      'CHEMBL611~null', // Terazosin
      'CHEMBL621~null', // Trazodone
    ];

    test('filterbox search without comma returns partial case-insensitive matches', async ({
      page,
    }) => {
      await navigateToComparison(page, CT_PAGE, true, 'url');
      await testPartialCaseInsensitiveSearch(page, 'raz', pinnedItems);
    });

    test('filterbox search excludes pinned items from results', async ({ page }) => {
      await navigateToComparison(
        page,
        CT_PAGE,
        true,
        'url',
        getQueryParamFromValues(pinnedItems, 'pinned'),
      );
      await testSearchExcludesPinnedItems(page, pinnedItems, 'raz', 'terazosin,');
    });

    test('filterbox search with commas returns full, case-insensitive matches', async ({
      page,
    }) => {
      const pinnedItems = [
        'CHEMBL457~null', // Gemfibrozil
        'CHEMBL19449~null', // Ibudilast
        'CHEMBL1371770~null', // Piribedil
      ];
      await navigateToComparison(page, CT_PAGE, true, 'url');
      await testFullCaseInsensitiveMatch(
        page,
        'ibudilast,gemfibrozil,piribedil',
        pinnedItems,
        'CHEMBL545437~null', // Amibegron
      );
    });
  });

  test.describe('pagination', () => {
    test('table loads previous page when last item on last page is pinned', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true, 'url');
      await testPinLastItemLastPageGoesToPreviousPage(page);
    });

    test('table returns to first page when filter selected and removed', async ({ page }) => {
      const configs = await fetchComparisonToolConfig(page, CT_PAGE);
      const config = configs[0];
      const filters = config.filters;
      expect(filters.length).toBeGreaterThan(1);
      const filter = filters[0];

      await navigateToComparison(page, CT_PAGE, true, 'url');
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

    test('table returns to first page when sort changed', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testTableReturnsToFirstPageWhenSortChanged(page);
    });
  });

  test.describe('sort URL sync', () => {
    // Non-default sort columns that are present on the page and can be sorted in tests
    const sortColumns: ColumnConfig[] = [
      { name: 'First Nominated', field: 'initial_nomination' },
      { name: 'Principal Investigators', field: 'principal_investigators' },
      { name: 'Modality', field: 'modality' },
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
        'sortFields=initial_nomination&sortOrders=1',
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
        'sortFields=initial_nomination,principal_investigators&sortOrders=-1,1',
      );
      await testMultiColumnSortRestoredFromUrl(page, sortColumns.slice(0, 2), sortColumns[2]);
    });

    test('Meta+click on existing sort columns toggles their order', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testMetaClickTogglesExistingSortOrder(page, sortColumns.slice(0, 2));
    });
  });

  test.describe('filter URL sync', () => {
    test('filter selections are added to URL when selected and removed when cleared', async ({
      page,
    }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testFilterSelectionUpdatesUrl(page, 'nominatingPis', 'Nominating PI', 'Mark Albers');
    });

    test('filter selections are restored from URL on page load', async ({ page }) => {
      const expectedFilterParams = {
        nominatingPis: ['Mark Albers', 'Jan Krumsiek', 'Lei Xie'],
      };
      const expectedSelectedFilters = {
        'Nominating PI': ['Mark Albers', 'Jan Krumsiek', 'Lei Xie'],
      };

      await navigateToComparison(
        page,
        CT_PAGE,
        true,
        'url',
        getQueryParamsFromRecords(expectedFilterParams),
      );
      await testFilterSelectionRestoredFromUrl(page, expectedFilterParams, expectedSelectedFilters);
    });

    test('filters are removed from URL when Clear All is clicked', async ({ page }) => {
      const expectedInitialFilterParams = {
        nominations: ['2'],
        nominatingPis: ['Jan Krumsiek', 'Marina Sirota'],
      };

      await navigateToComparison(
        page,
        CT_PAGE,
        true,
        'url',
        getQueryParamsFromRecords(expectedInitialFilterParams),
      );
      await testFiltersRemovedFromUrlOnClearAll(page, expectedInitialFilterParams);
    });
  });
});
