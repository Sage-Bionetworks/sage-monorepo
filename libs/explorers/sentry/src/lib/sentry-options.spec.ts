import {
  backendTracingOptions,
  parseTracesSampleRate,
  SentryConfig,
  tracingOptions,
} from './sentry-options';

const baseConfig: SentryConfig = {
  dsn: 'https://test-dsn@sentry.io/123',
  environment: 'dev',
};

describe('tracingOptions', () => {
  it('sets the sample rate when one is configured', () => {
    expect(tracingOptions({ ...baseConfig, tracesSampleRate: 0.1 })).toEqual({
      tracesSampleRate: 0.1,
    });
  });

  it('keeps a configured rate of 0, which records spans but samples none', () => {
    expect(tracingOptions({ ...baseConfig, tracesSampleRate: 0 })).toEqual({ tracesSampleRate: 0 });
  });

  it.each([undefined, null])('leaves the option out when the rate is %s', (tracesSampleRate) => {
    expect(tracingOptions({ ...baseConfig, tracesSampleRate })).toEqual({});
  });
});

describe('backendTracingOptions', () => {
  it("follows the browser trace's sampling decision, and samples nothing without one", () => {
    const inheritedSampleRate = 1;
    const inheritOrSampleWith = jest.fn(() => inheritedSampleRate);
    const { tracesSampler } = backendTracingOptions({ ...baseConfig, tracesSampleRate: 0.1 });

    const sampleRate = tracesSampler?.({
      name: 'GET /v1/comparison-tools/transcriptomics',
      attributes: {},
      inheritOrSampleWith,
    });

    expect(sampleRate).toBe(inheritedSampleRate);
    expect(inheritOrSampleWith).toHaveBeenCalledWith(0);
  });

  it.each([undefined, null])('leaves the option out when the rate is %s', (tracesSampleRate) => {
    expect(backendTracingOptions({ ...baseConfig, tracesSampleRate })).toEqual({});
  });
});

describe('parseTracesSampleRate', () => {
  it.each([
    ['1', 1],
    ['1.0', 1],
    ['0.25', 0.25],
    ['0', 0],
  ])('reads "%s" as %s', (value, rate) => {
    expect(parseTracesSampleRate(value)).toBe(rate);
  });

  it.each([undefined, '', '  '])('leaves tracing off when the variable is %p', (value) => {
    expect(parseTracesSampleRate(value)).toBeUndefined();
  });

  it.each(['1.5', '-0.1', 'all', '10%'])(
    'throws for "%s", so the server fails to start',
    (value) => {
      expect(() => parseTracesSampleRate(value)).toThrow(
        `SENTRY_TRACES_SAMPLE_RATE must be a sample rate from 0.0 to 1.0, got "${value}"`,
      );
    },
  );
});
