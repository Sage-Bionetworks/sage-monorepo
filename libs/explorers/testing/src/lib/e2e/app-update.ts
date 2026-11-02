import { expect, type Locator, type Page, type Response, test } from '@playwright/test';
import type { AppVersion } from '@sagebionetworks/explorers/config';
import {
  APP_UPDATE_CONFIRM_DELAY_MS,
  APP_UPDATE_MIN_CHECK_INTERVAL_MS,
  APP_VERSION_PATH,
  CHUNK_LOAD_ERROR_MESSAGE,
  DATA_UPDATE_CONFIRM_DELAY_MS,
  DATA_VERSION_PATH,
  getAppUpdateMessage,
  getDataUpdateMessage,
} from '@sagebionetworks/explorers/constants';

/** How long one fast-forward attempt waits for the update notice before trying again. */
const UPDATE_NOTICE_RENDER_TIMEOUT_MS = 1000;

/**
 * Must match the `TransferState` key `ConfigLoaderService` sends the app config to the browser
 * under.
 */
const TRANSFERRED_CONFIG_KEY = 'app-config';

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

/**
 * Makes the app server's `/app-version` report `appVersion` instead of the version it was deployed
 * with. Tests override it because the app server only knows its version from environment variables
 * the infra deploy sets, so a local app server reports an empty version, which a check cannot
 * compare against.
 */
export async function overrideAppVersion(page: Page, appVersion: AppVersion) {
  await page.route(`**${APP_VERSION_PATH}`, (route) => route.fulfill({ json: appVersion }));
}

/** Makes the app server report a newer deploy than the one the tab loaded with. */
export async function simulateNewerDeploy(page: Page) {
  await overrideAppVersion(page, NEWER_APP_VERSION);
}

/** Reads the app version the tab loaded with, from the app config the server sent the page. */
export async function readLoadedAppVersion(page: Page): Promise<AppVersion> {
  return page.evaluate((configKey): AppVersion => {
    // Angular writes transferred state into a script tag whose id is the app's `APP_ID` followed by
    // `-state`, such as `model-ad-app-state`
    const transferredState = document.querySelector('script[id$="-state"]')?.textContent;
    const { appVersion = '', commitSha = '' } =
      JSON.parse(transferredState || '{}')[configKey] ?? {};
    return { appVersion, commitSha };
  }, TRANSFERRED_CONFIG_KEY);
}

/**
 * Makes the app server report the app version the tab loaded with, as a deployed app server does
 * until the next deploy. Resolves with that version, so the test can verify the override with
 * `expectMatchingAppVersionCheck`. Must be called after `gotoAndWaitForBrowserApp`.
 */
export async function setupMatchingAppVersion(page: Page): Promise<AppVersion> {
  // Deployed environments: the infra deploy sets `APP_VERSION` and `COMMIT_SHA` on the app container. Both the
  // app config the tab loads and `/app-version` read them, so the two match until the next deploy.
  //
  // Local and CI: neither variable is set. The tab falls back to the config file's `appVersion`
  // (`local`), while `/app-version` reports empty values. A check treats an empty version as
  // unknown, and an unknown version never counts as a mismatch, so a check would match even if the
  // comparison were wrong.
  //
  // Overriding `/app-version` with the tab's own version makes a local check compare two known,
  // equal versions, as it would on a deployed app with no new deploy.
  const loadedAppVersion = await readLoadedAppVersion(page);
  await overrideAppVersion(page, loadedAppVersion);
  return loadedAppVersion;
}

/**
 * Verifies that a check compared two known, equal app versions: the tab loaded with a nonempty
 * version, and the server reported that same version back through `setupMatchingAppVersion`.
 */
