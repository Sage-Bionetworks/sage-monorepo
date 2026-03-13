import * as Sentry from '@sentry/angular';
import { getSentryEnvironment, initSentry, SentryConfig } from './init-sentry';

jest.mock('@sentry/angular', () => ({
  init: jest.fn(),
}));

const hostEnvironmentMap: Record<string, string> = {
  'app-dev.example.com': 'dev',
  'app-stage.example.com': 'stage',
  'app.example.com': 'prod',
};

const mockConfig: SentryConfig = {
  dsn: 'https://test-dsn@sentry.io/123',
  hostEnvironmentMap,
};

describe('getSentryEnvironment', () => {
  // The "server" (SSR) path cannot be tested in jsdom since window is always defined,
  // and passing undefined explicitly triggers the default parameter which reads window.location.hostname.

  it('should return "localhost" for localhost', () => {
    expect(getSentryEnvironment(hostEnvironmentMap, 'localhost')).toBe('localhost');
  });

  it('should return "localhost" for 127.0.0.1', () => {
    expect(getSentryEnvironment(hostEnvironmentMap, '127.0.0.1')).toBe('localhost');
  });

  it('should resolve a mapped hostname to its environment', () => {
    expect(getSentryEnvironment(hostEnvironmentMap, 'app-dev.example.com')).toBe('dev');
    expect(getSentryEnvironment(hostEnvironmentMap, 'app-stage.example.com')).toBe('stage');
    expect(getSentryEnvironment(hostEnvironmentMap, 'app.example.com')).toBe('prod');
  });

  it('should fall back to the raw hostname when not in the map', () => {
    expect(getSentryEnvironment(hostEnvironmentMap, 'unknown-host.example.com')).toBe(
      'unknown-host.example.com',
    );
  });
});

describe('initSentry', () => {
  afterEach(() => {
    (Sentry.init as jest.Mock).mockClear();
  });

  it('should initialize Sentry with release from config', () => {
    initSentry({ ...mockConfig, release: '1.0.0+abc1234' });

    expect(Sentry.init).toHaveBeenCalledWith(
      expect.objectContaining({
        dsn: mockConfig.dsn,
        release: '1.0.0+abc1234',
        sendDefaultPii: false,
      }),
    );
  });

  it('should initialize Sentry without release when release is not provided', () => {
    initSentry(mockConfig);

    expect(Sentry.init).toHaveBeenCalledWith(expect.objectContaining({ release: undefined }));
  });

  it('should initialize Sentry without release when release is empty string', () => {
    initSentry({ ...mockConfig, release: '' });

    expect(Sentry.init).toHaveBeenCalledWith(expect.objectContaining({ release: undefined }));
  });
});
