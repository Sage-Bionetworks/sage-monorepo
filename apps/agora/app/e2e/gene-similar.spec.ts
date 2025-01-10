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
    await expect(page).toHaveTitle('Agora');

    // expect div for page not found content to be visible
    await expect(page.locator('.page-not-found')).toBeVisible();
  });
});
