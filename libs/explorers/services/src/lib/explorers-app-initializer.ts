import { inject } from '@angular/core';
import { ConfigService } from '@sagebionetworks/explorers/config';
import { DATA_VERSION_PATH } from '@sagebionetworks/explorers/constants';
import { initSentry } from '@sagebionetworks/explorers/sentry';
import { AppUpdateCheckTriggerEnum, AppUpdateService } from './app-update.service';

/**
 * Loads the app config, then starts error reporting and app update detection with it. Must be
 * called from `provideAppInitializer`, since it injects its dependencies. Update detection checks
 * the data version on the API the browser calls, at the same path the generated API clients use.
 */
export function initializeExplorersApp(siteName: string): Promise<void> {
  const configService = inject(ConfigService);
  const appUpdateService = inject(AppUpdateService);
  return configService.loadConfig().then(() => {
    const { csrApiUrl, sentryDsn, sentryEnvironment, sentryRelease, sentryTracesSampleRate } =
      configService.config;
    initSentry({
      dsn: sentryDsn,
      environment: sentryEnvironment,
      release: sentryRelease,
      tracesSampleRate: sentryTracesSampleRate,
      // Intentionally does more than filter: for a chunk-load error, `isAppStale` runs a real
      // version check and shows the update overlay on a mismatch. Chunk failures outside navigation
      // (e.g. a `@defer` block) never reach the router, so this is the only place the app learns
      // of them.
      isStaleClient: () => appUpdateService.isAppStale(AppUpdateCheckTriggerEnum.ChunkLoadError),
    });
    appUpdateService.start(configService.config, siteName, getDataVersionUrl(csrApiUrl));
  });
}

function getDataVersionUrl(apiBaseUrl: string): string {
  return `${apiBaseUrl.replace(/\/+$/, '')}${DATA_VERSION_PATH}`;
}
