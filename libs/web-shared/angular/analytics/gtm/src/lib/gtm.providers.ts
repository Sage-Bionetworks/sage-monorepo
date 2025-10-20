import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { GTM_CONFIG, GtmConfig } from './gtm.tokens';

/**
 * Provides Google Tag Manager configuration (GTM_CONFIG token).
 *
 * This sets up the primary GTM configuration that the GoogleTagManagerComponent uses
 * to determine if GTM is enabled, get the GTM ID, and check if running on server.
 *
 * @param configFactory - Factory function that returns GTM configuration
 * @returns Environment providers for GTM_CONFIG token
 *
 * @example
 * ```typescript
 * // In app.config.ts
 * import { provideGtmConfig } from '@sagebionetworks/web-shared/angular/analytics/gtm';
 * import { ConfigService } from './config.service';
 *
 * export const appConfig: ApplicationConfig = {
 *   providers: [
 *     provideGtmConfig((config: ConfigService) => ({
 *       enabled: config.config.google.tagManager.enabled,
 *       gtmId: config.config.google.tagManager.id,
 *       isPlatformServer: config.config.isPlatformServer,
 *     }), [ConfigService]),
 *   ]
 * };
 * ```
 */
export function provideGtmConfig<T = unknown>(
  configFactory: (...deps: T[]) => GtmConfig,
  deps: unknown[] = [],
): EnvironmentProviders {
  return makeEnvironmentProviders([
    {
      provide: GTM_CONFIG,
      useFactory: configFactory,
      deps,
    },
  ]);
}

/**
 * Provides the 'googleTagManagerId' token required by angular-google-tag-manager library.
 *
 * This derives the GTM ID from the GTM_CONFIG token, ensuring a single source of truth.
 * The third-party GoogleTagManagerService expects this string token to be provided.
 *
 * @returns Provider for 'googleTagManagerId' token
 *
 * @example
 * ```typescript
 * // In app.config.ts
 * import { provideGtmConfig, provideGtmId } from '@sagebionetworks/web-shared/angular/analytics/gtm';
 *
 * export const appConfig: ApplicationConfig = {
 *   providers: [
 *     provideGtmConfig(...),  // Provide GTM_CONFIG first
 *     provideGtmId(),          // Then derive the ID from it
 *   ]
 * };
 * ```
 */
export function provideGtmId(): Provider {
  return {
    provide: 'googleTagManagerId',
    useFactory: (gtmConfig: GtmConfig) => gtmConfig.gtmId,
    deps: [GTM_CONFIG],
  };
}
