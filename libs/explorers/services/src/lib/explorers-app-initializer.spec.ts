import { TestBed } from '@angular/core/testing';
import { ConfigService } from '@sagebionetworks/explorers/config';
import { DATA_VERSION_PATH } from '@sagebionetworks/explorers/constants';
import { BrowserSentryConfig, initSentry } from '@sagebionetworks/explorers/sentry';
import { AppUpdateCheckTriggerEnum, AppUpdateService } from './app-update.service';
import { initializeExplorersApp } from './explorers-app-initializer';

jest.mock('@sagebionetworks/explorers/sentry', () => ({
  ...jest.requireActual('@sagebionetworks/explorers/sentry'),
  initSentry: jest.fn(),
}));

const siteName = 'Model AD';
const csrApiUrl = 'https://modeladexplorer.org/api/v1';
const config = {
  appVersion: '4.0.0-rc3',
  commitSha: 'abc1234',
  csrApiUrl,
  sentryDsn: 'https://test-dsn@sentry.io/123',
  sentryEnvironment: 'stage',
  sentryRelease: 'model-ad@4.0.0-rc3+abc1234',
  sentryTracesSampleRate: null as number | null,
};

describe('initializeExplorersApp', () => {
  let configService: { config: typeof config; loadConfig: jest.Mock };
  const dataVersionUrl = `${csrApiUrl}${DATA_VERSION_PATH}`;
  let appUpdateService: { start: jest.Mock; requestCheck: jest.Mock; isAppStale: jest.Mock };

  async function initialize(loadedConfig: typeof config = config) {
    configService = {
      config: loadedConfig,
      loadConfig: jest.fn(() => Promise.resolve(loadedConfig)),
    };
    appUpdateService = {
      start: jest.fn(),
      requestCheck: jest.fn(),
      isAppStale: jest.fn(() => Promise.resolve(true)),
    };
    TestBed.configureTestingModule({
      providers: [
        { provide: ConfigService, useValue: configService },
        { provide: AppUpdateService, useValue: appUpdateService },
      ],
    });
    await TestBed.runInInjectionContext(() => initializeExplorersApp(siteName));
  }

  function sentryConfig(): BrowserSentryConfig {
    return (initSentry as jest.Mock).mock.calls[0][0];
  }

  afterEach(() => {
    TestBed.resetTestingModule();
    jest.clearAllMocks();
  });

  it('reports errors to Sentry with the loaded config', async () => {
    await initialize();

    expect(configService.loadConfig).toHaveBeenCalledTimes(1);
    expect(sentryConfig()).toEqual(
      expect.objectContaining({
        dsn: config.sentryDsn,
        environment: config.sentryEnvironment,
        release: config.sentryRelease,
      }),
    );
  });

  it('passes the configured performance tracing sample rate to Sentry', async () => {
    const fullSampleRate = 1;

    await initialize({ ...config, sentryTracesSampleRate: fullSampleRate });

    expect(sentryConfig().tracesSampleRate).toBe(fullSampleRate);
  });

  it('starts update detection with the loaded version, the site name, and the data version URL', async () => {
    await initialize();

    expect(appUpdateService.start).toHaveBeenCalledWith(config, siteName, dataVersionUrl);
  });

  it('builds the data version URL when the API URL ends with a slash', async () => {
    await initialize({ ...config, csrApiUrl: `${csrApiUrl}/` });

    expect(appUpdateService.start).toHaveBeenCalledWith(
      expect.anything(),
      siteName,
      dataVersionUrl,
    );
  });

  it('lets Sentry ask whether the tab is stale with a chunk-load check', async () => {
    await initialize();

    await expect(sentryConfig().isStaleClient?.()).resolves.toBe(true);
    expect(appUpdateService.isAppStale).toHaveBeenCalledWith(
      AppUpdateCheckTriggerEnum.ChunkLoadError,
    );
  });
});
