import { expect, test } from '@playwright/test';
import {
  expectNoResultsFound,
  expectPinnedParams,
  expectPinnedRows,
  expectUnpinnedTableOnly,
  getPinnedTable,
  getRowByName,
  getUnpinnedTable,
  pinAll,
  pinByName,
  searchViaFilterbox,
  unPinByName,
} from '@sagebionetworks/explorers/testing/e2e';
import { baseURL } from '../playwright.config';
import { COMPARISON_TOOL_PATHS } from './constants';
import {
  fetchDiseaseCorrelations,
  fetchModelOverviews,
  navigateToComparison,
} from './helpers/comparison-tool';

const CT_PAGE = 'Model Overview';
const MODEL_OVERVIEW_PATH = COMPARISON_TOOL_PATHS[CT_PAGE];

test.describe('model overview', () => {
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
    await expectPinnedRows(page, [firstCorrelation.name]);
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

    await searchViaFilterbox(page, 'tg-');

    const unpinnedTable = getUnpinnedTable(page);
    const row = getRowByName(unpinnedTable, page, '3xTg-AD');
    await expect(row).toBeVisible();
    await expect(page.getByText('1-1 of 1')).toBeVisible();
  });

  test('filterbox search excludes pinned items from results', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true, 'url', `pinned=3xTg-AD,LOAD1`);

    await expectPinnedParams(page, ['3xTg-AD', 'LOAD1']);
    await expectPinnedRows(page, ['3xTg-AD', 'LOAD1']);

    await searchViaFilterbox(page, 'tg-');
    await expectNoResultsFound(page);

    await searchViaFilterbox(page, '3xtg-ad,load1');
    await expectNoResultsFound(page);
  });

  test('filterbox search with commas returns full, case-insensitive matches', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true);

    await searchViaFilterbox(page, '3xtg-ad,apoe4,fad');

    const unpinnedTable = getUnpinnedTable(page);
    await expect(getRowByName(unpinnedTable, page, '3xTg-AD')).toBeVisible();
    await expect(getRowByName(unpinnedTable, page, 'APOE4')).toBeVisible();
    await expect(getRowByName(unpinnedTable, page, '5xFAD (UCI)')).toBeHidden();

    await pinAll(page);

    await expectPinnedRows(page, ['3xTg-AD', 'APOE4']);
    await expectPinnedParams(page, ['3xTg-AD', 'APOE4']);
  });
});
