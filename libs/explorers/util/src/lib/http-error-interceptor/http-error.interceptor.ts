import { inject } from '@angular/core';
import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError, timer } from 'rxjs';
import { retry, catchError } from 'rxjs/operators';
import {
  AppUpdateCheckTriggerEnum,
  AppUpdateService,
  HttpRequestError,
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
 * caller handles.
 */
export const httpErrorInterceptor: HttpInterceptorFn = (
  req: HttpRequest<any>,
  next: HttpHandlerFn,
): Observable<any> => {
  const appUpdateService = inject(AppUpdateService);

  return next(req).pipe(
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
    catchError((error: HttpErrorResponse) => {
      const httpRequestError = new HttpRequestError(error, req.method);

      appUpdateService.requestCheck(AppUpdateCheckTriggerEnum.HttpError);

      return throwError(() => httpRequestError);
    }),
  );
};
