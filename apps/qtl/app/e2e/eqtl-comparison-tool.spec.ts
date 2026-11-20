import { expect, Page, test } from '@playwright/test';
import { DEFAULT_PAGE_SIZE, MAX_PIN_LIMIT } from '@sagebionetworks/explorers/constants';
import {
  clickRowByName,
  closeTutorialDialog,
  ColumnConfig,
  expectPinnedParams,
  expectSelectedRow,
  getHoveredRows,
  getPinnedTable,
  getQueryParamsFromRecords,
  getRowByName,
  getSelectedRows,
  getUnpinnedTable,
  hoverRowByName,
  openFilterMenuAndClickCheckbox,
  pinByName,
  runFilterPanelTests,
  STANDARD_NO_RESULTS_MESSAGE,
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

const CELL_SUBCLASS_FILTER = {
  menuName: 'Cell Subclass',
  queryParamKey: 'cellSubclass',
  value: 'Astrocyte',
};

// Neither shares a cell class with CELL_CLASS_FILTER's Astrocyte rows, and no sample row is an
// Immune Cell
const EXCITATORY_NEURON_CELL_CLASS = 'Excitatory Neuron';
const IMMUNE_CELL_CLASS = 'Immune Cell';

// How long a row click is given to trigger a results request that shouldn't happen
const NO_REFETCH_WAIT_MS = 1000;

// Long enough for the results to land first when an unpin refetches both tables
const PINNED_RESPONSE_DELAY_MS = 2000;

const delayPinnedResponses = (page: Page) =>
  page.route(
    (url) =>
      url.pathname.endsWith('/comparison-tools/eqtl') &&
      url.searchParams.get('itemFilterType') === 'include',
    async (route) => {
      await new Promise((resolve) => setTimeout(resolve, PINNED_RESPONSE_DELAY_MS));
      await route.continue();
    },
  );

// Pins the first result and an Astrocyte row, and selects the Astrocyte one
const pinTwoAndSelectAstrocyte = async (page: Page) => {
  const astrocyte = await findUnremarkableAstrocyte(page);
  const [first] = await fetchFirstPage(page);
  await navigateToEqtlComparison(page, true);
  for (const eqtl of [first, astrocyte]) {
    await pinByName(getUnpinnedTable(page), page, eqtl.composite_id);
    await expect(getRowByName(getPinnedTable(page), page, eqtl.composite_id)).toHaveCount(1);
  }
  await clickRowByName(getPinnedTable(page), page, astrocyte.composite_id);
  await expectSelectedRow(page, astrocyte.composite_id);
  return astrocyte;
};

const filterByCellClassValue = async (page: Page, value: string) => {
  await openFilterMenuAndClickCheckbox(page, CELL_CLASS_FILTER.menuName, value);
  await toggleFilterPanel(page);
};

const filterByCellClass = (page: Page) => filterByCellClassValue(page, CELL_CLASS_FILTER.value);

const fetchEqtlsInCellClass = (page: Page, value: string) =>
  fetchEqtls(page, {}, { [CELL_CLASS_FILTER.queryParamKey]: [value] });

// The rows on the first page of results, in the default sort
const fetchFirstPage = async (page: Page) => (await fetchEqtls(page)).slice(0, DEFAULT_PAGE_SIZE);

// A first-page Astrocyte row that is neither of the first two results nor the first Astrocyte
// result, so a wrongly reset selection can't land on it by chance, even with the first result pinned
const findUnremarkableAstrocyte = async (page: Page) => {
  const [firstAstrocyte] = await fetchEqtlsInCellClass(page, CELL_CLASS_FILTER.value);
  const astrocyte = (await fetchFirstPage(page))
    .slice(2)
    .find(
      (eqtl) =>
        eqtl.cell_class === CELL_CLASS_FILTER.value &&
        eqtl.composite_id !== firstAstrocyte.composite_id,
    );
  expect(astrocyte).toBeDefined();
  return astrocyte as NonNullable<typeof astrocyte>;
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
      [CELL_SUBCLASS_FILTER.queryParamKey]: [CELL_SUBCLASS_FILTER.value],
    };
    const selectedFilters = {
      [CELL_CLASS_FILTER.menuName]: [CELL_CLASS_FILTER.value],
      [CELL_SUBCLASS_FILTER.menuName]: [CELL_SUBCLASS_FILTER.value],
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
      await navigateToEqtlComparison(
        page,
        true,
        'url',
        `sortFields=${sortColumns[0].field}&sortOrders=1`,
      );
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
      await navigateToEqtlComparison(
        page,
        true,
        'url',
        `sortFields=${sortColumns[0].field},${sortColumns[1].field}&sortOrders=-1,1`,
      );
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

  test.describe('row selection', () => {
    test('the first result is selected on load', async ({ page }) => {
      const [first] = await fetchFirstPage(page);

      await navigateToEqtlComparison(page, true);

      await expectSelectedRow(page, first.composite_id);
    });

    test('nothing is selected when only pinned rows are showing', async ({ page }) => {
      const [first] = await fetchFirstPage(page);
      expect(await fetchEqtlsInCellClass(page, IMMUNE_CELL_CLASS)).toHaveLength(0);
      const queryParameters = getQueryParamsFromRecords({
        pinned: [first.composite_id],
        [CELL_CLASS_FILTER.queryParamKey]: [IMMUNE_CELL_CLASS],
      });

      // navigateToEqtlComparison waits for a result row, and this view has none by design
      await page.goto(`${EQTL_CT_PATH}?${queryParameters}`);
      await closeTutorialDialog(page);
      await expect(page.getByText(STANDARD_NO_RESULTS_MESSAGE)).toBeVisible();

      await expect(getRowByName(getPinnedTable(page), page, first.composite_id)).toHaveCount(1);
      await expect(getSelectedRows(page)).toHaveCount(0);
    });

    test('clicking a result selects it, and clicking it again keeps it selected', async ({
      page,
    }) => {
      const [, second] = await fetchFirstPage(page);

      await navigateToEqtlComparison(page, true);
      await clickRowByName(getUnpinnedTable(page), page, second.composite_id);
      await expectSelectedRow(page, second.composite_id);

      await clickRowByName(getUnpinnedTable(page), page, second.composite_id);
      await expectSelectedRow(page, second.composite_id);
    });

    test('clicking a pinned row selects it', async ({ page }) => {
      const [first] = await fetchFirstPage(page);

      await navigateToEqtlComparison(page, true, 'url', `pinned=${first.composite_id}`);
      await clickRowByName(getPinnedTable(page), page, first.composite_id);

      await expectSelectedRow(page, first.composite_id);
    });

    test('hovering a row highlights it only while hovered', async ({ page }) => {
      const [, , third] = await fetchFirstPage(page);

      await navigateToEqtlComparison(page, true);
      await hoverRowByName(getUnpinnedTable(page), page, third.composite_id);
      await expect(getHoveredRows(page)).toHaveCount(1);
      await expect(getRowByName(getUnpinnedTable(page), page, third.composite_id)).toHaveClass(
        /hovered/,
      );

      await page.mouse.move(0, 0);
      await expect(getHoveredRows(page)).toHaveCount(0);
    });

    test('the selected row is highlighted only on its own page', async ({ page }) => {
      const [, , third] = await fetchFirstPage(page);
      const unpinnedTable = getUnpinnedTable(page);

      await navigateToEqtlComparison(page, true);
      await clickRowByName(unpinnedTable, page, third.composite_id);

      await page.getByRole('button', { name: /next page/i }).click();
      await expect(getRowByName(unpinnedTable, page, third.composite_id)).toHaveCount(0);
      await expect(getSelectedRows(page)).toHaveCount(0);

      await page.getByRole('button', { name: /previous page/i }).click();
      await expectSelectedRow(page, third.composite_id);
    });

    test('a filter that keeps the selected row leaves it selected', async ({ page }) => {
      const astrocyte = await findUnremarkableAstrocyte(page);

      await navigateToEqtlComparison(page, true);
      await clickRowByName(getUnpinnedTable(page), page, astrocyte.composite_id);
      await filterByCellClass(page);

      await expectSelectedRow(page, astrocyte.composite_id);
    });

    test('a filter that removes the selected row selects the first new result', async ({
      page,
    }) => {
      const astrocyte = await findUnremarkableAstrocyte(page);
      const [firstExcitatory] = await fetchEqtlsInCellClass(page, EXCITATORY_NEURON_CELL_CLASS);

      await navigateToEqtlComparison(page, true);
      await clickRowByName(getUnpinnedTable(page), page, astrocyte.composite_id);
      await filterByCellClassValue(page, EXCITATORY_NEURON_CELL_CLASS);

      await expectSelectedRow(page, firstExcitatory.composite_id);
    });

    test('a filter with no results selects nothing, and clearing it selects the first result', async ({
      page,
    }) => {
      const [first, , third] = await fetchFirstPage(page);
      expect(await fetchEqtlsInCellClass(page, IMMUNE_CELL_CLASS)).toHaveLength(0);

      await navigateToEqtlComparison(page, true);
      await clickRowByName(getUnpinnedTable(page), page, third.composite_id);
      await filterByCellClassValue(page, IMMUNE_CELL_CLASS);
      await expect(getRowByName(getUnpinnedTable(page), page, third.composite_id)).toHaveCount(0);
      await expect(getSelectedRows(page)).toHaveCount(0);

      await page.locator('.filter-list').getByRole('button', { name: 'Clear all' }).click();
      await expectSelectedRow(page, first.composite_id);
    });

    test('a pinned selected row stays selected when a filter excludes it', async ({ page }) => {
      const astrocyte = await findUnremarkableAstrocyte(page);

      await navigateToEqtlComparison(page, true);
      await clickRowByName(getUnpinnedTable(page), page, astrocyte.composite_id);
      await pinByName(getUnpinnedTable(page), page, astrocyte.composite_id);
      await filterByCellClassValue(page, EXCITATORY_NEURON_CELL_CLASS);

      await expect(getRowByName(getPinnedTable(page), page, astrocyte.composite_id)).toHaveCount(1);
      await expectSelectedRow(page, astrocyte.composite_id);
    });

    test('pinning and unpinning the selected row keeps it selected', async ({ page }) => {
      const [, second] = await fetchFirstPage(page);

      await navigateToEqtlComparison(page, true);
      await clickRowByName(getUnpinnedTable(page), page, second.composite_id);

      await pinByName(getUnpinnedTable(page), page, second.composite_id);
      await expect(getRowByName(getPinnedTable(page), page, second.composite_id)).toHaveCount(1);
      await expectSelectedRow(page, second.composite_id);

      await unPinByName(getPinnedTable(page), page, second.composite_id);
      await expect(getRowByName(getUnpinnedTable(page), page, second.composite_id)).toHaveCount(1);
      await expectSelectedRow(page, second.composite_id);
    });

    for (const pinnedRowsLandLast of [false, true]) {
      test(`unpinning the selected row while a filter excludes it selects the first result${
        pinnedRowsLandLast ? ', when the results land before the pinned rows' : ''
      }`, async ({ page }) => {
        const [firstExcitatory] = await fetchEqtlsInCellClass(page, EXCITATORY_NEURON_CELL_CLASS);
        const astrocyte = await pinTwoAndSelectAstrocyte(page);
        await filterByCellClassValue(page, EXCITATORY_NEURON_CELL_CLASS);
        if (pinnedRowsLandLast) await delayPinnedResponses(page);

        await unPinByName(getPinnedTable(page), page, astrocyte.composite_id);

        await expect(getRowByName(getPinnedTable(page), page, astrocyte.composite_id)).toHaveCount(
          0,
        );
        await expectSelectedRow(page, firstExcitatory.composite_id);
      });
    }

    test('unpinning the selected row while a filter leaves no results selects nothing', async ({
      page,
    }) => {
      // The first result stays pinned, so the second is the first one left in the results
      const [, firstUnpinned] = await fetchFirstPage(page);
      expect(await fetchEqtlsInCellClass(page, IMMUNE_CELL_CLASS)).toHaveLength(0);
      const astrocyte = await pinTwoAndSelectAstrocyte(page);
      await filterByCellClassValue(page, IMMUNE_CELL_CLASS);
      await delayPinnedResponses(page);

      await unPinByName(getPinnedTable(page), page, astrocyte.composite_id);
      await expect(getRowByName(getPinnedTable(page), page, astrocyte.composite_id)).toHaveCount(0);
      await expect(getSelectedRows(page)).toHaveCount(0);

      // A selection left on the unpinned row would show again once it's back in the results
      await page.unrouteAll();
      await page.locator('.filter-list').getByRole('button', { name: 'Clear all' }).click();
      await expectSelectedRow(page, firstUnpinned.composite_id);
    });

    test('Clear All Pins while the selected row is pinned and filtered out selects the first result', async ({
      page,
    }) => {
      const [firstExcitatory] = await fetchEqtlsInCellClass(page, EXCITATORY_NEURON_CELL_CLASS);
      await pinTwoAndSelectAstrocyte(page);
      await filterByCellClassValue(page, EXCITATORY_NEURON_CELL_CLASS);

      await page.getByRole('button', { name: 'Clear All Pins' }).click();

      await expect(getPinnedTable(page).locator('tbody tr')).toHaveCount(0);
      await expectSelectedRow(page, firstExcitatory.composite_id);
    });

    test('selecting rows does not request the results again', async ({ page }) => {
      const [, second, third, fourth] = await fetchFirstPage(page);

      await navigateToEqtlComparison(page, true);
      const resultsRequested = page
        .waitForRequest((request) => request.url().includes('/comparison-tools/eqtl?'), {
          timeout: NO_REFETCH_WAIT_MS,
        })
        .then(
          () => true,
          () => false,
        );

      for (const eqtl of [second, third, fourth]) {
        await clickRowByName(getUnpinnedTable(page), page, eqtl.composite_id);
      }
      await expectSelectedRow(page, fourth.composite_id);

      expect(await resultsRequested).toBe(false);
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
