import { type Page } from '@playwright/test';
import type { AppVersion } from '@sagebionetworks/explorers/config';
import {
  APP_UPDATE_CONFIRM_DELAY_MS,
  APP_VERSION_PATH,
} from '@sagebionetworks/explorers/constants';

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
 * test must call `page.clock.install()` before loading the page.
 */
export async function waitOutVersionMismatchConfirmation(page: Page) {
  await page.clock.fastForward(APP_UPDATE_CONFIRM_DELAY_MS);
}
