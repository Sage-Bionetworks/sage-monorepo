import { expect, test } from '@playwright/test';
import { baseURL } from '../playwright.config';

test.describe('specific viewport block', () => {
  test.use({ viewport: { width: 1600, height: 1200 } });

  test('has title', async ({ page }) => {
    await page.goto('/comparison/nominated-targets');

    // Expect a title "to contain" a substring.
    await expect(page).toHaveTitle(
      "Nominated Targets | Candidate genes for Alzheimer's Disease treatment or prevention",
    );
  });
});

test.describe('legacy url redirects', () => {
  test('redirects to nominated targets', async ({ page }) => {
    await page.goto('/genes/(genes-router:genes-list)');
    await expect(page).toHaveURL(`${baseURL}/comparison/nominated-targets`);
  });
});
