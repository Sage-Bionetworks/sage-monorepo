import { expect, test } from '@playwright/test';

test.describe('home', () => {
  test('should display welcome message', async ({ page }) => {
    await page.goto('/');

    const welcomeMessage = page.locator('h1');
    await expect(welcomeMessage).toHaveText('Welcome to the xQTL Explorer!');
  });
});
