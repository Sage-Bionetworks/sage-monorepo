import { expect, test } from '@playwright/test';

test.describe('gene details', () => {
  test('url missing tissue results in a 404 redirect', async ({ page }) => {
    await page.goto('/genes/ENSMUSG00000033417?model=3xTg-AD');
    await expect(page.getByRole('heading', { level: 1 })).toHaveText(
      ` This page isn't available right now. `,
    );
  });

  test('url missing model and model group results in a 404 redirect', async ({ page }) => {
    await page.goto('/genes/ENSMUSG00000033417?tissue=Hippocampus');
    await expect(page.getByRole('heading', { level: 1 })).toHaveText(
      ` This page isn't available right now. `,
    );
  });
});
