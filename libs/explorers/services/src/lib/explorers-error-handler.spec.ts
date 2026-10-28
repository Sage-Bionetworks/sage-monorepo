import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ErrorOverlayService } from './error-overlay.service';
import { ExplorersErrorHandler } from './explorers-error-handler';
import { HttpRequestError } from './http-request-error';

jest.mock('@sentry/angular', () => ({
  createErrorHandler: () => mockSentryErrorHandler,
}));

const mockSentryErrorHandler = { handleError: jest.fn() };

describe('ExplorersErrorHandler', () => {
  let errorHandler: ExplorersErrorHandler;
  let errorOverlayService: ErrorOverlayService;

  beforeEach(() => {
    mockSentryErrorHandler.handleError.mockClear();
    TestBed.configureTestingModule({ providers: [ExplorersErrorHandler] });
    errorHandler = TestBed.inject(ExplorersErrorHandler);
    errorOverlayService = TestBed.inject(ErrorOverlayService);
  });

  describe('an HTTP failure no call site handled', () => {
    const unhandledRequestError = new HttpRequestError(
      new HttpErrorResponse({
        status: HttpStatusCode.ServiceUnavailable,
        url: 'https://agora.adknowledgeportal.org/api/v1/genes/ENSG00000130203/nominations',
      }),
      'GET',
    );

    it('shows the error overlay with its message for users', () => {
      errorHandler.handleError(unhandledRequestError);

      expect(errorOverlayService.activeMessage()).toBe(unhandledRequestError.userMessage);
    });

    it('is reported to Sentry', () => {
      errorHandler.handleError(unhandledRequestError);

      expect(mockSentryErrorHandler.handleError).toHaveBeenCalledWith(unhandledRequestError);
    });
  });

  describe('any other error', () => {
    const unexpectedError = new TypeError("Cannot read properties of undefined (reading 'name')");

    it('does not show the error overlay', () => {
      errorHandler.handleError(unexpectedError);

      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it('is reported to Sentry', () => {
      errorHandler.handleError(unexpectedError);

      expect(mockSentryErrorHandler.handleError).toHaveBeenCalledWith(unexpectedError);
    });
  });
});
