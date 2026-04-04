import { z } from 'zod';
import { BaseConfigSchema } from '@sagebionetworks/platform/config/angular';

export const AppConfigSchema = BaseConfigSchema.extend({
  app: z.object({
    version: z.string().min(1, 'App version is required'),
    termsOfServiceUrl: z.url(),
    contactUrl: z.url(),
    feedbackUrl: z.url(),
    sageBionetworksUrl: z.url(),
  }),

  api: z.object({
    csrBaseUrl: z.url({ message: 'CSR API base URL must be a valid URL' }),
    ssrBaseUrl: z.url({ message: 'SSR API base URL must be a valid URL' }),
  }),

  auth: z.object({
    csrBaseUrl: z.url({ message: 'Auth CSR base URL must be a valid URL' }),
    ssrBaseUrl: z.url({ message: 'Auth SSR base URL must be a valid URL' }),
  }),
});

export type AppConfig = z.infer<typeof AppConfigSchema>;

export interface RuntimeAppConfig extends AppConfig {
  isPlatformServer: boolean;
}

export function validateConfig(config: unknown): AppConfig {
  return AppConfigSchema.parse(config);
}
