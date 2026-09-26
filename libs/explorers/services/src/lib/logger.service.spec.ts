import { TestBed } from '@angular/core/testing';
import * as Sentry from '@sentry/angular';
import { LoggerService, SourceLogger } from './logger.service';

jest.mock('@sentry/angular', () => ({
  addBreadcrumb: jest.fn(),
  captureException: jest.fn(),
  captureMessage: jest.fn(),
}));

const SOURCE = 'GeneDetailsComponent';
const LOG_MESSAGE = 'Loading gene';
const WARNING_MESSAGE = 'Failed to fetch data version';
const ERROR_MESSAGE = 'Error retrieving comparison tool config';

function title(message: string): string {
  return `${SOURCE}: ${message}`;
}

describe('LoggerService', () => {
  let logger: SourceLogger;

  beforeEach(() => {
    jest.spyOn(console, 'log').mockImplementation();
    jest.spyOn(console, 'warn').mockImplementation();
    jest.spyOn(console, 'error').mockImplementation();
    logger = TestBed.inject(LoggerService).forSource(SOURCE);
  });

  afterEach(() => {
    jest.clearAllMocks();
    jest.restoreAllMocks();
  });

  it('should create a logger bound to the source', () => {
    expect(logger).toBeInstanceOf(SourceLogger);
    expect(logger.source).toBe(SOURCE);
  });

  describe('log', () => {
    it('should record a breadcrumb in the source category with the data', () => {
      const data = { geneId: 'ENSG00000130203' };

      logger.log(LOG_MESSAGE, data);

      expect(Sentry.addBreadcrumb).toHaveBeenCalledWith({
        category: SOURCE,
        message: LOG_MESSAGE,
        level: 'info',
        data,
      });
    });

    it('should never create a Sentry event', () => {
      logger.log(LOG_MESSAGE);

      expect(Sentry.captureMessage).not.toHaveBeenCalled();
      expect(Sentry.captureException).not.toHaveBeenCalled();
    });
  });

  describe('warn', () => {
    it('should send a warning titled and grouped by source and message, with the data as extra context', () => {
      const data = { requested: 60, max: 50 };

      logger.warn(WARNING_MESSAGE, data);

      expect(Sentry.captureMessage).toHaveBeenCalledWith(title(WARNING_MESSAGE), {
        level: 'warning',
        fingerprint: [SOURCE, WARNING_MESSAGE],
        tags: { source: SOURCE },
        extra: data,
      });
    });

    it('should write the titled message to the console', () => {
      logger.warn(WARNING_MESSAGE);

      expect(console.warn).toHaveBeenCalledWith('[WARN]', title(WARNING_MESSAGE));
    });
  });

  describe('error', () => {
    const data = { url: '/comparison/expression?categories=RNA' };

    function capturedException(): Error {
      return (Sentry.captureException as jest.Mock).mock.calls[0][0];
    }

    it('should send an error titled by source and message with the caught error as its cause', () => {
      const cause = new Error('HTTP 500 GET /v1/comparison-tools/config');

      logger.error(ERROR_MESSAGE, { error: cause, data });

      expect(Sentry.captureException).toHaveBeenCalledWith(expect.any(Error), {
        level: 'error',
        fingerprint: [SOURCE, ERROR_MESSAGE],
        tags: { source: SOURCE },
        extra: data,
      });
      expect(capturedException().message).toBe(title(ERROR_MESSAGE));
      expect(capturedException().cause).toBe(cause);
    });

    it('should link a non-Error value as the cause', () => {
      const cause = { status: 500, statusText: 'Internal Server Error' };

      logger.error(ERROR_MESSAGE, { error: cause });

      expect(capturedException().cause).toBe(cause);
      expect(Sentry.captureMessage).not.toHaveBeenCalled();
    });

    it('should send an error-level message when only data is given', () => {
      logger.error(ERROR_MESSAGE, { data });

      expect(Sentry.captureMessage).toHaveBeenCalledWith(title(ERROR_MESSAGE), {
        level: 'error',
        fingerprint: [SOURCE, ERROR_MESSAGE],
        tags: { source: SOURCE },
        extra: data,
      });
      expect(Sentry.captureException).not.toHaveBeenCalled();
    });

    it('should write the titled message, error, and data to the console', () => {
      const cause = new Error('boom');

      logger.error(ERROR_MESSAGE, { error: cause, data });

      expect(console.error).toHaveBeenCalledWith('[ERROR]', title(ERROR_MESSAGE), cause, data);
    });
  });

  it('should keep separate sources in separate issues for the same message', () => {
    const otherSource = 'DiseaseCorrelationComparisonToolComponent';

    logger.warn(WARNING_MESSAGE);
    TestBed.inject(LoggerService).forSource(otherSource).warn(WARNING_MESSAGE);

    const fingerprints = (Sentry.captureMessage as jest.Mock).mock.calls.map(
      ([, context]) => context.fingerprint,
    );
    expect(fingerprints).toEqual([
      [SOURCE, WARNING_MESSAGE],
      [otherSource, WARNING_MESSAGE],
    ]);
  });
});
