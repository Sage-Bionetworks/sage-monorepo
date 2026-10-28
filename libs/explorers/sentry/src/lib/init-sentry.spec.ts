import * as Sentry from '@sentry/angular';
import type { ErrorEvent, EventHint } from '@sentry/angular';
import { BrowserSentryConfig, initSentry } from './init-sentry';
import { SENTRY_DATA_COLLECTION } from './sentry-options';

jest.mock('@sentry/angular', () => ({
  init: jest.fn(),
  browserTracingIntegration: jest.fn(() => mockBrowserTracingIntegration),
}));

const mockBrowserTracingIntegration = { name: 'BrowserTracing' };

const mockConfig: BrowserSentryConfig = {
  dsn: 'https://test-dsn@sentry.io/123',
  environment: 'dev',
};

const event = { message: 'Uncaught error' } as ErrorEvent;
const chunkLoadError = new TypeError(
  'Failed to fetch dynamically imported module: https://modeladexplorer.org/chunk-ABCD1234.js',
);
const unrelatedError = new TypeError('Cannot read properties of undefined');

function runBeforeSend(originalException: unknown) {
  const { beforeSend } = (Sentry.init as jest.Mock).mock.calls[0][0];
  const hint: EventHint = { originalException };
  return beforeSend(event, hint);
}

describe('initSentry', () => {
  afterEach(() => {
    (Sentry.init as jest.Mock).mockClear();
  });

  it('initializes Sentry with dsn, environment, and release', () => {
    initSentry({ ...mockConfig, release: 'agora@1.0.0+abc1234' });

    expect(Sentry.init).toHaveBeenCalledWith({
      dsn: mockConfig.dsn,
      environment: 'dev',
      release: 'agora@1.0.0+abc1234',
      dataCollection: SENTRY_DATA_COLLECTION,
      integrations: [mockBrowserTracingIntegration],
      beforeSend: expect.any(Function),
    });
  });

  it('sends performance data at the configured sample rate', () => {
    const fullSampleRate = 1;

    initSentry({ ...mockConfig, tracesSampleRate: fullSampleRate });

    expect(Sentry.init).toHaveBeenCalledWith(
      expect.objectContaining({ tracesSampleRate: fullSampleRate }),
    );
  });

  it.each([undefined, null])(
    'propagates trace headers without sending performance data when the sample rate is %s',
    (tracesSampleRate) => {
      initSentry({ ...mockConfig, tracesSampleRate });

      const options = (Sentry.init as jest.Mock).mock.calls[0][0];
      expect(options.integrations).toContain(mockBrowserTracingIntegration);
      expect(options).not.toHaveProperty('tracesSampleRate');
      expect(options).not.toHaveProperty('tracesSampler');
    },
  );

  it('does not collect user info, cookies, or HTTP bodies', () => {
    initSentry(mockConfig);

    expect(Sentry.init).toHaveBeenCalledWith(
      expect.objectContaining({
        dataCollection: expect.objectContaining({
          userInfo: false,
          cookies: false,
          httpBodies: [],
        }),
      }),
    );
  });

  it('passes release: undefined when release is not provided', () => {
    initSentry(mockConfig);

    expect(Sentry.init).toHaveBeenCalledWith(expect.objectContaining({ release: undefined }));
  });

  it('passes release: undefined when release is empty', () => {
    initSentry({ ...mockConfig, release: '' });

    expect(Sentry.init).toHaveBeenCalledWith(expect.objectContaining({ release: undefined }));
  });

  it('does not call Sentry.init when environment is empty', () => {
    const warnSpy = jest.spyOn(console, 'warn').mockImplementation(() => undefined);

    initSentry({ ...mockConfig, environment: '' });

    expect(Sentry.init).not.toHaveBeenCalled();
    expect(warnSpy).toHaveBeenCalledWith(expect.stringContaining('SENTRY_ENVIRONMENT'));

    warnSpy.mockRestore();
  });

  it('does not call Sentry.init when dsn is empty', () => {
    const warnSpy = jest.spyOn(console, 'warn').mockImplementation(() => undefined);

    initSentry({ ...mockConfig, dsn: '' });

    expect(Sentry.init).not.toHaveBeenCalled();
    expect(warnSpy).toHaveBeenCalledWith(expect.stringContaining('SENTRY_DSN'));

    warnSpy.mockRestore();
  });

  describe('beforeSend', () => {
    it('drops a chunk-load error from a stale tab', async () => {
      initSentry({ ...mockConfig, isStaleClient: () => Promise.resolve(true) });

      await expect(runBeforeSend(chunkLoadError)).resolves.toBeNull();
    });

    it('reports a chunk-load error from a current tab', async () => {
      initSentry({ ...mockConfig, isStaleClient: () => Promise.resolve(false) });

      await expect(runBeforeSend(chunkLoadError)).resolves.toBe(event);
    });

    it('reports other errors from a stale tab without checking staleness', async () => {
      const isStaleClient = jest.fn(() => Promise.resolve(true));
      initSentry({ ...mockConfig, isStaleClient });

      await expect(runBeforeSend(unrelatedError)).resolves.toBe(event);
      expect(isStaleClient).not.toHaveBeenCalled();
    });

    it('reports chunk-load errors when staleness cannot be checked', async () => {
      initSentry(mockConfig);

      await expect(runBeforeSend(chunkLoadError)).resolves.toBe(event);
    });
  });
});
