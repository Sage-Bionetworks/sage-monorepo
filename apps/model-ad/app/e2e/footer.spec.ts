import { expect, test } from '@playwright/test';

test.describe('footer', () => {
  test('has site version', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByText('Site Version local')).toBeVisible();
  });

  test('has data version', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByText(/data version/i)).toContainText(/syn.*v/i);
  });
});
