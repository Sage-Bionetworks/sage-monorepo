import { z } from 'zod';
import { BaseConfigSchema } from '@sagebionetworks/platform/config/angular';
import { SentryTracesSampleRateSchema } from './config.schema';

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
  sentryTracesSampleRate: SentryTracesSampleRateSchema,
});

export type ClientConfig = z.infer<typeof ClientConfigSchema>;

export interface RuntimeClientConfig extends ClientConfig {
  isPlatformServer: false;
}
