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
  /**
   * Returns a locator for visible heatmap circle buttons.
   * Some buttons may be hidden due to no data or significance filter,
   * so we filter to only those with a visible circle (display: block).
   */
  const getVisibleHeatmapCircleButtons = (page: Page) =>
    page.locator('button.heatmap-circle-button').filter({ visible: true });

  test.describe('heatmap details panel', () => {
    test('clicking a heatmap circle opens the details panel', async ({ page }) => {
      await navigateFn(page);

      const heatmapButton = getVisibleHeatmapCircleButtons(page).first();
      await expect(heatmapButton).toBeVisible();
      await heatmapButton.click();

      // Verify a details panel heading is visible
      await expect(page.locator('.heatmap-details-panel-heading').first()).toBeVisible();
    });

    test('clicking a different heatmap circle updates the panel content', async ({ page }) => {
      await navigateFn(page);

      const heatmapButtons = getVisibleHeatmapCircleButtons(page);
      const firstButton = heatmapButtons.first();
      // Use a button further down the list to avoid popover overlap
      const secondButton = heatmapButtons.nth(10);

      await firstButton.click();

      const panelData = page.locator('.heatmap-details-panel-data').first();
      await expect(panelData).toBeVisible();

      // Capture the initial panel data (values that change between circles)
      const initialData = await panelData.textContent();

      // Click second button - use force: true in case popover still overlaps during transition
      await secondButton.click({ force: true });

      // Panel should still be visible with different data values
      await expect(panelData).toBeVisible();
      await expect(panelData).not.toHaveText(initialData ?? '');
    });

    test('clicking a different heatmap circle repositions the panel near the new target', async ({
      page,
    }) => {
      await navigateFn(page);

      const heatmapButtons = getVisibleHeatmapCircleButtons(page);
      const firstButton = heatmapButtons.first();
      // Use a button further down the list to ensure different vertical position
      const secondButton = heatmapButtons.nth(10);

      // Click first button and get panel position
      await firstButton.click();
      const panelHeading = page.locator('.heatmap-details-panel-heading').first();
      await expect(panelHeading).toBeVisible();
      const firstPanelBox = await panelHeading.boundingBox();

      // Get first button position for reference
      const firstButtonBox = await firstButton.boundingBox();

      // Click second button
      await secondButton.click({ force: true });
      await expect(panelHeading).toBeVisible();

      // Get second button position
      const secondButtonBox = await secondButton.boundingBox();

      // Wait for panel to potentially reposition
      await page.waitForTimeout(100);
      const secondPanelBox = await panelHeading.boundingBox();

      // Verify positions are available
      expect(firstButtonBox).not.toBeNull();
      expect(secondButtonBox).not.toBeNull();
      expect(firstPanelBox).not.toBeNull();
      expect(secondPanelBox).not.toBeNull();

      // If buttons are at different vertical positions, panel should reposition accordingly
      // The panel should be closer to its target button than to the other button
      if (firstButtonBox && secondButtonBox && firstPanelBox && secondPanelBox) {
        if (Math.abs(firstButtonBox.y - secondButtonBox.y) > 50) {
          const panelMovedSignificantly = Math.abs(firstPanelBox.y - secondPanelBox.y) > 20;
          expect(panelMovedSignificantly).toBe(true);
        }
      }
    });

    test('clicking outside the panel closes it', async ({ page }) => {
      await navigateFn(page);

      const heatmapButton = getVisibleHeatmapCircleButtons(page).first();
      await heatmapButton.click();

      await expect(page.locator('.heatmap-details-panel-heading').first()).toBeVisible();

      // Click outside the panel (on the page body)
      await page.locator('body').click({ position: { x: 10, y: 10 } });
      await expect(page.locator('.heatmap-details-panel-heading')).toHaveCount(0);
    });

    test('panel displays data values and p-value', async ({ page }) => {
      await navigateFn(page);

      const heatmapButton = getVisibleHeatmapCircleButtons(page).first();
      await heatmapButton.click();

      // Verify the panel contains the expected data sections
      const panelData = page.locator('.heatmap-details-panel-data').first();
      await expect(panelData).toBeVisible();
      await expect(panelData).toContainText('P-value');
    });

    test('clicking the same heatmap circle again closes the details panel', async ({ page }) => {
      await navigateFn(page);

      const heatmapButton = getVisibleHeatmapCircleButtons(page).first();
      await expect(heatmapButton).toBeVisible();

      // First click - open panel
      await heatmapButton.click();
      const panelHeading = page.locator('.heatmap-details-panel-heading').first();
      await expect(panelHeading).toBeVisible();

      // Second click on the same button - close panel (toggle behavior)
      await heatmapButton.click();
      await expect(panelHeading).toBeHidden();
    });
  });
};
