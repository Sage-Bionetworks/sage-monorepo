import { z } from 'zod';
import { BaseConfigSchema } from '@sagebionetworks/platform/config/angular';

export const ClientConfigSchema = BaseConfigSchema.extend({
  appVersion: z.string().min(1),
  commitSha: z.string().default(''),
  apiDocsUrl: z.url(),
  csrApiUrl: z.url(),
  googleTagManagerEnabled: z.boolean(),
  googleTagManagerId: z.string().default(''),
  sentryDsn: z.string().default(''),
  sentryEnvironment: z.string().default(''),
  sentryRelease: z.string().default(''),
  sentryTracesSampleRate: z.number().min(0).max(1).nullable().default(null),
});

export type ClientConfig = z.infer<typeof ClientConfigSchema>;

export interface RuntimeClientConfig extends ClientConfig {
  isPlatformServer: false;
}
