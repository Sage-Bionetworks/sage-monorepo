import { TestBed } from '@angular/core/testing';
import { ConfigService } from '@sagebionetworks/explorers/config';
import { initSentry, SentryConfig } from '@sagebionetworks/explorers/sentry';
import { AppUpdateCheckTriggerEnum, AppUpdateService } from './app-update.service';
import { initializeExplorersApp } from './explorers-app-initializer';

jest.mock('@sagebionetworks/explorers/sentry', () => ({
  ...jest.requireActual('@sagebionetworks/explorers/sentry'),
  initSentry: jest.fn(),
}));

const siteName = 'Model AD';
const config = {
  appVersion: '4.0.0-rc3',
  commitSha: 'abc1234',
  sentryDsn: 'https://test-dsn@sentry.io/123',
  sentryEnvironment: 'stage',
  sentryRelease: 'model-ad@4.0.0-rc3+abc1234',
};

describe('initializeExplorersApp', () => {
  let configService: { config: typeof config; loadConfig: jest.Mock };
  let appUpdateService: { start: jest.Mock; requestCheck: jest.Mock; checkNow: jest.Mock };

  async function initialize() {
    configService = { config, loadConfig: jest.fn(() => Promise.resolve(config)) };
    appUpdateService = {
      start: jest.fn(),
      requestCheck: jest.fn(),
      checkNow: jest.fn(() => Promise.resolve(true)),
    };
    TestBed.configureTestingModule({
      providers: [
        { provide: ConfigService, useValue: configService },
        { provide: AppUpdateService, useValue: appUpdateService },
      ],
    });
    await TestBed.runInInjectionContext(() => initializeExplorersApp(siteName));
  }

  function sentryConfig(): SentryConfig {
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

  it('starts app update detection with the loaded version and the site name', async () => {
    await initialize();

    expect(appUpdateService.start).toHaveBeenCalledWith(config, siteName);
  });

  it('lets Sentry ask whether the tab is stale with a chunk-load check', async () => {
    await initialize();

    await expect(sentryConfig().isStaleClient?.()).resolves.toBe(true);
    expect(appUpdateService.checkNow).toHaveBeenCalledWith(
      AppUpdateCheckTriggerEnum.ChunkLoadError,
    );
  });
});
