import { z } from 'zod';
import { BaseConfigSchema } from '@sagebionetworks/platform/config/angular';

/**
 * Zod schema for application configuration
 * Extends BaseConfigSchema to inherit the standard 'environment' property
 */
export const AppConfigSchema = BaseConfigSchema.extend({
  app: z.object({
    version: z.string().min(1, 'App version is required'),
    telemetry: z.object({
      enabled: z.boolean(),
    }),
  }),

  api: z.object({
    docs: z.object({
      url: z.url({ message: 'API docs URL must be a valid URL' }),
    }),
    csr: z.object({
      url: z.url({ message: 'CSR API URL must be a valid URL' }),
    }),
    ssr: z.object({
      url: z.url({ message: 'SSR API URL must be a valid URL' }),
    }),
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

  urls: z.object({
    privacyPolicy: z.url({ message: 'Privacy policy URL must be a valid URL' }),
    termsOfUse: z.url({ message: 'Terms of use URL must be a valid URL' }),
  }),
});

/**
 * TypeScript type inferred from Zod schema
 * This ensures type safety across the application
 */
export type AppConfig = z.infer<typeof AppConfigSchema>;

/**
 * Runtime configuration with additional computed properties
 * These are added after the base config is loaded
 */
export interface RuntimeAppConfig extends AppConfig {
  isPlatformServer: boolean;
}

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
