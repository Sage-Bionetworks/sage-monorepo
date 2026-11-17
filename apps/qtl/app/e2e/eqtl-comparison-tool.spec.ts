import { expect, test } from '@playwright/test';
import { EQTL_CT_PATH, navigateToEqtlComparison } from './helpers/comparison-tool';

test.describe('eQTL comparison tool', () => {
  test('has the eQTL Explorer title', async ({ page }) => {
    await navigateToEqtlComparison(page, true);
    await expect(page).toHaveTitle('eQTL Explorer');
  });

  test('is reachable from the header navigation', async ({ page }) => {
    await page.goto('/');
    await navigateToEqtlComparison(page, true, 'link');
    await expect(page).toHaveURL(EQTL_CT_PATH);
  });

  test('loads the results sorted by adjusted p-value, ascending', async ({ page }) => {
    await navigateToEqtlComparison(page, true);
    await expect(page.locator('th', { hasText: 'Adjusted P-Value' })).toHaveAttribute(
      'aria-sort',
      'ascending',
    );
  });
});
