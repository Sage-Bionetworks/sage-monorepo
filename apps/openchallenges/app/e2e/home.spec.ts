import { test, expect } from '@playwright/test';

test('has banner', async ({ page }) => {
  await page.goto('/');

  // Expect first h4 to contain a substring.
  await expect(page.locator('h4').first()).toHaveText('Welcome to OpenChallenges!');
});
