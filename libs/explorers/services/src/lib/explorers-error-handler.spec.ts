import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ErrorOverlayService } from './error-overlay.service';
import { ExplorersErrorHandler, UNHANDLED_HTTP_ERROR_MESSAGE } from './explorers-error-handler';
import { HttpRequestError } from './http-request-error';
import { SourceLogger } from './logger.service';

jest.mock('@sentry/angular', () => ({
  ...jest.requireActual('@sentry/angular'),
  createErrorHandler: () => mockSentryErrorHandler,
}));

const mockSentryErrorHandler = { handleError: jest.fn() };

const NOMINATIONS_URL =
  'https://agora.adknowledgeportal.org/api/v1/genes/ENSG00000130203/nominations';

function unhandledRequestError(status: HttpStatusCode): HttpRequestError {
  return new HttpRequestError(new HttpErrorResponse({ status, url: NOMINATIONS_URL }), 'GET');
}

describe('ExplorersErrorHandler', () => {
  let errorHandler: ExplorersErrorHandler;
  let errorOverlayService: ErrorOverlayService;
  let loggerErrorSpy: jest.SpyInstance;
  let loggerWarnSpy: jest.SpyInstance;

  beforeEach(() => {
    mockSentryErrorHandler.handleError.mockClear();
    loggerErrorSpy = jest.spyOn(SourceLogger.prototype, 'error').mockImplementation();
    loggerWarnSpy = jest.spyOn(SourceLogger.prototype, 'warn').mockImplementation();
    TestBed.configureTestingModule({ providers: [ExplorersErrorHandler] });
    errorHandler = TestBed.inject(ExplorersErrorHandler);
    errorOverlayService = TestBed.inject(ErrorOverlayService);
  });

  afterEach(() => jest.restoreAllMocks());

  describe('an HTTP failure no call site handled', () => {
    const serviceUnavailableError = unhandledRequestError(HttpStatusCode.ServiceUnavailable);

    it('shows the error overlay with its message for users', () => {
      errorHandler.handleError(serviceUnavailableError);

      expect(errorOverlayService.activeMessage()).toBe(serviceUnavailableError.userMessage);
    });

    it('is reported as an error under its endpoint and status', () => {
      errorHandler.handleError(serviceUnavailableError);

      expect(loggerErrorSpy).toHaveBeenCalledWith(
        `${UNHANDLED_HTTP_ERROR_MESSAGE} (${serviceUnavailableError.message})`,
        expect.objectContaining({ error: serviceUnavailableError }),
      );
      expect(mockSentryErrorHandler.handleError).not.toHaveBeenCalled();
    });

    it('is reported as a warning when it is a 500 the API already reported', () => {
      const internalServerError = unhandledRequestError(HttpStatusCode.InternalServerError);

      errorHandler.handleError(internalServerError);

      expect(loggerWarnSpy).toHaveBeenCalledWith(
        `${UNHANDLED_HTTP_ERROR_MESSAGE} (${internalServerError.message})`,
        expect.objectContaining({ error: internalServerError }),
      );
      expect(loggerErrorSpy).not.toHaveBeenCalled();
      expect(mockSentryErrorHandler.handleError).not.toHaveBeenCalled();
    });
  });

  describe('any other error', () => {
    const unexpectedError = new TypeError("Cannot read properties of undefined (reading 'name')");

    it('does not show the error overlay', () => {
      errorHandler.handleError(unexpectedError);

      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it("is reported through Sentry's error handler", () => {
      errorHandler.handleError(unexpectedError);

      expect(mockSentryErrorHandler.handleError).toHaveBeenCalledWith(unexpectedError);
    });
  });
});
