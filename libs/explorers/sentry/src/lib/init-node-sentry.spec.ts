import * as Sentry from '@sentry/node';
import { initNodeSentry } from './init-node-sentry';
import { SENTRY_DATA_COLLECTION, SentryConfig } from './sentry-options';

jest.mock('@sentry/node', () => ({
  init: jest.fn(),
}));

const mockConfig: SentryConfig = {
  dsn: 'https://test-dsn@sentry.io/123',
  environment: 'dev',
};
const release = 'model-ad@1.0.0+abc1234';

describe('initNodeSentry', () => {
  afterEach(() => {
    (Sentry.init as jest.Mock).mockClear();
  });

  it('initializes Sentry with dsn, environment, release, and the shared data collection', () => {
    initNodeSentry({ ...mockConfig, release });

    expect(Sentry.init).toHaveBeenCalledWith({
      dsn: mockConfig.dsn,
      environment: mockConfig.environment,
      release,
      dataCollection: SENTRY_DATA_COLLECTION,
      enableRuntimeChannelInjection: false,
    });
  });

  it("follows the browser trace's sampling decision when a sample rate is configured", () => {
    const inheritOrSampleWith = jest.fn(() => 1);

    initNodeSentry({ ...mockConfig, tracesSampleRate: 1 });
    const { tracesSampler } = (Sentry.init as jest.Mock).mock.calls[0][0];
    tracesSampler({ name: 'GET /v1/genes', attributes: {}, inheritOrSampleWith });

    expect(inheritOrSampleWith).toHaveBeenCalledWith(0);
  });

  it('sends no performance data when no sample rate is configured', () => {
    initNodeSentry(mockConfig);

    const options = (Sentry.init as jest.Mock).mock.calls[0][0];
    expect(options).not.toHaveProperty('tracesSampleRate');
    expect(options).not.toHaveProperty('tracesSampler');
  });

  it('passes release: undefined when release is empty', () => {
    initNodeSentry({ ...mockConfig, release: '' });

    expect(Sentry.init).toHaveBeenCalledWith(expect.objectContaining({ release: undefined }));
  });

  it('does not call Sentry.init when environment is empty', () => {
    const warnSpy = jest.spyOn(console, 'warn').mockImplementation(() => undefined);

    initNodeSentry({ ...mockConfig, environment: '' });

    expect(Sentry.init).not.toHaveBeenCalled();
    expect(warnSpy).toHaveBeenCalledWith(expect.stringContaining('SENTRY_ENVIRONMENT'));

    warnSpy.mockRestore();
  });

  it('does not call Sentry.init when dsn is empty', () => {
    const warnSpy = jest.spyOn(console, 'warn').mockImplementation(() => undefined);

    initNodeSentry({ ...mockConfig, dsn: '' });

    expect(Sentry.init).not.toHaveBeenCalled();
    expect(warnSpy).toHaveBeenCalledWith(expect.stringContaining('SENTRY_DSN'));

    warnSpy.mockRestore();
  });
});
