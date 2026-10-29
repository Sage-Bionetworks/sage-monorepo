import { z } from 'zod';
import { BaseConfigSchema } from '@sagebionetworks/platform/config/angular';

/**
 * Unset, null, or empty turns performance tracing off. An environment variable override stays a
 * string when the config loader can't read it as a number, such as an empty
 * `SENTRY_TRACES_SAMPLE_RATE=` or `.5`, so a string is converted here.
 */
export const SentryTracesSampleRateSchema = z
  .preprocess(
    (value) => (typeof value === 'string' ? parseSampleRateString(value) : value),
    z.number().min(0).max(1).nullable(),
  )
  .default(null);

function parseSampleRateString(value: string): number | null {
  return value.trim() === '' ? null : Number(value);
}

export const AppConfigSchema = BaseConfigSchema.extend({
  appVersion: z.string().min(1),
  commitSha: z.string().default(''),
  apiDocsUrl: z.url(),
  csrApiUrl: z.url(),
  ssrApiUrl: z.url(),
  googleTagManagerEnabled: z.boolean(),
  googleTagManagerId: z.string().default(''),
  sentryDsn: z.string().default(''),
  sentryEnvironment: z.string().default(''),
  sentryRelease: z.string().default(''),
  sentryTracesSampleRate: SentryTracesSampleRateSchema,
});

export type AppConfig = z.infer<typeof AppConfigSchema>;
export type ServerConfig = AppConfig;

export interface RuntimeServerConfig extends AppConfig {
  isPlatformServer: true;
}

export function validateConfig(config: unknown): AppConfig {
  return AppConfigSchema.parse(config);
}
