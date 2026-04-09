import { z } from 'zod';
import { BaseConfigSchema } from '@sagebionetworks/platform/config/angular';

export const AppConfigSchema = BaseConfigSchema.extend({
  app: z.object({
    version: z.string().min(1, 'App version is required'),
  }),

  links: z.object({
    termsOfService: z.url(),
    contact: z.url(),
    feedback: z.url(),
    sageBionetworks: z.url(),
  }),

  api: z.object({
    baseUrls: z.object({
      csr: z.url({ message: 'API CSR base URL must be a valid URL' }),
      ssr: z.url({ message: 'API SSR base URL must be a valid URL' }),
    }),
  }),

  auth: z.object({
    baseUrls: z.object({
      csr: z.url({ message: 'Auth CSR base URL must be a valid URL' }),
    }),
  }),

  analytics: z.object({
    googleTagManager: z.object({
      enabled: z.boolean(),
      id: z.string(),
    }),
  }),
});

export type AppConfig = z.infer<typeof AppConfigSchema>;

export interface RuntimeServerConfig extends AppConfig {
  isPlatformServer: true;
}

export function validateConfig(config: unknown): AppConfig {
  return AppConfigSchema.parse(config);
}
