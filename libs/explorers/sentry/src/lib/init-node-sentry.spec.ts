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
