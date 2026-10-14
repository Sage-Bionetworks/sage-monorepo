import { inject } from '@angular/core';
import { ConfigService } from '@sagebionetworks/explorers/config';
import { initSentry } from '@sagebionetworks/explorers/sentry';
import { AppUpdateCheckTriggerEnum, AppUpdateService } from './app-update.service';

/**
 * Loads the app config, then starts error reporting and app update detection with it. Must be
 * called from `provideAppInitializer`, since it injects its dependencies.
 */
export function initializeExplorersApp(siteName: string): Promise<void> {
  const configService = inject(ConfigService);
  const appUpdateService = inject(AppUpdateService);
  return configService.loadConfig().then(() => {
    const { sentryDsn, sentryEnvironment, sentryRelease } = configService.config;
    initSentry({
      dsn: sentryDsn,
      environment: sentryEnvironment,
      release: sentryRelease,
      isStaleClient: () => appUpdateService.checkNow(AppUpdateCheckTriggerEnum.ChunkLoadError),
    });
    appUpdateService.start(configService.config, siteName);
  });
}
