import { z } from 'zod';
import { BaseConfigSchema } from '@sagebionetworks/platform/config/angular';

/**
 * Client-side configuration schema
 * This is a subset of the server configuration, containing only properties
 * that are needed by the browser/client application.
 *
 * Key differences from server config:
 * - api.baseUrl (single URL) instead of api.baseUrls.csr/ssr
 * - No server-only properties
 * - Optimized for minimal transfer size
 */
export const ClientConfigSchema = BaseConfigSchema.extend({
  app: z.object({
    version: z.string().min(1, 'App version is required'),
  }),

  telemetry: z.object({
    enabled: z.boolean(),
  }),

  api: z.object({
    baseUrl: z.url({ message: 'API base URL must be a valid URL' }),
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
 * TypeScript type for client configuration
 */
export type ClientConfig = z.infer<typeof ClientConfigSchema>;

/**
 * Runtime client configuration with additional computed properties
 */
export interface RuntimeClientConfig extends ClientConfig {
  isPlatformServer: boolean;
}

/**
 * Validate client configuration object against schema
 * Throws ZodError if validation fails
 */
export function validateClientConfig(config: unknown): ClientConfig {
  return ClientConfigSchema.parse(config);
}

/**
 * Safe validation that returns parsed config or error
 */
export function safeValidateClientConfig(
  config: unknown,
): { success: true; data: ClientConfig } | { success: false; error: z.ZodError } {
  const result = ClientConfigSchema.safeParse(config);
  return result;
}
