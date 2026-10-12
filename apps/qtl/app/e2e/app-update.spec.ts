import { expect, test } from '@playwright/test';
import { getAppUpdateMessage } from '@sagebionetworks/explorers/constants';
import {
  gotoAndWaitForBrowserApp,
  returnToTab,
  simulateNewerDeploy,
  waitForVersionCheck,
} from '@sagebionetworks/explorers/testing/e2e';

const SITE_NAME = 'QTL';

test.describe('app update', () => {
  test('blocks the page with a reload prompt when a newer version was deployed', async ({
    page,
  }) => {
    await gotoAndWaitForBrowserApp(page);
    await simulateNewerDeploy(page);

    await returnToTab(page);

    await expect(page.getByText(getAppUpdateMessage(SITE_NAME))).toBeVisible();
    await expect(page.getByRole('button', { name: 'Reload Page' })).toBeVisible();
  });

  test('stays usable when the deployed version has not changed', async ({ page }) => {
    await gotoAndWaitForBrowserApp(page);
    const versionCheck = waitForVersionCheck(page);

    await returnToTab(page);

    expect((await versionCheck).status()).toBe(200);
    await expect(page.getByText(getAppUpdateMessage(SITE_NAME))).toBeHidden();
  });
});
