import { expect, test } from '@playwright/test';

test.describe('specific viewport block', () => {
  test.use({ viewport: { width: 1600, height: 1200 } });

  test('has title', async ({ page }) => {
    await page.goto('/');

    // Expect a title "to contain" a substring.
    await expect(page).toHaveTitle("Agora | Explore Alzheimer's Disease Genes");
  });

  test('has header', async ({ page }) => {
    await page.goto('/');

    // Expect header to be visible
    await expect(page.locator('div#header')).toBeVisible();
  });

  test('has heading', async ({ page }) => {
    await page.goto('/');

    // Expect header to be visible
    await expect(page.locator('h1')).toBeVisible();
  });

  test('has footer', async ({ page }) => {
    await page.goto('/');

    // Expect header to be visible
    await expect(page.locator('#footer')).toBeVisible();
  });

  test('has news', async ({ page }) => {
    await page.goto('/');

    // Hamburger menu should be hidden for the desktop viewport
    await expect(page.locator('button.header-nav-toggle')).toBeHidden();

    // look for news link and click it
    const newsLink = page.getByRole('link', { name: 'News' });

    // news link should be visible on the home page
    await expect(newsLink).toBeVisible();

    await newsLink.click();
    await expect(page).toHaveTitle('News | Agora Releases');
  });
});
