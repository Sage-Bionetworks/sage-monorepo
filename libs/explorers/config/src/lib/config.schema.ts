import { z } from 'zod';
import { BaseConfigSchema } from '@sagebionetworks/platform/config/angular';

export const AppConfigSchema = BaseConfigSchema.extend({
  appVersion: z.string().min(1),
  commitSha: z.string().default(''),
  apiDocsUrl: z.url(),
  csrApiUrl: z.url(),
  ssrApiUrl: z.url(),
  googleTagManagerId: z.string().default(''),
  sentryRelease: z.string().default(''),
});

export type AppConfig = z.infer<typeof AppConfigSchema>;
export type ServerConfig = AppConfig;

export interface RuntimeServerConfig extends AppConfig {
  isPlatformServer: true;
}

export function validateConfig(config: unknown): AppConfig {
  return AppConfigSchema.parse(config);
}
