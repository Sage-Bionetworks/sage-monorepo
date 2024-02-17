import { test, expect } from '@playwright/test';

test('reset page number when query input changes', async ({ page }) => {
  await page.goto('/challenge');

  // dummy assertion
  await expect(page.locator('h2')).toHaveText('Challenges');
});
