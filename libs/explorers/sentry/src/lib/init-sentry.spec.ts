import * as Sentry from '@sentry/angular';
import { initSentry, SentryConfig } from './init-sentry';

jest.mock('@sentry/angular', () => ({
  init: jest.fn(),
}));

const mockConfig: SentryConfig = {
  dsn: 'https://test-dsn@sentry.io/123',
};

describe('initSentry', () => {
  afterEach(() => {
    (Sentry.init as jest.Mock).mockClear();
  });

  it('should initialize Sentry with environment and release', () => {
    initSentry({ ...mockConfig, environment: 'prod', release: '1.0.0+abc1234' });

    expect(Sentry.init).toHaveBeenCalledWith({
      dsn: mockConfig.dsn,
      environment: 'prod',
      release: '1.0.0+abc1234',
      sendDefaultPii: false,
    });
  });

  it('should initialize Sentry without environment when not provided', () => {
    initSentry(mockConfig);

    expect(Sentry.init).toHaveBeenCalledWith(expect.objectContaining({ environment: undefined }));
  });

  it('should initialize Sentry without environment when empty string', () => {
    initSentry({ ...mockConfig, environment: '' });

    expect(Sentry.init).toHaveBeenCalledWith(expect.objectContaining({ environment: undefined }));
  });

  it('should initialize Sentry without release when not provided', () => {
    initSentry(mockConfig);

    expect(Sentry.init).toHaveBeenCalledWith(expect.objectContaining({ release: undefined }));
  });

  it('should initialize Sentry without release when empty string', () => {
    initSentry({ ...mockConfig, release: '' });

    expect(Sentry.init).toHaveBeenCalledWith(expect.objectContaining({ release: undefined }));
  });

  it('should warn and not initialize Sentry when dsn is not provided', () => {
    const warnSpy = jest.spyOn(console, 'warn').mockImplementation();
    initSentry({});

    expect(console.warn).toHaveBeenCalledWith('Sentry DSN is not configured.');
    expect(Sentry.init).not.toHaveBeenCalled();
    warnSpy.mockRestore();
  });

  it('should warn and not initialize Sentry when dsn is empty string', () => {
    const warnSpy = jest.spyOn(console, 'warn').mockImplementation();
    initSentry({ dsn: '' });

    expect(console.warn).toHaveBeenCalledWith('Sentry DSN is not configured.');
    expect(Sentry.init).not.toHaveBeenCalled();
    warnSpy.mockRestore();
  });
});
