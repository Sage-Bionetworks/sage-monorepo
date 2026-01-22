import { expect, test } from '@playwright/test';
import {
  ColumnConfig,
  expectPinnedParams,
  expectPinnedRows,
  expectUnpinnedTableOnly,
  getPinnedTable,
  getQueryParamsFromRecords,
  getRowByName,
  getUnpinnedTable,
  pinByName,
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
  unPinByName,
} from '@sagebionetworks/explorers/testing/e2e';
import { baseURL } from '../playwright.config';
import { COMPARISON_TOOL_PATHS } from './constants';
import {
  fetchComparisonToolConfig,
  fetchDiseaseCorrelations,
  fetchModelOverviews,
  navigateToComparison,
} from './helpers/comparison-tool';

const CT_PAGE = 'Model Overview';
const MODEL_OVERVIEW_PATH = COMPARISON_TOOL_PATHS[CT_PAGE];

test.describe('model overview', () => {
  runFilterPanelTests(async (page) => navigateToComparison(page, CT_PAGE, true));

  test('share URL button copies URL to clipboard', async ({ page, context }) => {
    await context.grantPermissions(['clipboard-read']);

    await navigateToComparison(page, CT_PAGE, true);

    const shareUrlButton = page.getByRole('button', { name: 'Share URL' });
    await expect(shareUrlButton).toBeVisible();

    await page.waitForURL(MODEL_OVERVIEW_PATH);

    await shareUrlButton.click();

    const clipboardContent = await page.evaluate(() => navigator.clipboard.readText());
    expect(clipboardContent).toEqual(`${baseURL}${MODEL_OVERVIEW_PATH}`);
  });

  test('pinning and unpinning items updates the pinned query param', async ({ page }) => {
    const models = await fetchModelOverviews(page);
    expect(models.length).toBeGreaterThan(1);

    const [firstModel, secondModel] = models;

    await navigateToComparison(page, CT_PAGE, true);

    const pinnedTable = getPinnedTable(page);
    const unpinnedTable = getUnpinnedTable(page);

    await pinByName(unpinnedTable, page, firstModel.name);
    await expect(getRowByName(pinnedTable, page, firstModel.name)).toHaveCount(1);
    await expectPinnedParams(page, [firstModel.name]);

    await pinByName(unpinnedTable, page, secondModel.name);
    await expect(getRowByName(pinnedTable, page, secondModel.name)).toHaveCount(1);
    await expectPinnedParams(page, [firstModel.name, secondModel.name]);

    const firstPinnedRow = await unPinByName(pinnedTable, page, firstModel.name);
    await expect(firstPinnedRow).toHaveCount(0);
    await expectPinnedParams(page, [secondModel.name]);

    const secondPinnedRow = await unPinByName(pinnedTable, page, secondModel.name);
    await expect(secondPinnedRow).toHaveCount(0);
    await expectPinnedParams(page, []);
  });

  test('pinned items in the URL are restored in the UI', async ({ page }) => {
    const [firstModel] = await fetchModelOverviews(page);
    expect(firstModel).toBeDefined();

    await navigateToComparison(page, CT_PAGE, true, 'url', `pinned=${firstModel.name}`);

    await expect(page.locator('explorers-base-table')).toHaveCount(2);
    await expect(getRowByName(getPinnedTable(page), page, firstModel.name)).toHaveCount(1);
    await expectPinnedParams(page, [firstModel.name]);
  });

  test('pinned items are removed from URL when navigating to another comparison tool', async ({
    page,
  }) => {
    const [firstModel] = await fetchModelOverviews(page);
    expect(firstModel).toBeDefined();

    await navigateToComparison(page, CT_PAGE, true, 'url', `pinned=${firstModel.name}`);
    await expectPinnedParams(page, [firstModel.name]);

    await navigateToComparison(page, 'Disease Correlation', true, 'link');
    await expectPinnedParams(page, []);
    await expectUnpinnedTableOnly(page);
  });

  test('pinned items are updated in URL when navigating to another comparison tool with cached pinned items', async ({
    page,
  }) => {
    const initialCT = 'Disease Correlation';

    const [firstModel] = await fetchModelOverviews(page);
    expect(firstModel).toBeDefined();

    const [firstCorrelation] = await fetchDiseaseCorrelations(page);
    expect(firstCorrelation).toBeDefined();

    await navigateToComparison(
      page,
      initialCT,
      true,
      'url',
      `pinned=${firstCorrelation.composite_id}`,
    );
    await expectPinnedParams(page, [firstCorrelation.composite_id]);

    await navigateToComparison(page, CT_PAGE, true, 'link');
    await expectPinnedParams(page, []);
    await expectUnpinnedTableOnly(page);

    await pinByName(getUnpinnedTable(page), page, firstModel.name);
    await expectPinnedRows(page, [firstModel.name]);
    await expectPinnedParams(page, [firstModel.name]);

    await navigateToComparison(page, initialCT, true, 'link');
    await expectPinnedParams(page, [firstCorrelation.composite_id]);
    await expectPinnedRows(page, [firstCorrelation.composite_id]);
  });

  test('pinned items are loaded from cache and displayed in UI and in URL when returning to the comparison tool', async ({
    page,
  }) => {
    const [firstModel] = await fetchModelOverviews(page);
    expect(firstModel).toBeDefined();

    await navigateToComparison(page, CT_PAGE, true, 'url', `pinned=${firstModel.name}`);
    await expectPinnedParams(page, [firstModel.name]);

    await navigateToComparison(page, 'Disease Correlation', true, 'link');
    await expectPinnedParams(page, []);
    await expectUnpinnedTableOnly(page);

    await navigateToComparison(page, CT_PAGE, true, 'link');
    await expectPinnedRows(page, [firstModel.name]);
    await expectPinnedParams(page, [firstModel.name]);
  });

  test('filterbox search without comma returns partial case-insensitive matches', async ({
    page,
  }) => {
    await navigateToComparison(page, CT_PAGE, true);
    await testPartialCaseInsensitiveSearch(page, 'tg-', ['3xTg-AD']);
  });

  test('filterbox search excludes pinned items from results', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true, 'url', `pinned=3xTg-AD,LOAD1`);
    await testSearchExcludesPinnedItems(page, ['LOAD1', '3xTg-AD'], 'tg-', '3xtg-ad,load1');
  });

  test('filterbox search with commas returns full, case-insensitive matches', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true);
    await testFullCaseInsensitiveMatch(
      page,
      '3xtg-ad,apoe4,fad',
      ['APOE4', '3xTg-AD'],
      '5xFAD (UCI)',
    );
  });

  test('filterbox search partial case-insensitive matches with special characters', async ({
    page,
  }) => {
    await navigateToComparison(page, CT_PAGE, true);
    await testPartialCaseInsensitiveSearch(page, '(uc', ['5xFAD (UCI)']);
  });

  test('table loads previous page when last item on last page is pinned', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true);
    await testPinLastItemLastPageGoesToPreviousPage(page);
  });

  test('table returns to first page when filter selected and removed', async ({ page }) => {
    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const filters = configs[0]?.filters;
    expect(filters.length).toBeGreaterThan(1);
    const filter = filters[1];

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

  test('table returns to first page when sort changed', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true);
    await testTableReturnsToFirstPageWhenSortChanged(page);
  });

  test.describe('sort URL sync', () => {
    // Columns: 0=Model (name), 1=Model Type, 2=Matched Controls, 3=Gene Expression, 4=Disease Correlation, ...
    // Default sort: model_type DESC, name ASC

    const sortColumns: ColumnConfig[] = [
      { name: 'Gene Expression', field: 'gene_expression' },
      { name: 'Disease Correlation', field: 'disease_correlation' },
      { name: 'Pathology', field: 'pathology' },
      { name: 'Biomarkers', field: 'biomarkers' },
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
        'sortFields=disease_correlation&sortOrders=1',
      );
      await testSortRestoredFromUrl(page, sortColumns[1].name, sortColumns[1].field);
    });

    test('clicking different columns in sequence replaces single-column sort', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testClickDifferentColumnsReplacesSingleSort(page, sortColumns.slice(0, 3));
    });

    test('Meta+click builds multi-column sort with multiple columns', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testMetaClickBuildsMultiColumnSort(page, sortColumns.slice(0, 3));
    });

    test('multi-column sort is restored from URL', async ({ page }) => {
      await navigateToComparison(
        page,
        CT_PAGE,
        true,
        'url',
        'sortFields=gene_expression,disease_correlation&sortOrders=-1,1',
      );
      await testMultiColumnSortRestoredFromUrl(page, sortColumns.slice(0, 2), sortColumns[2]);
    });

    test('Meta+click on existing sort columns toggles their order', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testMetaClickTogglesExistingSortOrder(page, sortColumns.slice(0, 3));
    });
  });

  test.describe('filter URL sync', () => {
    test('filter selections are added to URL when selected and removed when cleared', async ({
      page,
    }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testFilterSelectionUpdatesUrl(page, 'model_type', 'Model Type', 'Familial AD');
    });

    test('filter selections are restored from URL on page load', async ({ page }) => {
      const expectedFilterParams = {
        available_data: ['Biomarkers', 'Pathology'],
        center: ['UCI'],
      };
      const expectedSelectedFilters = {
        'Available Data': ['Biomarkers', 'Pathology'],
        'Contributing Center': ['UCI'],
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
        model_type: ['Familial AD'],
        available_data: ['Gene Expression'],
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
