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

  it.each(['1.5', '-0.1', 'all'])('leaves tracing off with a warning for "%s"', (value) => {
    const warnSpy = jest.spyOn(console, 'warn').mockImplementation(() => undefined);

    expect(parseTracesSampleRate(value)).toBeUndefined();
    expect(warnSpy).toHaveBeenCalledWith(expect.stringContaining('SENTRY_TRACES_SAMPLE_RATE'));

    warnSpy.mockRestore();
  });
});
