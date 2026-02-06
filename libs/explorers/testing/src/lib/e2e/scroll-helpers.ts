import { expect, type Page } from '@playwright/test';

/**
 * Waits for the page scroll position to stabilize.
 * Useful for detecting and waiting out phantom scrolls or layout shifts.
 *
 * @param page - The Playwright page instance
 * @param timeout - Maximum time to wait for stability (default: 5000ms)
 */
export async function waitForScrollToStop(page: Page, timeout = 5000) {
  let previousOffset = await page.evaluate(() => window.pageYOffset);
  let stableCount = 0;
  const requiredStableChecks = 10;

  await expect
    .poll(
      async () => {
        const currentOffset = await page.evaluate(() => window.pageYOffset);

        if (currentOffset === previousOffset) {
          stableCount++;
          return stableCount >= requiredStableChecks;
        } else {
          stableCount = 0;
          previousOffset = currentOffset;
          return false;
        }
      },
      { timeout, intervals: [200] },
    )
    .toBe(true);
}
