import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import {
  AppUpdateCheckTriggerEnum,
  AppUpdateService,
  ErrorOverlayService,
  formatHttpErrorReportMessage,
  HttpErrorReportLevelEnum,
  HttpRequestError,
  LoggerService,
  reportHttpError,
  SourceLogger,
  UNREPORTED_HTTP_ERROR_MESSAGE,
} from '@sagebionetworks/explorers/services';
import { config, firstValueFrom } from 'rxjs';
import { httpErrorInterceptor, RETRY_DELAY_MS } from './http-error.interceptor';

const DATA_VERSION_URL = '/v1/dataversion';
const NOT_FOUND = { status: 404, statusText: 'Not Found' };
const SERVICE_UNAVAILABLE = { status: 503, statusText: 'Service Unavailable' };

describe('httpErrorInterceptor', () => {
  let http: HttpClient;
  let httpTestingController: HttpTestingController;
  let appUpdateService: {
    start: jest.Mock;
    requestCheck: jest.Mock;
    isAppStale: jest.Mock;
  };

  beforeEach(() => {
    appUpdateService = {
      start: jest.fn(),
      requestCheck: jest.fn(),
      isAppStale: jest.fn(() => Promise.resolve(false)),
    };

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([httpErrorInterceptor])),
        provideHttpClientTesting(),
        { provide: AppUpdateService, useValue: appUpdateService },
      ],
    });

    http = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
    jest.restoreAllMocks();
  });

  /** Sends a request and returns a getter for the error it fails with, once it has failed. */
  function request(params?: Record<string, string>): () => unknown {
    let thrown: unknown;
    http.get(DATA_VERSION_URL, { params }).subscribe({ error: (error) => (thrown = error) });
    return () => thrown;
  }

  function requestAndFail(): unknown {
    const thrown = request();
    httpTestingController.expectOne(DATA_VERSION_URL).flush(null, NOT_FOUND);
    return thrown();
  }

  it('should not show the overlay, since only the caller knows whether to', () => {
    requestAndFail();

    expect(TestBed.inject(ErrorOverlayService).isOverlayVisible()).toBe(false);
  });

  describe('unreported failures', () => {
    const CALLER_OPERATION = 'Error loading data version';

    let loggerWarn: jest.SpyInstance;
    let loggerError: jest.SpyInstance;

    function reportAsCaller(error: unknown) {
      const logger = TestBed.inject(LoggerService).forSource('caller');
      reportHttpError(logger, CALLER_OPERATION, { error, level: HttpErrorReportLevelEnum.Error });
    }

    function failPendingRequest() {
      httpTestingController.expectOne(DATA_VERSION_URL).flush(null, NOT_FOUND);
    }

    function unreportedWarnings() {
      return loggerWarn.mock.calls.filter(([message]) =>
        String(message).startsWith(UNREPORTED_HTTP_ERROR_MESSAGE),
      );
    }

    beforeEach(() => {
      jest.useFakeTimers();
      loggerWarn = jest.spyOn(SourceLogger.prototype, 'warn').mockImplementation();
      loggerError = jest.spyOn(SourceLogger.prototype, 'error').mockImplementation();
    });

    afterEach(() => {
      config.onUnhandledError = null;
      jest.useRealTimers();
    });

    it('should not report a failure while its caller may still report it', () => {
      requestAndFail();

      expect(loggerWarn).not.toHaveBeenCalled();
      expect(loggerError).not.toHaveBeenCalled();
    });

    it('should warn on the next tick about a failure its caller caught without reporting', async () => {
      const thrown = requestAndFail();

      await jest.runOnlyPendingTimersAsync();

      expect(loggerWarn).toHaveBeenCalledWith(
        formatHttpErrorReportMessage(UNREPORTED_HTTP_ERROR_MESSAGE, thrown as HttpRequestError),
        expect.any(Object),
        thrown,
      );
    });

    it('should stay quiet when the caller reports the failure in its error handler', async () => {
      http.get(DATA_VERSION_URL).subscribe({ error: reportAsCaller });
      failPendingRequest();

      await jest.runOnlyPendingTimersAsync();

      expect(unreportedWarnings()).toHaveLength(0);
      expect(loggerError).toHaveBeenCalledTimes(1);
    });

    it('should stay quiet when an awaiting caller reports the failure in its catch', async () => {
      const awaitingCaller = firstValueFrom(http.get(DATA_VERSION_URL)).catch(reportAsCaller);
      failPendingRequest();

      await awaitingCaller;
      await jest.runOnlyPendingTimersAsync();

      expect(unreportedWarnings()).toHaveLength(0);
    });

    it('should stay quiet when the failure is unhandled and the ErrorHandler reports it', async () => {
      // RxJS hands an error no subscriber handles to onUnhandledError, or throws it to the
      // ErrorHandler when unset, from a timer it queues while the error is delivered.
      config.onUnhandledError = reportAsCaller;
      http.get(DATA_VERSION_URL).subscribe();
      failPendingRequest();

      await jest.runOnlyPendingTimersAsync();

      expect(unreportedWarnings()).toHaveLength(0);
      expect(loggerError).toHaveBeenCalledTimes(1);
    });
  });

  it('should re-throw a typed error that keeps the status, method, and path', () => {
    const thrown = requestAndFail();

    expect(thrown).toBeInstanceOf(HttpRequestError);
    expect(thrown).toMatchObject({
      status: NOT_FOUND.status,
      method: 'GET',
      urlPath: DATA_VERSION_URL,
    });
  });

  it('should drop the query string from the path of the re-thrown error', () => {
    const thrown = request({ id: 'ENSG00000130203' });
    httpTestingController
      .expectOne((pendingRequest) => pendingRequest.url === DATA_VERSION_URL)
      .flush(null, NOT_FOUND);

    expect(thrown()).toMatchObject({ urlPath: DATA_VERSION_URL });
  });

  it('should ask for an app version check, since a stale tab may cause the error', () => {
    requestAndFail();

    expect(appUpdateService.requestCheck).toHaveBeenCalledTimes(1);
    expect(appUpdateService.requestCheck).toHaveBeenCalledWith(AppUpdateCheckTriggerEnum.HttpError);
  });

  describe('retry', () => {
    beforeEach(() => {
      jest.useFakeTimers();
    });

    afterEach(() => {
      jest.useRealTimers();
    });

    it('should retry a server error once before re-throwing it', () => {
      const thrown = request();

      httpTestingController.expectOne(DATA_VERSION_URL).flush(null, SERVICE_UNAVAILABLE);
      expect(thrown()).toBeUndefined();

      jest.advanceTimersByTime(RETRY_DELAY_MS);
      httpTestingController.expectOne(DATA_VERSION_URL).flush(null, SERVICE_UNAVAILABLE);

      expect(thrown()).toMatchObject({ status: SERVICE_UNAVAILABLE.status });
    });

    it('should re-throw a client error immediately without retrying', () => {
      const thrown = requestAndFail();
      expect(thrown).toMatchObject({ status: NOT_FOUND.status });

      jest.advanceTimersByTime(RETRY_DELAY_MS);

      httpTestingController.expectNone(DATA_VERSION_URL);
    });
  });
});
