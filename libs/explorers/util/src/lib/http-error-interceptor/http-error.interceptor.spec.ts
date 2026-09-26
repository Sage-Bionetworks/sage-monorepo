import {
  HttpClient,
  HttpContext,
  HttpErrorResponse,
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import {
  ErrorOverlayService,
  LoggerService,
  SKIP_ERROR_REPORTING,
  SUPPRESS_ERROR_OVERLAY,
} from '@sagebionetworks/explorers/services';
import { httpErrorInterceptor, RETRY_DELAY_MS } from './http-error.interceptor';

const DATA_VERSION_URL = '/v1/dataversion';
const NOT_FOUND = { status: 404, statusText: 'Not Found' };
const SERVICE_UNAVAILABLE = { status: 503, statusText: 'Service Unavailable' };

describe('httpErrorInterceptor', () => {
  let http: HttpClient;
  let httpTestingController: HttpTestingController;
  let logger: { error: jest.Mock };
  let errorOverlayService: { showError: jest.Mock };

  beforeEach(() => {
    logger = { error: jest.fn() };
    errorOverlayService = { showError: jest.fn() };

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([httpErrorInterceptor])),
        provideHttpClientTesting(),
        { provide: LoggerService, useValue: { forSource: () => logger } },
        { provide: ErrorOverlayService, useValue: errorOverlayService },
      ],
    });

    http = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  function requestAndFail(context?: HttpContext): void {
    http.get(DATA_VERSION_URL, { context }).subscribe({ error: () => undefined });
    httpTestingController.expectOne(DATA_VERSION_URL).flush(null, NOT_FOUND);
  }

  it('should report the error and show the overlay by default', () => {
    requestAndFail();

    expect(logger.error).toHaveBeenCalledTimes(1);
    expect(errorOverlayService.showError).toHaveBeenCalledTimes(1);
  });

  it('should report the error under a message naming the status, method, and path', () => {
    requestAndFail();

    expect(logger.error).toHaveBeenCalledWith(`HTTP ${NOT_FOUND.status} GET ${DATA_VERSION_URL}`, {
      error: expect.any(HttpErrorResponse),
      data: expect.objectContaining({
        errorMessage: 'The requested resource was not found.',
        statusText: NOT_FOUND.statusText,
      }),
    });
  });

  it('should drop the query string from the reported path', () => {
    http.get(DATA_VERSION_URL, { params: { id: 'ENSG00000130203' } }).subscribe({
      error: () => undefined,
    });
    httpTestingController
      .expectOne((request) => request.url === DATA_VERSION_URL)
      .flush(null, NOT_FOUND);

    expect(logger.error).toHaveBeenCalledWith(
      `HTTP ${NOT_FOUND.status} GET ${DATA_VERSION_URL}`,
      expect.anything(),
    );
  });

  it('should not report the error when the caller owns error reporting', () => {
    requestAndFail(new HttpContext().set(SKIP_ERROR_REPORTING, true));

    expect(logger.error).not.toHaveBeenCalled();
  });

  it('should not show the overlay when the request suppresses it', () => {
    requestAndFail(new HttpContext().set(SUPPRESS_ERROR_OVERLAY, true));

    expect(errorOverlayService.showError).not.toHaveBeenCalled();
  });

  describe('retry', () => {
    beforeEach(() => {
      jest.useFakeTimers();
    });

    afterEach(() => {
      jest.useRealTimers();
    });

    it('should retry a server error once before reporting it', () => {
      http.get(DATA_VERSION_URL).subscribe({ error: () => undefined });

      httpTestingController.expectOne(DATA_VERSION_URL).flush(null, SERVICE_UNAVAILABLE);
      expect(logger.error).not.toHaveBeenCalled();

      jest.advanceTimersByTime(RETRY_DELAY_MS);
      httpTestingController.expectOne(DATA_VERSION_URL).flush(null, SERVICE_UNAVAILABLE);

      expect(logger.error).toHaveBeenCalledTimes(1);
    });

    it('should report a client error immediately without retrying', () => {
      requestAndFail();
      expect(logger.error).toHaveBeenCalledTimes(1);

      jest.advanceTimersByTime(RETRY_DELAY_MS);

      httpTestingController.expectNone(DATA_VERSION_URL);
    });
  });
});
