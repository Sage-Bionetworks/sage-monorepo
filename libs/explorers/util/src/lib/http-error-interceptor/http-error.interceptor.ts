import { inject } from '@angular/core';
import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, timer } from 'rxjs';
import { retry } from 'rxjs/operators';
import {
  AppUpdateCheckTriggerEnum,
  AppUpdateService,
  HttpErrorReportLevelEnum,
  HttpRequestError,
  LoggerService,
  reportHttpError,
  UNREPORTED_HTTP_ERROR_MESSAGE,
  wasReported,
} from '@sagebionetworks/explorers/services';

export const RETRY_DELAY_MS = 1000;

/**
 * HTTP interceptor that handles errors from HTTP requests.
 *
 * This interceptor:
 * - Retries failed requests once for transient errors (network issues, 5xx)
 * - Does NOT retry client errors (4xx) as they won't succeed
 * - Asks for an app version check, since a tab from an earlier deploy may be calling an API that
 *   has since changed. If the mismatch persists through the confirmation delay, the update
 *   notice replaces any error overlay.
 * - Re-throws an `HttpRequestError`, so callers can tell expected failures from unexpected ones and
 *   recover (fallback values, redirects)
 *
 * It neither reports to Sentry nor shows the error overlay: only the caller knows whether a failure
 * is expected, so callers report through `reportHttpError` and show the overlay through
 * `ErrorOverlayService.showRequestError`. The global `ErrorHandler` does both for any failure no
 * caller handles, and a failure a caller catches but never reports is sent as an
 * `UNREPORTED_HTTP_ERROR_MESSAGE` warning.
 */
export const httpErrorInterceptor: HttpInterceptorFn = (
  req: HttpRequest<any>,
  next: HttpHandlerFn,
): Observable<any> => {
  const appUpdateService = inject(AppUpdateService);
  const logger = inject(LoggerService).forSource('httpErrorInterceptor');

  const retried = next(req).pipe(
    retry({
      count: 1,
      delay: (error: HttpErrorResponse) => {
        // Only retry on network errors or server errors (5xx)
        if (error.status === 0 || (error.status >= 500 && error.status < 600)) {
          return timer(RETRY_DELAY_MS);
        }
        // Don't retry client errors (4xx) - they won't succeed
        throw error;
      },
    }),
  );

  return new Observable((subscriber) =>
    retried.subscribe({
      next: (event) => subscriber.next(event),
      complete: () => subscriber.complete(),
      error: (error: HttpErrorResponse) => {
        const httpRequestError = new HttpRequestError(error, req.method);
        appUpdateService.requestCheck(AppUpdateCheckTriggerEnum.HttpError);

        subscriber.error(httpRequestError);

        // Checked a tick after the error is delivered, by which point a caller's catchError (run
        // while delivering), an awaiting try/catch (a microtask), and RxJS's hand-off of an
        // unhandled error to the ErrorHandler (a timer queued while delivering) have all reported.
        setTimeout(() => {
          if (!wasReported(httpRequestError)) {
            reportHttpError(logger, UNREPORTED_HTTP_ERROR_MESSAGE, {
              error: httpRequestError,
              level: HttpErrorReportLevelEnum.Warn,
            });
          }
        });
      },
    }),
  );
};
