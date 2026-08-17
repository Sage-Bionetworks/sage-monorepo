import { expect, test } from '@playwright/test';
import {
  clickViewDetailsButtonByName,
  ColumnConfig,
  expectPinnedParams,
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
  testMetaClickBuildsMultiColumnSort,
  testMetaClickTogglesExistingSortOrder,
  testMultiColumnSortRestoredFromUrl,
  testPartialCaseInsensitiveSearch,
  testSortRestoredFromUrl,
  unPinByName,
} from '@sagebionetworks/explorers/testing/e2e';
import { baseURL } from '../playwright.config';
import { COMPARISON_TOOL_PATHS } from './constants';
import { fetchMarmosetModelOverviews, navigateToComparison } from './helpers/comparison-tool';

const CT_PAGE = 'Marmoset Model Overview';
const MARMOSET_MODEL_OVERVIEW_PATH = COMPARISON_TOOL_PATHS[CT_PAGE];

const MODEL = 'Presenilin 1';
const MODEL_BIOMARKERS_PATH = `/models/${encodeURIComponent(MODEL)}/biomarkers?modelOrganism=marmoset`;

test.describe('marmoset model overview', () => {
  runFilterPanelTests(async (page) => navigateToComparison(page, CT_PAGE, true));

  test('share URL button copies URL to clipboard', async ({ page, context }) => {
    await context.grantPermissions(['clipboard-read']);

    await navigateToComparison(page, CT_PAGE, true);

    const shareUrlButton = page.getByRole('button', { name: 'Share URL' });
    await expect(shareUrlButton).toBeVisible();

    await page.waitForURL(MARMOSET_MODEL_OVERVIEW_PATH);

    await shareUrlButton.click();

    const clipboardContent = await page.evaluate(() => navigator.clipboard.readText());
    expect(clipboardContent).toEqual(`${baseURL}${MARMOSET_MODEL_OVERVIEW_PATH}`);
  });

  test('pinning and unpinning items updates the pinned query param', async ({ page }) => {
    const [firstModel] = await fetchMarmosetModelOverviews(page);
    expect(firstModel).toBeDefined();

    await navigateToComparison(page, CT_PAGE, true);

    const pinnedTable = getPinnedTable(page);
    const unpinnedTable = getUnpinnedTable(page);

    await pinByName(unpinnedTable, page, firstModel.name);
    await expect(getRowByName(pinnedTable, page, firstModel.name)).toHaveCount(1);
    await expectPinnedParams(page, [firstModel.name]);

    const pinnedRow = await unPinByName(pinnedTable, page, firstModel.name);
    await expect(pinnedRow).toHaveCount(0);
    await expectPinnedParams(page, []);
  });

  test('filterbox search without comma returns partial case-insensitive matches', async ({
    page,
  }) => {
    await navigateToComparison(page, CT_PAGE, true);
    await testPartialCaseInsensitiveSearch(page, 'presenilin', [MODEL]);
  });

  test.describe('sort URL sync', () => {
    // Columns: 0=Model (name), 1=Model Type, 2=Plasma Biomarkers, 3=Study Data
    // Default sort: name ASC

    const sortColumns: ColumnConfig[] = [
      { name: 'Model Type', field: 'model_type' },
      { name: 'Plasma Biomarkers', field: 'biomarkers' },
      { name: 'Study Data', field: 'study_data' },
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
      await navigateToComparison(page, CT_PAGE, true, 'url', 'sortFields=biomarkers&sortOrders=1');
      await testSortRestoredFromUrl(page, sortColumns[1].name, sortColumns[1].field);
    });

    test('clicking different columns in sequence replaces single-column sort', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testClickDifferentColumnsReplacesSingleSort(page, sortColumns);
    });

    test('Meta+click builds multi-column sort with multiple columns', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testMetaClickBuildsMultiColumnSort(page, sortColumns);
    });

    test('multi-column sort is restored from URL', async ({ page }) => {
      await navigateToComparison(
        page,
        CT_PAGE,
        true,
        'url',
        'sortFields=model_type,biomarkers&sortOrders=-1,1',
      );
      await testMultiColumnSortRestoredFromUrl(page, sortColumns.slice(0, 2), sortColumns[2]);
    });

    test('Meta+click on existing sort columns toggles their order', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testMetaClickTogglesExistingSortOrder(page, sortColumns);
    });
  });

  test.describe('filter URL sync', () => {
    // Every value matches the single marmoset model, so applying them never empties the table
    const filterParams = {
      modelTypes: ['Familial AD'],
      availableData: ['Plasma Biomarkers'],
      modifiedGenes: ['PSEN1'],
    };
    const selectedFilters = {
      'Model Type': ['Familial AD'],
      'Available Data': ['Plasma Biomarkers'],
      'Modified Gene': ['PSEN1'],
    };

    test('filter selections are added to URL when selected and removed when cleared', async ({
      page,
    }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testFilterSelectionUpdatesUrl(page, 'modelTypes', 'Model Type', 'Familial AD');
    });

    test('filter selections are restored from URL on page load', async ({ page }) => {
      await navigateToComparison(
        page,
        CT_PAGE,
        true,
        'url',
        getQueryParamsFromRecords(filterParams),
      );
      await testFilterSelectionRestoredFromUrl(page, filterParams, selectedFilters);
    });

    test('filters are removed from URL when Clear All is clicked', async ({ page }) => {
      await navigateToComparison(
        page,
        CT_PAGE,
        true,
        'url',
        getQueryParamsFromRecords(filterParams),
      );
      await testFiltersRemovedFromUrlOnClearAll(page, filterParams);
    });
  });

  test('plasma biomarkers link opens the marmoset model biomarkers tab in a new tab', async ({
    page,
  }) => {
    await navigateToComparison(page, CT_PAGE, true);

    const link = page.getByRole('link', { name: 'Results' }).first();
    await expect(link).toHaveAttribute('href', MODEL_BIOMARKERS_PATH);

    const popupPromise = page.waitForEvent('popup');
    await link.click();
    const popup = await popupPromise;

    await popup.waitForURL(MODEL_BIOMARKERS_PATH);
    await expect(popup.getByRole('heading', { level: 1, name: MODEL })).toBeVisible();
  });

  test('study data link points to the Synapse study page in a new tab', async ({ page }) => {
    const [firstModel] = await fetchMarmosetModelOverviews(page);
    const studyDataUrl = firstModel.study_data.link_url ?? '';
    expect(studyDataUrl).toContain('synapse.org');

    await navigateToComparison(page, CT_PAGE, true);

    const link = page.getByRole('link', { name: 'View Data' }).first();
    await expect(link).toHaveAttribute('href', studyDataUrl);
    await expect(link).toHaveAttribute('target', '_blank');
  });

  test('view details button opens the marmoset model details page in a new tab', async ({
    page,
  }) => {
    await navigateToComparison(page, CT_PAGE, true);

    const popupPromise = page.waitForEvent('popup');
    await clickViewDetailsButtonByName(getUnpinnedTable(page), page, MODEL);
    const popup = await popupPromise;

    await popup.waitForURL(`/models/${encodeURIComponent(MODEL)}?modelOrganism=marmoset`);
    await expect(popup.getByRole('heading', { level: 1, name: MODEL })).toBeVisible();
  });
});
