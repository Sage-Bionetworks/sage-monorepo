import { expect, test } from '@playwright/test';

test.describe('protein details', () => {
  test('url missing tissue results in a 404 redirect', async ({ page }) => {
    await page.goto('/proteins/ENSMUSG00000057738B9EKJ1?model=LOAD2');
    await expect(page.getByRole('heading', { level: 1 })).toHaveText(
      ` This page isn't available right now. `,
    );
  });

  test('url missing model and model group results in a 404 redirect', async ({ page }) => {
    await page.goto('/proteins/ENSMUSG00000057738B9EKJ1?tissue=Hemibrain');
    await expect(page.getByRole('heading', { level: 1 })).toHaveText(
      ` This page isn't available right now. `,
    );
  });
});
