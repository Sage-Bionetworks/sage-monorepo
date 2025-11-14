import { Page, expect, test } from '@playwright/test';

export const closeVisualizationOverviewDialog = async (page: Page) => {
  await test.step('close visualization overview dialog', async () => {
    const dialog = page.getByRole('dialog');

    const closeBtn = dialog.getByRole('button').first();
    await closeBtn.click();

    await expect(dialog).toBeHidden();
  });
};
