import { test, expect } from '@playwright/test';

test('paginator reset to page 1 on filter change', async ({ page }) => {
  // Go to the challenge search page
  await page.goto('/challenge');
  // Go to the second page
  await page.getByRole('button', { name: '2' }).click();
  // Make a change to the challenge query input by selecting 'active' status
  await page
    .locator('div')
    .filter({ hasText: /^Active$/ })
    .locator('div')
    .nth(2)
    .click();
  // Assert that the paginator page number has been reset to 1
  await expect(page.getByRole('button', { name: '1' })).toBeVisible();
});
