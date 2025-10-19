import { InjectionToken, Provider } from '@angular/core';

/**
 * Required configuration shape for Google Tag Manager integration.
 * The application's config must include these properties.
 */
export interface GoogleTagManagerConfig {
  googleTagManagerId: string;
  isPlatformServer?: boolean;
}

/**
 * Interface that the application's config service must implement
 * to provide Google Tag Manager configuration.
 * Apps should provide their config service using APP_CONFIG_SERVICE token.
 */
export interface AppConfigService<T extends GoogleTagManagerConfig = GoogleTagManagerConfig> {
  readonly config: T;
}

/**
 * Injection token for the application's config service.
 * Apps must provide their config service using this token for GTM to work.
 *
 * Example usage in app.component.ts:
 * ```typescript
 * providers: [
 *   {
 *     provide: APP_CONFIG_SERVICE,
 *     useExisting: ConfigService,
 *   },
 *   createGoogleTagManagerIdProvider(),
 * ]
 * ```
 */
export const APP_CONFIG_SERVICE = new InjectionToken<AppConfigService>('AppConfigService');

/**
 * Checks if a Google Tag Manager ID is set, i.e. not empty or whitespace-only string, null, or undefined.
 *
 * @param id The Google Tag Manager ID to check
 * @returns true if the ID is set (contains at least one non-whitespace character), false otherwise
 */
export function isGoogleTagManagerIdSet(id: string | undefined | null): boolean {
  return !!id?.trim();
}

/**
 * Creates a provider for the Google Tag Manager ID.
 * This provider extracts the googleTagManagerId from the app's config service
 * and makes it available for injection.
 *
 * @returns Provider configuration for 'googleTagManagerId'
 */
export const createGoogleTagManagerIdProvider = <T extends GoogleTagManagerConfig>(): Provider => ({
  provide: 'googleTagManagerId',
  useFactory: (appConfigService: AppConfigService<T>) => appConfigService.config.googleTagManagerId,
  deps: [APP_CONFIG_SERVICE],
});
