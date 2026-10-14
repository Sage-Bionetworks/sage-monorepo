import { expect, type Page, test } from '@playwright/test';
import type { AppVersion } from '@sagebionetworks/explorers/config';
import {
  APP_UPDATE_CONFIRM_DELAY_MS,
  APP_VERSION_PATH,
  getAppUpdateMessage,
} from '@sagebionetworks/explorers/constants';

/** How long one fast-forward attempt waits for the update notice before trying again. */
const UPDATE_NOTICE_RENDER_TIMEOUT_MS = 1000;

/** A version no real deploy reports, so it differs from whatever version the tab loaded with. */
export const NEWER_DEPLOY_VERSION: AppVersion = {
  appVersion: '999.0.0',
  commitSha: 'e2e0000',
};

/**
 * Waits until the browser app has started, which is after the app initializer starts the app
 * update service. The data version is fetched only in the browser, so its response marks that.
 */
export async function gotoAndWaitForBrowserApp(page: Page, path = '/') {
  const dataVersionResponse = page.waitForResponse('**/data-version');
  await page.goto(path);
  await dataVersionResponse;
}

/** Makes the app server report a newer deploy than the one the tab loaded with. */
export async function simulateNewerDeploy(page: Page) {
  await page.route(`**${APP_VERSION_PATH}`, (route) =>
    route.fulfill({ json: NEWER_DEPLOY_VERSION }),
  );
}

/** Resolves with the next version check's response. */
export function waitForVersionCheck(page: Page) {
  return page.waitForResponse(`**${APP_VERSION_PATH}`);
}

/** Simulates the user switching away from the tab and back, which triggers a version check. */
export async function returnToTab(page: Page) {
  await page.evaluate(() => {
    for (const visibilityState of ['hidden', 'visible']) {
      Object.defineProperty(document, 'visibilityState', {
        configurable: true,
        get: () => visibilityState,
      });
      document.dispatchEvent(new Event('visibilitychange'));
    }
  });
}

/**
 * Lets the confirmation delay pass, so the check it schedules confirms a version mismatch. The
 * test must call `page.clock.install()` before loading the page. The confirmation is scheduled
 * when the check that found the mismatch responds, so a fast-forward before then lets nothing
 * through.
 */
export async function waitOutVersionMismatchConfirmation(page: Page) {
  await page.clock.fastForward(APP_UPDATE_CONFIRM_DELAY_MS);
}

/**
 * Defines the app update tests every explorer app runs. `siteName` must match the app's
 * `SITE_NAME`, since the update notice names the site.
 */
export function defineAppUpdateTests(siteName: string) {
  test.describe('app update', () => {
    test('blocks the page with a reload prompt once a newer deploy is confirmed', async ({
      page,
    }) => {
      await page.clock.install();
      await gotoAndWaitForBrowserApp(page);
      await simulateNewerDeploy(page);
      const firstVersionCheckResponse = waitForVersionCheck(page);

      await returnToTab(page);
      await firstVersionCheckResponse;
      // The browser schedules the confirmation just after Playwright sees the response, so the
      // fast-forward is retried until the confirmation it lets through shows the notice.
      const updateNotice = page.getByText(getAppUpdateMessage(siteName));
      await expect(async () => {
        await waitOutVersionMismatchConfirmation(page);
        await expect(updateNotice).toBeVisible({ timeout: UPDATE_NOTICE_RENDER_TIMEOUT_MS });
      }).toPass();

      await expect(page.getByRole('button', { name: 'Reload Page' })).toBeVisible();
    });

    test('stays usable when the deployed version has not changed', async ({ page }) => {
      await gotoAndWaitForBrowserApp(page);
      const versionCheckResponse = waitForVersionCheck(page);

      await returnToTab(page);

      expect((await versionCheckResponse).status()).toBe(200);
      await expect(page.getByText(getAppUpdateMessage(siteName))).toBeHidden();
    });
  });
}
