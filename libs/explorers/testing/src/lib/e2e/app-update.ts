import { expect, type Locator, type Page, test } from '@playwright/test';
import type { AppVersion } from '@sagebionetworks/explorers/config';
import {
  APP_UPDATE_CONFIRM_DELAY_MS,
  APP_VERSION_PATH,
  DATA_UPDATE_CONFIRM_DELAY_MS,
  DATA_VERSION_PATH,
  getAppUpdateMessage,
  getDataUpdateMessage,
} from '@sagebionetworks/explorers/constants';

/** How long one fast-forward attempt waits for the update notice before trying again. */
const UPDATE_NOTICE_RENDER_TIMEOUT_MS = 1000;

export const NEWER_APP_VERSION: AppVersion = {
  appVersion: '999.0.0',
  commitSha: 'e2e0000',
};

export const NEWER_DATA_VERSION = {
  data_file: 'syn00000000',
  data_version: '9999',
};

/**
 * Waits until the browser app has started, which is after the app initializer starts the app
 * update service. The footer fetches the data version only in the browser, so its response marks
 * that. It is also the data version the tab records as the one it loaded with.
 */
export async function gotoAndWaitForBrowserApp(page: Page, path = '/') {
  const dataVersionResponse = waitForDataVersionCheck(page);
  await page.goto(path);
  await dataVersionResponse;
}

/** Makes the app server report a newer deploy than the one the tab loaded with. */
export async function simulateNewerDeploy(page: Page) {
  await page.route(`**${APP_VERSION_PATH}`, (route) => route.fulfill({ json: NEWER_APP_VERSION }));
}

/**
 * Makes the API report a newer data release than the one the tab loaded with. Must be called after
 * `gotoAndWaitForBrowserApp`, or the footer would load the newer release as the tab's own.
 */
export async function simulateNewDataRelease(page: Page) {
  await page.route(`**${DATA_VERSION_PATH}`, (route) =>
    route.fulfill({ json: NEWER_DATA_VERSION }),
  );
}

/** Resolves with the next app version check's response. */
export function waitForAppVersionCheck(page: Page) {
  return page.waitForResponse(`**${APP_VERSION_PATH}`);
}

/** Resolves with the next data version response, from the footer or from a version check. */
export function waitForDataVersionCheck(page: Page) {
  return page.waitForResponse(`**${DATA_VERSION_PATH}`);
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
 * Fast-forwards past a confirmation delay until the update notice appears. The test must call
 * `page.clock.install()` before loading the page. The confirmation is scheduled when the check
 * that found the mismatch responds, which the browser handles just after Playwright sees the
 * response, so a single fast-forward can land too early; it is retried until the notice shows.
 */
export async function waitOutMismatchConfirmation(
  page: Page,
  confirmDelayMs: number,
  updateNotice: Locator,
) {
  await expect(async () => {
    await page.clock.fastForward(confirmDelayMs);
    await expect(updateNotice).toBeVisible({ timeout: UPDATE_NOTICE_RENDER_TIMEOUT_MS });
  }).toPass();
}

/**
 * Defines the app update tests every explorer app runs. `siteName` must match the app's
 * `SITE_NAME`, since the update notices name the site.
 */
export function defineAppUpdateTests(siteName: string) {
  test.describe('app update', () => {
    test('blocks the page with a reload prompt once a newer deploy is confirmed', async ({
      page,
    }) => {
      await page.clock.install();
      await gotoAndWaitForBrowserApp(page);
      await simulateNewerDeploy(page);
      const firstAppVersionCheck = waitForAppVersionCheck(page);

      await returnToTab(page);
      await firstAppVersionCheck;
      await waitOutMismatchConfirmation(
        page,
        APP_UPDATE_CONFIRM_DELAY_MS,
        page.getByText(getAppUpdateMessage(siteName)),
      );

      await expect(page.getByRole('button', { name: 'Reload Page' })).toBeVisible();
    });

    test('blocks the page with a reload prompt once a new data release is confirmed', async ({
      page,
    }) => {
      await page.clock.install();
      await gotoAndWaitForBrowserApp(page);
      await simulateNewDataRelease(page);
      const firstDataVersionCheck = waitForDataVersionCheck(page);

      await returnToTab(page);
      await firstDataVersionCheck;
      await waitOutMismatchConfirmation(
        page,
        DATA_UPDATE_CONFIRM_DELAY_MS,
        page.getByText(getDataUpdateMessage(siteName)),
      );

      await expect(page.getByRole('button', { name: 'Reload Page' })).toBeVisible();
    });

    test('stays usable when neither the deployed version nor the data release has changed', async ({
      page,
    }) => {
      await gotoAndWaitForBrowserApp(page);
      const appVersionCheck = waitForAppVersionCheck(page);
      const dataVersionCheck = waitForDataVersionCheck(page);

      await returnToTab(page);

      expect((await appVersionCheck).status()).toBe(200);
      expect((await dataVersionCheck).status()).toBe(200);
      await expect(page.getByText(getAppUpdateMessage(siteName))).toBeHidden();
      await expect(page.getByText(getDataUpdateMessage(siteName))).toBeHidden();
    });
  });
}
