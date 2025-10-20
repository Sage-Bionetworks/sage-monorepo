import { EnvironmentProviders, makeEnvironmentProviders } from '@angular/core';
import { GTM_CONFIG, GtmConfig } from './gtm.tokens';

/**
 * Provides Google Tag Manager configuration token.
 *
 * This function sets up the GTM_CONFIG injection token.
 * Apps should also call provideGoogleTagManager from 'angular-google-tag-manager' separately.
 *
 * @param configFactory - Factory function that returns GTM configuration
 * @returns Environment providers for GTM
 *
 * @example
 * ```typescript
 * // In app.config.ts
 * import { provideGtm, GTM_CONFIG } from '@sagebionetworks/web-shared/angular/analytics/gtm';
 * import { provideGoogleTagManager } from 'angular-google-tag-manager';
 * import { ConfigService } from './config.service';
 *
 * export const appConfig: ApplicationConfig = {
 *   providers: [
 *     // ... other providers
 *     {
 *       provide: GTM_CONFIG,
 *       useFactory: () => {
 *         const config = inject(ConfigService);
 *         return {
 *           gtmId: config.config.googleTagManagerId,
 *           isPlatformServer: config.config.isPlatformServer,
 *         };
 *       },
 *     },
 *     provideGoogleTagManager({
 *       id: inject(ConfigService).config.googleTagManagerId,
 *     }),
 *   ]
 * };
 * ```
 */
export function provideGtm(configFactory: () => GtmConfig): EnvironmentProviders {
  return makeEnvironmentProviders([
    {
      provide: GTM_CONFIG,
      useFactory: configFactory,
    },
  ]);
}
