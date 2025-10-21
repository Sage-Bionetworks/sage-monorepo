import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { GTM_CONFIG, GtmConfig, validateGtmConfig } from './gtm.tokens';

/**
 * Provides Google Tag Manager configuration (GTM_CONFIG token).
 *
 * This sets up the primary GTM configuration that the GoogleTagManagerComponent uses
 * to determine if GTM is enabled, get the GTM ID, and check if running on server.
 *
 * The configuration is automatically validated using Zod schema at runtime.
 * If validation fails, a ZodError will be thrown with detailed error messages.
 *
 * @param configFactory - Factory function that returns GTM configuration
 * @param deps - Dependencies to inject into the factory function (e.g., [ConfigService])
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
 *       enabled: config.config.analytics.googleTagManager.enabled,
 *       gtmId: config.config.analytics.googleTagManager.id,
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
      useFactory: (...factoryDeps: T[]) => {
        const config = configFactory(...factoryDeps);
        // Validate configuration at runtime using Zod schema
        return validateGtmConfig(config);
      },
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
