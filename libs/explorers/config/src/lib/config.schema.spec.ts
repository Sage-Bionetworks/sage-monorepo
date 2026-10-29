import { PLATFORM_ID } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { EnvironmentMapperService } from '@sagebionetworks/platform/config/angular';
import { SentryTracesSampleRateSchema } from './config.schema';

const SAMPLE_RATE_ENVIRONMENT_VARIABLE = 'SENTRY_TRACES_SAMPLE_RATE';
/** Mirrors the sample rate key in the apps' `application.yaml`. */
const configFileSampleRate = { sentryTracesSampleRate: null };

function parseSampleRateFromEnvironment(value: string): number | null {
  jest.replaceProperty(process, 'env', {
    ...process.env,
    [SAMPLE_RATE_ENVIRONMENT_VARIABLE]: value,
  });
  const { sentryTracesSampleRate } =
    TestBed.inject(EnvironmentMapperService).applyEnvironmentOverrides(configFileSampleRate);
  return SentryTracesSampleRateSchema.parse(sentryTracesSampleRate);
}

describe('SentryTracesSampleRateSchema', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [{ provide: PLATFORM_ID, useValue: 'server' }] });
  });

  it('turns tracing off when the config file leaves the rate unset', () => {
    expect(SentryTracesSampleRateSchema.parse(undefined)).toBeNull();
    expect(SentryTracesSampleRateSchema.parse(null)).toBeNull();
  });

  it('turns tracing off when the environment variable is empty, as in `.env.example`', () => {
    expect(parseSampleRateFromEnvironment('')).toBeNull();
    expect(parseSampleRateFromEnvironment('  ')).toBeNull();
  });

  it('reads a rate from the environment variable', () => {
    expect(parseSampleRateFromEnvironment('0.1')).toBe(0.1);
    expect(parseSampleRateFromEnvironment('.5')).toBe(0.5);
    expect(parseSampleRateFromEnvironment('1')).toBe(1);
  });

  it('rejects a rate that is not a number from 0 to 1', () => {
    for (const invalidRate of ['abc', '10', '-0.1']) {
      expect(() => parseSampleRateFromEnvironment(invalidRate)).toThrow();
    }
  });
});
