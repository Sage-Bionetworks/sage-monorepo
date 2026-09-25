import { TestBed } from '@angular/core/testing';
import * as Sentry from '@sentry/angular';
import { LoggerService } from './logger.service';

jest.mock('@sentry/angular', () => ({
  addBreadcrumb: jest.fn(),
  captureException: jest.fn(),
  captureMessage: jest.fn(),
}));

const WARNING_MESSAGE = 'Failed to fetch data version';

describe('LoggerService', () => {
  let service: LoggerService;

  beforeEach(() => {
    jest.spyOn(console, 'warn').mockImplementation();
    service = TestBed.inject(LoggerService);
  });

  afterEach(() => {
    jest.clearAllMocks();
    jest.restoreAllMocks();
  });

  describe('warn', () => {
    it('should send a warning-level Sentry event with the data as extra context', () => {
      const data = { error: new Error('Unable to connect to the server.') };

      service.warn(WARNING_MESSAGE, data);

      expect(Sentry.captureMessage).toHaveBeenCalledWith(WARNING_MESSAGE, {
        level: 'warning',
        extra: data,
      });
    });

    it('should send a warning-level Sentry event when no data is given', () => {
      service.warn(WARNING_MESSAGE);

      expect(Sentry.captureMessage).toHaveBeenCalledWith(
        WARNING_MESSAGE,
        expect.objectContaining({ level: 'warning' }),
      );
    });
  });
});
