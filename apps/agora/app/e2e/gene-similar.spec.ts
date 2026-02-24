import { expect, test } from '@playwright/test';
import { waitForSpinnerNotVisible } from './helpers/utils';

test.describe('specific viewport block', () => {
  test.slow();
  test.use({ viewport: { width: 1600, height: 1200 } });

  test('invalid gene results in a 404 redirect', async ({ page }) => {
    // go to invalid ENSG page
    await page.goto('/genes/ENSG00000000000/similar');
    await waitForSpinnerNotVisible(page);

    // expect a title "to contain" a substring.
    await expect(page).toHaveTitle('Agora | Page Not Found');

    // expect div for page not found content to be visible
    await expect(page.getByRole('heading', { level: 1 })).toHaveText(
      ` This page isn't available right now. `,
    );
  });

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

    const cell = page.getByRole('row').nth(1).getByRole('cell').nth(1);
    await expect(cell).not.toHaveText('');
  });
});
