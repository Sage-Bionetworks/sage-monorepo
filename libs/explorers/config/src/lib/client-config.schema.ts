import { z } from 'zod';
import { BaseConfigSchema } from '@sagebionetworks/platform/config/angular';

export const ClientConfigSchema = BaseConfigSchema.extend({
  appVersion: z.string().min(1),
  commitSha: z.string().default(''),
  apiDocsUrl: z.url(),
  csrApiUrl: z.url(),
  googleTagManagerId: z.string().default(''),
  sentryRelease: z.string().default(''),
});

export type ClientConfig = z.infer<typeof ClientConfigSchema>;

export interface RuntimeClientConfig extends ClientConfig {
  isPlatformServer: boolean;
}
