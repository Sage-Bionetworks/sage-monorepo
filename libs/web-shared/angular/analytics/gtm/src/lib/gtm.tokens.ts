import { InjectionToken } from '@angular/core';

/**
 * Configuration required for Google Tag Manager integration.
 * Apps provide this configuration via the GTM_CONFIG token.
 */
export interface GtmConfig {
  /**
   * Whether GTM is enabled. If false, no tracking will occur.
   */
  enabled: boolean;

  /**
   * Google Tag Manager Container ID (e.g., 'GTM-XXXXXX')
   */
  gtmId: string;

  /**
   * Whether the app is running on the server (SSR).
   * GTM should only be initialized on the client side.
   */
  isPlatformServer?: boolean;
}

/**
 * Injection token for Google Tag Manager configuration.
 *
 * Apps must provide their GTM configuration using this token.
 *
 * @example
 * ```typescript
 * // In app.config.ts or app.component.ts providers:
 * {
 *   provide: GTM_CONFIG,
 *   useFactory: () => {
 *     const config = inject(ConfigService);
 *     return {
 *       gtmId: config.config.googleTagManagerId,
 *       isPlatformServer: config.config.isPlatformServer
 *     };
 *   }
 * }
 * ```
 */
export const GTM_CONFIG = new InjectionToken<GtmConfig>('GTM_CONFIG');

/**
 * Checks if a Google Tag Manager ID is set, i.e. not empty or whitespace-only string, null, or undefined.
 *
 * @param id The Google Tag Manager ID to check
 * @returns true if the ID is set (contains at least one non-whitespace character), false otherwise
 */
export function isGtmIdSet(id: string | undefined | null): boolean {
  return !!id?.trim();
}
