import { expect, Page, test } from '@playwright/test';
import { MAX_PIN_LIMIT } from '@sagebionetworks/explorers/constants';
import {
  ColumnConfig,
  expectPinnedParams,
  getPinnedTable,
  getQueryParamsFromRecords,
  getRowByName,
  getUnpinnedTable,
  openFilterMenuAndClickCheckbox,
  pinByName,
  runFilterPanelTests,
  STANDARD_PIN_LIMIT_COPY,
  testClickColumnTogglesSortOrder,
  testClickColumnUpdatesSortUrl,
  testClickDifferentColumnsReplacesSingleSort,
  testFilterSelectionRestoredFromUrl,
  testFilterSelectionUpdatesUrl,
  testFiltersRemovedFromUrlOnClearAll,
  testMetaClickBuildsMultiColumnSort,
  testMetaClickTogglesExistingSortOrder,
  testMultiColumnSortRestoredFromUrl,
  testPinAllExceedsLimit,
  testPinLastItemLastPageGoesToPreviousPage,
  testPinsRemovedFromUrlOnClearAllPins,
  testSortRestoredFromUrl,
  testTableReturnsToFirstPageWhenFilterSelectedAndRemoved,
  testTableReturnsToFirstPageWhenSortChanged,
  toggleFilterPanel,
  unPinByName,
} from '@sagebionetworks/explorers/testing/e2e';
import { EQTL_CT_PATH, fetchEqtls, navigateToEqtlComparison } from './helpers/comparison-tool';

const CELL_CLASS_FILTER = {
  menuName: 'Cell Class',
  queryParamKey: 'cellClass',
  value: 'Astrocyte',
};

const filterByCellClass = async (page: Page) => {
  await openFilterMenuAndClickCheckbox(page, CELL_CLASS_FILTER.menuName, CELL_CLASS_FILTER.value);
  await toggleFilterPanel(page);
};

