import * as Sentry from '@sentry/angular';
import { initSentry, SentryConfig } from './init-sentry';

jest.mock('@sentry/angular', () => ({
  init: jest.fn(),
}));

const mockConfig: SentryConfig = {
  dsn: 'https://test-dsn@sentry.io/123',
  environment: 'dev',
};

describe('initSentry', () => {
  afterEach(() => {
    (Sentry.init as jest.Mock).mockClear();
  });

  it('initializes Sentry with dsn, environment, and release', () => {
    initSentry({ ...mockConfig, release: '1.0.0+abc1234' });

    expect(Sentry.init).toHaveBeenCalledWith({
      dsn: mockConfig.dsn,
      environment: 'dev',
      release: '1.0.0+abc1234',
      sendDefaultPii: false,
    });
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
});
