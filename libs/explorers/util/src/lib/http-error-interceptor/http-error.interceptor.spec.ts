import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import {
  AppUpdateCheckTriggerEnum,
  AppUpdateService,
  ErrorOverlayService,
  HttpRequestError,
  SourceLogger,
} from '@sagebionetworks/explorers/services';
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

  it('should not report the error, since only the caller knows whether it was expected', () => {
    const loggerWarn = jest.spyOn(SourceLogger.prototype, 'warn');
    const loggerError = jest.spyOn(SourceLogger.prototype, 'error');

    requestAndFail();

    expect(loggerWarn).not.toHaveBeenCalled();
    expect(loggerError).not.toHaveBeenCalled();
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