export async function expectMatchingAppVersionCheck(
  appVersionCheck: Response,
  loadedAppVersion: AppVersion,
) {
  expect(loadedAppVersion.appVersion, 'the app version the tab loaded with').not.toBe('');
  expect(await appVersionCheck.json(), 'the app version the server reported').toEqual(
    loadedAppVersion,
  );
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

/**
 * Makes every lazy-loaded chunk the tab requests missing, as the app server answers a chunk from
 * an earlier deploy, until the tab starts loading a new page. The new page has the current build,
 * so its chunks load normally.
 */
export async function simulateChunksFromEarlierDeploy(page: Page) {
  let isLoadingNewPage = false;
  page.on('request', (request) => {
    if (request.isNavigationRequest() && request.frame() === page.mainFrame()) {
      isLoadingNewPage = true;
    }
  });
  await page.route('**/chunk-*.js', (route) =>
    isLoadingNewPage ? route.continue() : route.fulfill({ status: 404 }),
  );
}

/** Makes every lazy-loaded chunk the tab requests from now on fail to download. */
export async function simulateChunkDownloadFailure(page: Page) {
  await page.route('**/chunk-*.js', (route) => route.abort());
}

/**
 * Navigates within the running app, as a link click would, so the router loads the route's chunk
 * instead of the browser loading a new page. Every explorer app has an about route.
 */
export async function navigateWithinApp(page: Page, path = '/about') {
  await page.evaluate((targetPath) => {
    history.pushState(null, '', targetPath);
    dispatchEvent(new PopStateEvent('popstate'));
  }, path);
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
 * Runs a version check once the minimum interval between checks has passed, and resolves when both
 * versions' responses arrive, resolving with them. The tab sends a check's requests only after it
 * has handled the previous check's result, so this also proves that result was handled. The test
 * must call `page.clock.install()` before loading the page. The fast-forward can reach a background
 * poll, which then runs the check and holds back the tab switch's, so the responses are awaited
 * from before it.
 */
async function runNextVersionCheck(page: Page) {
  const appVersionResponse = waitForAppVersionCheck(page);
  const dataVersionResponse = waitForDataVersionCheck(page);
  await page.clock.fastForward(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
  await returnToTab(page);
  const [appVersionCheck, dataVersionCheck] = await Promise.all([
    appVersionResponse,
    dataVersionResponse,
  ]);
  expect(appVersionCheck.status()).toBe(200);
  expect(dataVersionCheck.status()).toBe(200);
  return { appVersionCheck, dataVersionCheck };
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
    test('blocks the page with a modal reload prompt once a newer deploy is confirmed', async ({
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
      const reloadButton = page.getByRole('button', { name: 'Reload Page' });
      await expect(reloadButton).toBeFocused();
      await page.keyboard.press('Escape');

      await expect(
        page.getByRole('alertdialog', { name: getAppUpdateMessage(siteName) }),
      ).toBeVisible();
      await expect(reloadButton).toBeFocused();
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

    test('asks to reload when a page fails to download on the current deploy', async ({ page }) => {
      await gotoAndWaitForBrowserApp(page);
      // Not verified with `expectMatchingAppVersionCheck` here: a wrong comparison loads a new page,
      // which discards the response body before it can be read. The overlay assertions below catch
      // that instead, and the "stays usable" test verifies the same setup.
      await setupMatchingAppVersion(page);
      await simulateChunkDownloadFailure(page);
      const appVersionCheck = waitForAppVersionCheck(page);

      await navigateWithinApp(page);

      expect((await appVersionCheck).status()).toBe(200);
      await expect(
        page.getByText(CHUNK_LOAD_ERROR_MESSAGE),
        'expected the network error overlay when a chunk fails to download and the server reports the same app version the tab loaded with',
      ).toBeVisible();
      await expect(
        page.getByRole('button', { name: 'Reload Page' }),
        'expected a Reload Page button on the network error overlay when a chunk fails to download and the server reports the same app version the tab loaded with',
      ).toBeVisible();
    });

    test('stays usable past the confirmation delay when neither the deployed version nor the data release has changed', async ({
      page,
    }) => {
      await page.clock.install();
      await gotoAndWaitForBrowserApp(page);
      const loadedAppVersion = await setupMatchingAppVersion(page);
      const { appVersionCheck } = await runNextVersionCheck(page);
      await expectMatchingAppVersionCheck(appVersionCheck, loadedAppVersion);
      // The second check proves the first one's result was handled, so a mismatch the first one
      // found is now waiting out its confirmation delay
      await runNextVersionCheck(page);

      await page.clock.fastForward(APP_UPDATE_CONFIRM_DELAY_MS);

      // A mismatch confirmed after the delay stops every check, so at most one more would arrive
      await runNextVersionCheck(page);
      await runNextVersionCheck(page);
      await expect(page.getByText(getAppUpdateMessage(siteName))).toBeHidden();
      await expect(page.getByText(getDataUpdateMessage(siteName))).toBeHidden();
    });

    test('loads the page the user clicked when its chunk belongs to an earlier deploy', async ({
      page,
    }) => {
      await gotoAndWaitForBrowserApp(page);
      await simulateNewerDeploy(page);
      await simulateChunksFromEarlierDeploy(page);

      // A link click, unlike `navigateWithinApp`, leaves the URL unchanged until the route loads
      await page.getByRole('link', { name: 'About', exact: true }).first().click();

      await expect(page).toHaveURL(/\/about$/);
    });
  });
}