test.describe('eQTL comparison tool', () => {
  test('has the eQTL Explorer title', async ({ page }) => {
    await navigateToEqtlComparison(page, true);
    await expect(page).toHaveTitle('eQTL Explorer');
  });

  test('is reachable from the header navigation', async ({ page }) => {
    await page.goto('/');
    await navigateToEqtlComparison(page, true, 'link');
    await expect(page).toHaveURL(EQTL_CT_PATH);
  });

  test('loads the results sorted by adjusted p-value, ascending', async ({ page }) => {
    await navigateToEqtlComparison(page, true);
    await expect(page.locator('th', { hasText: 'Adjusted P-Value' })).toHaveAttribute(
      'aria-sort',
      'ascending',
    );
  });

  runFilterPanelTests(async (page) => navigateToEqtlComparison(page, true));

  test.describe('filter URL sync', () => {
    const selectedFilterParams = {
      [CELL_CLASS_FILTER.queryParamKey]: [CELL_CLASS_FILTER.value],
      cellSubclass: ['Astrocyte'],
    };
    const selectedFilters = {
      [CELL_CLASS_FILTER.menuName]: [CELL_CLASS_FILTER.value],
      'Cell Subclass': ['Astrocyte'],
    };

    test('filter selections are added to URL when selected and removed when cleared', async ({
      page,
    }) => {
      await navigateToEqtlComparison(page, true);
      await testFilterSelectionUpdatesUrl(
        page,
        CELL_CLASS_FILTER.queryParamKey,
        CELL_CLASS_FILTER.menuName,
        CELL_CLASS_FILTER.value,
      );
    });

    test('filter selections are restored from URL on page load', async ({ page }) => {
      await navigateToEqtlComparison(
        page,
        true,
        'url',
        getQueryParamsFromRecords(selectedFilterParams),
      );
      await testFilterSelectionRestoredFromUrl(page, selectedFilterParams, selectedFilters);
    });

    test('filters are removed from URL when Clear All is clicked', async ({ page }) => {
      await navigateToEqtlComparison(
        page,
        true,
        'url',
        getQueryParamsFromRecords(selectedFilterParams),
      );
      await testFiltersRemovedFromUrlOnClearAll(page, selectedFilterParams);
    });
  });

  test.describe('sort URL sync', () => {
    const sortColumns: ColumnConfig[] = [
      { name: 'Beta', field: 'beta' },
      { name: 'Minor Allele Frequency', field: 'maf' },
      { name: 'Gene', field: 'gene_symbol' },
    ];

    test('clicking column updates URL with sortFields and sortOrders', async ({ page }) => {
      await navigateToEqtlComparison(page, true);
      await testClickColumnUpdatesSortUrl(page, sortColumns[0].name, sortColumns[0].field);
    });

    test('clicking same column toggles between descending and ascending', async ({ page }) => {
      await navigateToEqtlComparison(page, true);
      await testClickColumnTogglesSortOrder(page, sortColumns[0].name, sortColumns[0].field);
    });

    test('sort is restored from URL on page load', async ({ page }) => {
      await navigateToEqtlComparison(page, true, 'url', 'sortFields=beta&sortOrders=1');
      await testSortRestoredFromUrl(page, sortColumns[0].name, sortColumns[0].field);
    });

    test('clicking different columns in sequence replaces single-column sort', async ({ page }) => {
      await navigateToEqtlComparison(page, true);
      await testClickDifferentColumnsReplacesSingleSort(page, sortColumns);
    });

    test('Meta+click builds multi-column sort and regular click resets', async ({ page }) => {
      await navigateToEqtlComparison(page, true);
      await testMetaClickBuildsMultiColumnSort(page, sortColumns);
    });

    test('multi-column sort is restored from URL', async ({ page }) => {
      await navigateToEqtlComparison(page, true, 'url', 'sortFields=beta,maf&sortOrders=-1,1');
      await testMultiColumnSortRestoredFromUrl(page, sortColumns.slice(0, 2), sortColumns[2]);
    });

    test('Meta+click on existing sort columns toggles their order', async ({ page }) => {
      await navigateToEqtlComparison(page, true);
      await testMetaClickTogglesExistingSortOrder(page, sortColumns.slice(0, 2));
    });
  });

  test.describe('pinning', () => {
    test('pinning and unpinning items updates the pinned query param', async ({ page }) => {
      const [first, second] = await fetchEqtls(page);

      await navigateToEqtlComparison(page, true);
      const pinnedTable = getPinnedTable(page);
      const unpinnedTable = getUnpinnedTable(page);

      await pinByName(unpinnedTable, page, first.composite_id);
      await expect(getRowByName(pinnedTable, page, first.composite_id)).toHaveCount(1);
      await expectPinnedParams(page, [first.composite_id]);

      await pinByName(unpinnedTable, page, second.composite_id);
      await expectPinnedParams(page, [first.composite_id, second.composite_id]);

      const firstPinnedRow = await unPinByName(pinnedTable, page, first.composite_id);
      await expect(firstPinnedRow).toHaveCount(0);
      await expectPinnedParams(page, [second.composite_id]);
    });

    test('pinned items in the URL are restored in the UI', async ({ page }) => {
      const [first] = await fetchEqtls(page);

      await navigateToEqtlComparison(page, true, 'url', `pinned=${first.composite_id}`);

      await expect(getRowByName(getPinnedTable(page), page, first.composite_id)).toHaveCount(1);
      await expectPinnedParams(page, [first.composite_id]);
    });

    test('pinned items are removed from URL when Clear All Pins is clicked', async ({ page }) => {
      const [first] = await fetchEqtls(page);

      await navigateToEqtlComparison(page, true, 'url', `pinned=${first.composite_id}`);
      await testPinsRemovedFromUrlOnClearAllPins(page, [first.composite_id]);
    });

    // TODO(EX-10): add the Pin All across pages test once the data has a filter value matching
    // more than one page and no more than the pin limit
    test('Pin All stops at the maximum number of pinned items', async ({ page }) => {
      const eqtls = await fetchEqtls(
        page,
        { remainingBudget: MAX_PIN_LIMIT },
        { [CELL_CLASS_FILTER.queryParamKey]: [CELL_CLASS_FILTER.value] },
      );

      await navigateToEqtlComparison(page, true);
      await testPinAllExceedsLimit(
        page,
        filterByCellClass,
        eqtls.map((eqtl) => eqtl.composite_id),
        STANDARD_PIN_LIMIT_COPY,
      );
    });
  });

  test.describe('pagination', () => {
    test('table loads previous page when last item on last page is pinned', async ({ page }) => {
      await navigateToEqtlComparison(page, true);
      await testPinLastItemLastPageGoesToPreviousPage(page);
    });

    test('table returns to first page when filter selected and removed', async ({ page }) => {
      await navigateToEqtlComparison(page, true);
      await testTableReturnsToFirstPageWhenFilterSelectedAndRemoved(
        page,
        CELL_CLASS_FILTER.menuName,
        CELL_CLASS_FILTER.value,
      );
    });

    test('table returns to first page when sort changed', async ({ page }) => {
      await navigateToEqtlComparison(page, true);
      await testTableReturnsToFirstPageWhenSortChanged(page);
    });
  });
});
