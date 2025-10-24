import { z } from 'zod';
import { BaseConfigSchema } from '@sagebionetworks/platform/config/angular';

/**
 * Server-side configuration schema (Full Schema)
 * This schema contains ALL configuration properties, including server-only properties.
 *
 * Used by:
 * - Server-side rendering (SSR) to load full configuration from YAML files
 * - Server-side API calls using api.baseUrls.ssr
 *
 * Note: For client-side configuration, see client-config.schema.ts
 * The client receives a filtered subset of this configuration.
 *
 * Extends BaseConfigSchema to inherit the standard 'environment' property
 */
export const AppConfigSchema = BaseConfigSchema.extend({
  app: z.object({
    version: z.string().min(1, 'App version is required'),
  }),

  telemetry: z.object({
    enabled: z.boolean(),
  }),

  api: z.object({
    baseUrls: z.object({
      csr: z.url({ message: 'CSR API base URL must be a valid URL' }),
      ssr: z.url({ message: 'SSR API base URL must be a valid URL' }),
    }),
    docsUrl: z.url({ message: 'API docs URL must be a valid URL' }),
  }),

  data: z.object({
    updatedOn: z.string().min(1, 'Data updated date is required'),
  }),

  features: z.object({
    announcement: z.object({
      enabled: z.boolean(),
    }),
    operationFilter: z.object({
      enabled: z.boolean(),
    }),
  }),

  analytics: z.object({
    googleTagManager: z.object({
      enabled: z.boolean(),
      id: z.string(),
    }),
  }),

  links: z.object({
    privacyPolicy: z.url({ message: 'Privacy policy URL must be a valid URL' }),
    termsOfUse: z.url({ message: 'Terms of use URL must be a valid URL' }),
  }),
});

/**
 * Server configuration type
 * Full configuration available on the server
 */
export type AppConfig = z.infer<typeof AppConfigSchema>;

/**
 * Alias for clarity: ServerConfig = AppConfig
 * This is the full configuration available on the server
 */
export type ServerConfig = AppConfig;

/**
 * Runtime server configuration with additional computed properties
 * These are added after the base config is loaded
 */
export interface RuntimeAppConfig extends AppConfig {
  isPlatformServer: boolean;
}

/**
 * Alias for clarity: RuntimeServerConfig = RuntimeAppConfig
 */
export type RuntimeServerConfig = RuntimeAppConfig;

/**
 * Validate configuration object against schema
 * Throws ZodError if validation fails
 */
export function validateConfig(config: unknown): AppConfig {
  return AppConfigSchema.parse(config);
}

/**
 * Safe validation that returns parsed config or error
 */
export function safeValidateConfig(
  config: unknown,
): { success: true; data: AppConfig } | { success: false; error: z.ZodError } {
  const result = AppConfigSchema.safeParse(config);
  return result;
}

/**
 * Empty config for initialization (use with caution)
 */
export const EMPTY_APP_CONFIG: Partial<AppConfig> = {};
