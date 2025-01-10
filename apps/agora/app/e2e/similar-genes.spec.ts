import { expect, test } from '@playwright/test';

test.describe('specific viewport block', () => {
  test.use({ viewport: { width: 1600, height: 1200 } });

  test('has title', async ({ page }) => {
    await page.goto('/genes/ENSG00000178209/similar');

    // Expect a title "to contain" a substring.
    await expect(page).toHaveTitle('Agora');
  });

  test('has true and false values for nominated target column', async ({ page }) => {
    await page.goto('/genes/ENSG00000178209/similar');

    await expect(page.locator('table')).toBeVisible();

    // sort forward on nominated target
    await page.getByRole('cell', { name: 'Nominated Target' }).click();

    const cell = await page.getByRole('row').nth(1).getByRole('cell').nth(1).innerText();
    expect(cell).not.toBe('');
  });
});
