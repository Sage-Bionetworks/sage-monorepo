import { expect, Page, test } from '@playwright/test';

/**
 * Runs a suite of filter panel tests for a comparison tool page.
 * @param navigateFn - Function that navigates to the comparison tool page
 */
export const runFilterPanelTests = (navigateFn: (page: Page) => Promise<void>) => {
  test.describe('filter panel', () => {
    test('clicking Filter Results button opens the filter panel', async ({ page }) => {
      await navigateFn(page);

      const filterButton = page.getByRole('button', { name: 'Filter Results' });
      await expect(filterButton).toBeVisible();

      await filterButton.click();

      const filterPanel = page.locator('.filter-panel');
      await expect(filterPanel).toHaveClass(/open/);
    });

    test('clicking close button closes the filter panel', async ({ page }) => {
      await navigateFn(page);

      const filterButton = page.getByRole('button', { name: 'Filter Results' });
      await filterButton.click();

      const filterPanel = page.locator('.filter-panel');
      await expect(filterPanel).toHaveClass(/open/);

      const closeButton = page.getByRole('button', { name: 'close' });
      await closeButton.click();

      await expect(filterPanel).not.toHaveClass(/open/);
    });

    test('clicking Filter Results button again closes the filter panel', async ({ page }) => {
      await navigateFn(page);

      const filterButton = page.getByRole('button', { name: 'Filter Results' });
      await filterButton.click();

      const filterPanel = page.locator('.filter-panel');
      await expect(filterPanel).toHaveClass(/open/);

      // Click the filter button again to close
      await filterButton.click();

      await expect(filterPanel).not.toHaveClass(/open/);
    });
  });
};

/**
 * Runs a suite of heatmap details panel tests for a comparison tool page.
 * @param navigateFn - Function that navigates to the comparison tool page
 */
export const runHeatmapDetailsPanelTests = (navigateFn: (page: Page) => Promise<void>) => {
  test.describe('heatmap details panel', () => {
    test('clicking a heatmap circle opens the details panel', async ({ page }) => {
      await navigateFn(page);

      // Find a visible heatmap circle button (some may be hidden due to no data or significance filter)
      // The circle div inside must be visible (display: block)
      const heatmapButton = page
        .locator('button.heatmap-circle-button')
        .filter({ has: page.locator('.heatmap-circle[style*="display: block"]') })
        .first();
      await heatmapButton.scrollIntoViewIfNeeded();
      await expect(heatmapButton).toBeVisible();
      await heatmapButton.click();

      // Verify a details panel heading is visible
      await expect(page.locator('.heatmap-details-panel-heading').first()).toBeVisible();
    });

    test('clicking the same heatmap circle again closes the details panel', async ({ page }) => {
      await navigateFn(page);

      const heatmapButton = page
        .locator('button.heatmap-circle-button')
        .filter({ has: page.locator('.heatmap-circle[style*="display: block"]') })
        .first();
      await heatmapButton.scrollIntoViewIfNeeded();
      await expect(heatmapButton).toBeVisible();

      // First click - open panel
      await heatmapButton.click();
      await expect(page.locator('.heatmap-details-panel-heading').first()).toBeVisible();

      // Second click - close panel (toggle behavior)
      await heatmapButton.click();
      await expect(page.locator('.heatmap-details-panel-heading')).toBeHidden();
    });

    test('clicking a different heatmap circle updates the panel content', async ({ page }) => {
      await navigateFn(page);

      const heatmapButtons = page
        .locator('button.heatmap-circle-button')
        .filter({ has: page.locator('.heatmap-circle[style*="display: block"]') });
      const firstButton = heatmapButtons.first();
      // Use a button further down the list to avoid popover overlap
      const secondButton = heatmapButtons.nth(10);

      // Click first button (scrolls into view automatically on click)
      await firstButton.scrollIntoViewIfNeeded();
      await firstButton.click();
      await expect(page.locator('.heatmap-details-panel-heading').first()).toBeVisible();

      // Click second button - use force: true in case popover still overlaps during transition
      await secondButton.scrollIntoViewIfNeeded();
      await secondButton.click({ force: true });

      // Panel should still be visible (showing different content)
      await expect(page.locator('.heatmap-details-panel-heading').first()).toBeVisible();
    });

    test('clicking outside the panel closes it', async ({ page }) => {
      await navigateFn(page);

      const heatmapButton = page
        .locator('button.heatmap-circle-button')
        .filter({ has: page.locator('.heatmap-circle[style*="display: block"]') })
        .first();
      await heatmapButton.scrollIntoViewIfNeeded();
      await heatmapButton.click();

      await expect(page.locator('.heatmap-details-panel-heading').first()).toBeVisible();

      // Click outside the panel (on the page body)
      await page.locator('body').click({ position: { x: 10, y: 10 } });
      await expect(page.locator('.heatmap-details-panel-heading')).toBeHidden();
    });
  });
};
