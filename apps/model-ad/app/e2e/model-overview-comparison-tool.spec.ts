import { expect, test } from '@playwright/test';
import {
  expectPinnedParams,
  expectPinnedRows,
  expectUnpinnedTableOnly,
  getPinnedTable,
  getRowByName,
  getUnpinnedTable,
  pinByName,
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
    await expectPinnedParams(page, [firstModel._id]);

    await pinByName(unpinnedTable, page, secondModel.name);
    await expect(getRowByName(pinnedTable, page, secondModel.name)).toHaveCount(1);
    await expectPinnedParams(page, [firstModel._id, secondModel._id]);

    const firstPinnedRow = await unPinByName(pinnedTable, page, firstModel.name);
    await expect(firstPinnedRow).toHaveCount(0);
    await expectPinnedParams(page, [secondModel._id]);

    const secondPinnedRow = await unPinByName(pinnedTable, page, secondModel.name);
    await expect(secondPinnedRow).toHaveCount(0);
    await expectPinnedParams(page, []);
  });

  test('pinned items in the URL are restored in the UI', async ({ page }) => {
    const [firstModel] = await fetchModelOverviews(page);
    expect(firstModel).toBeDefined();

    await navigateToComparison(page, CT_PAGE, true, 'url', `pinned=${firstModel._id}`);

    await expect(page.locator('explorers-base-table')).toHaveCount(2);
    await expect(getRowByName(getPinnedTable(page), page, firstModel.name)).toHaveCount(1);
    await expectPinnedParams(page, [firstModel._id]);
  });

  test('pinned items are removed from URL when navigating to another comparison tool', async ({
    page,
  }) => {
    const [firstModel] = await fetchModelOverviews(page);
    expect(firstModel).toBeDefined();

    await navigateToComparison(page, CT_PAGE, true, 'url', `pinned=${firstModel._id}`);
    await expectPinnedParams(page, [firstModel._id]);

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

    await navigateToComparison(page, initialCT, true, 'url', `pinned=${firstCorrelation._id}`);
    await expectPinnedParams(page, [firstCorrelation._id]);

    await navigateToComparison(page, CT_PAGE, true, 'link');
    await expectPinnedParams(page, []);
    await expectUnpinnedTableOnly(page);

    await pinByName(getUnpinnedTable(page), page, firstModel.name);
    await expectPinnedRows(page, [firstModel.name]);
    await expectPinnedParams(page, [firstModel._id]);

    await navigateToComparison(page, initialCT, true, 'link');
    await expectPinnedParams(page, [firstCorrelation._id]);
    await expectPinnedRows(page, [firstCorrelation.name]);
  });

  test('pinned items are loaded from cache and displayed in UI and in URL when returning to the comparison tool', async ({
    page,
  }) => {
    const [firstModel] = await fetchModelOverviews(page);
    expect(firstModel).toBeDefined();

    await navigateToComparison(page, CT_PAGE, true, 'url', `pinned=${firstModel._id}`);
    await expectPinnedParams(page, [firstModel._id]);

    await navigateToComparison(page, 'Disease Correlation', true, 'link');
    await expectPinnedParams(page, []);
    await expectUnpinnedTableOnly(page);

    await navigateToComparison(page, CT_PAGE, true, 'link');
    await expectPinnedRows(page, [firstModel.name]);
    await expectPinnedParams(page, [firstModel._id]);
  });
});
