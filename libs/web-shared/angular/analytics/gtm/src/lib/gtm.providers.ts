import { EnvironmentProviders, makeEnvironmentProviders } from '@angular/core';
import { provideGoogleTagManager } from 'angular-google-tag-manager';
import { GTM_CONFIG, GtmConfig } from './gtm.tokens';

/**
 * Provides Google Tag Manager with the given configuration.
 *
 * This function sets up both the GTM service and the configuration token.
 * Apps should call this in their app.config.ts or bootstrap providers.
 *
 * @param config - GTM configuration (gtmId and isPlatformServer)
 * @returns Environment providers for GTM
 *
 * @example
 * ```typescript
 * // In app.config.ts
 * import { provideGtm } from '@sagebionetworks/web-shared/angular/analytics/gtm';
 * import { ConfigService } from './config.service';
 *
 * export const appConfig: ApplicationConfig = {
 *   providers: [
 *     // ... other providers
 *     provideGtm(() => {
 *       const config = inject(ConfigService);
 *       return {
 *         gtmId: config.config.googleTagManagerId,
 *         isPlatformServer: config.config.isPlatformServer
 *       };
 *     })
 *   ]
 * };
 * ```
 */
export function provideGtm(configFactory: () => GtmConfig): EnvironmentProviders {
  const config = configFactory();

  return makeEnvironmentProviders([
    {
      provide: GTM_CONFIG,
      useValue: config,
    },
    provideGoogleTagManager({ id: config.gtmId }),
  ]);
}
