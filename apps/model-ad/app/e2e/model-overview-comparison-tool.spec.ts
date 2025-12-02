import { expect, test, type Page } from '@playwright/test';
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
import { ModelOverview } from '@sagebionetworks/model-ad/api-client';
import { baseURL } from '../playwright.config';
import { COMPARISON_TOOL_PATHS } from './constants';
import { navigateToComparison } from './helpers/comparison-tool';

const MODEL_OVERVIEW_PATH = COMPARISON_TOOL_PATHS['Model Overview'];
const MODEL_OVERVIEW_API_PATH = '/comparison-tools/model-overview';

const fetchModelOverviews = async (page: Page): Promise<ModelOverview[]> => {
  // Fetch all model overviews by using exclude filter with no items
  const response = await page.request.get(`${baseURL}/api/v1/${MODEL_OVERVIEW_API_PATH}`, {
    params: {
      itemFilterType: 'exclude',
    },
  });
  expect(response.ok()).toBeTruthy();
  const data = (await response.json()) as { modelOverviews: ModelOverview[] };
  return data.modelOverviews;
};

test.describe('model overview', () => {
  test('share URL button copies URL to clipboard', async ({ page, context }) => {
    await context.grantPermissions(['clipboard-read']);

    await navigateToComparison(page, 'Model Overview', true);

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

    await navigateToComparison(page, 'Model Overview', true);

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

    await navigateToComparison(page, 'Model Overview', true, 'url', `pinned=${firstModel._id}`);

    await expect(page.locator('explorers-base-table')).toHaveCount(2);
    await expect(getRowByName(getPinnedTable(page), page, firstModel.name)).toHaveCount(1);
    await expectPinnedParams(page, [firstModel._id]);
  });

  test('pinned items are removed from URL when navigating to another comparison tool', async ({
    page,
  }) => {
    const [firstModel] = await fetchModelOverviews(page);
    expect(firstModel).toBeDefined();

    await navigateToComparison(page, 'Model Overview', true, 'url', `pinned=${firstModel._id}`);
    await expectPinnedParams(page, [firstModel._id]);

    await navigateToComparison(page, 'Disease Correlation', true, 'link');
    await expectPinnedParams(page, []);
    await expectUnpinnedTableOnly(page);
  });

  test('pinned items are loaded from cache and displayed in UI and in URL when returning to the comparison tool', async ({
    page,
  }) => {
    const [firstModel] = await fetchModelOverviews(page);
    expect(firstModel).toBeDefined();

    await navigateToComparison(page, 'Model Overview', true, 'url', `pinned=${firstModel._id}`);
    await expectPinnedParams(page, [firstModel._id]);

    await navigateToComparison(page, 'Disease Correlation', true, 'link');
    await expectPinnedParams(page, []);
    await expectUnpinnedTableOnly(page);

    await navigateToComparison(page, 'Model Overview', true, 'link');
    await expectPinnedRows(page, [firstModel.name]);
    await expectPinnedParams(page, [firstModel._id]);
  });
});
