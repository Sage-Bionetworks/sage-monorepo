import { expect, test } from '@playwright/test';
import { baseURL } from '../playwright.config';
import { closeVisualizationOverviewDialog } from './helpers/comparison-tool';

test.describe('model overview', () => {
  test('share URL button copies URL to clipboard', async ({ page, context }) => {
    const path = '/comparison/model';
    await context.grantPermissions(['clipboard-read']);

    await page.goto(path);
    await expect(page.getByRole('heading', { level: 1, name: 'Model Overview' })).toBeVisible();

    const shareUrlButton = page.getByRole('button', { name: 'Share URL' });
    await expect(shareUrlButton).toBeVisible();

    await page.waitForURL(path);

    // close the visualization overview dialog
    await closeVisualizationOverviewDialog(page);

    await shareUrlButton.click();

    const clipboardContent = await page.evaluate(() => navigator.clipboard.readText());
    expect(clipboardContent).toEqual(`${baseURL}${path}`);
  });
});
