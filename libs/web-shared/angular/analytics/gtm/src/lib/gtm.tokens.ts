import { InjectionToken } from '@angular/core';
import { z } from 'zod';

/**
 * Zod schema for Google Tag Manager configuration
 */
export const GtmConfigSchema = z.object({
  /**
   * Whether GTM is enabled. If false, no tracking will occur.
   */
  enabled: z.boolean(),

  /**
   * Google Tag Manager Container ID (e.g., 'GTM-XXXXXX')
   * Must be a non-empty string with at least one non-whitespace character
   */
  gtmId: z.string().trim().min(1, 'GTM ID must not be empty'),

  /**
   * Whether the app is running on the server (SSR).
   * GTM should only be initialized on the client side.
   */
  isPlatformServer: z.boolean().optional(),
});

/**
 * Configuration required for Google Tag Manager integration.
 * Apps provide this configuration via the GTM_CONFIG token.
 */
export type GtmConfig = z.infer<typeof GtmConfigSchema>;

/**
 * Validate GTM configuration against schema
 * Throws ZodError if validation fails
 */
export function validateGtmConfig(config: unknown): GtmConfig {
  return GtmConfigSchema.parse(config);
}

/**
 * Injection token for Google Tag Manager configuration.
 *
 * Apps must provide their GTM configuration using this token.
 * Use `provideGtmConfig()` to automatically validate the configuration.
 *
 * @example
 * ```typescript
 * // In app.config.ts:
 * import { provideGtmConfig } from '@sagebionetworks/web-shared/angular/analytics/gtm';
 *
 * export const appConfig: ApplicationConfig = {
 *   providers: [
 *     provideGtmConfig((config: ConfigService) => ({
 *       enabled: config.config.google.tagManager.enabled,
 *       gtmId: config.config.google.tagManager.id,
 *       isPlatformServer: config.config.isPlatformServer
 *     }), [ConfigService]),
 *   ]
 * };
 * ```
 */
export const GTM_CONFIG = new InjectionToken<GtmConfig>('GTM_CONFIG');
