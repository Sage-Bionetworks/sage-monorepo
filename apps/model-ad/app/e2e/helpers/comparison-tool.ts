import { Page, expect, test } from '@playwright/test';
import { getUnpinnedTable } from '@sagebionetworks/explorers/testing/e2e';
import { COMPARISON_TOOL_PATHS } from '../constants';

export const closeVisualizationOverviewDialog = async (page: Page) => {
  await test.step('close visualization overview dialog', async () => {
    const dialog = page.getByRole('dialog');

    const closeBtn = dialog.getByRole('button').first();
    await closeBtn.click();

    await expect(dialog).toBeHidden();
  });
};

export const navigateToComparison = async (
  page: Page,
  name: string,
  queryParameters?: string,
  shouldCloseVisualizationOverviewDialog = true,
) => {
  const path = COMPARISON_TOOL_PATHS[name];
  const url = queryParameters ? `${path}?${queryParameters}` : path;
  await page.goto(url);

  if (shouldCloseVisualizationOverviewDialog) {
    await closeVisualizationOverviewDialog(page);
  }

  await expect(page.getByRole('heading', { level: 1, name })).toBeVisible();
  await expect(page.locator('explorers-base-table')).toHaveCount(2);
  await expect(getUnpinnedTable(page).locator('tbody tr').first()).toBeVisible();
};
