import { defineConfig, devices } from '@playwright/test';
import { nxE2EPreset } from '@nx/playwright/preset';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { workspaceRoot } from '@nx/devkit';

// For CI, you may want to set BASE_URL to the deployed application.
const baseURL = process.env['BASE_URL'] || 'http://localhost:4200';

/**
 * See https://playwright.dev/docs/test-configuration.
 */
export default defineConfig({
  ...nxE2EPreset(__filename, {
    testDir: './e2e',
    // includeMobileBrowsers: true, // includes mobile Chrome and Safari
    // includeBrandedBrowsers: true, // includes Google Chrome and Microsoft Edge
  }),
  projects: [
    /* Test against desktop browsers */
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
    /* Test against mobile viewports. */
    {
      name: 'Mobile Chrome',
      use: { ...devices['Pixel 5'] },
    },
    // {
    //   name: 'Mobile Safari',
    //   use: { ...devices['iPhone 12'] },
    // },
    /* Test against branded browsers. */
    // When Google Chrome and/or Microsoft Edge are installed in the dev container, Playwright will
    // use one of them to open the HTML report. These browsers are slow to open and respond because
    // they are emulated. It is recommended to now use them in combination with the HTML report. By
    // default, when Chrome and Edge are not installed, the HTML report opens in the native browser
    // of the developer, which is much faster.
    // {
    //   name: 'Google Chrome', use: { ...devices['Desktop Chrome'], channel: 'chrome' }, // or
    //   'chrome-beta'
    // },
    // {
    //   name: 'Microsoft Edge', use: { ...devices['Desktop Edge'], channel: 'msedge' }, // or
    //   'msedge-dev'
    // },
  ],
  /* Shared settings for all the projects below. See https://playwright.dev/docs/api/class-testoptions. */
  use: {
    baseURL,
    /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
    trace: 'on-first-retry',
  },
  /* Run your local dev server before starting the tests */
  webServer: {
    command: 'nx serve openchallenges-app',
    url: 'http://127.0.0.1:4200',
    reuseExistingServer: !process.env.CI,
    cwd: workspaceRoot,
  },
  reporter: [['line'], ['html', { open: 'never' }]],
  // reporter: process.env.CI ? 'html' : 'line',
});
