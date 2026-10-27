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
  ErrorOverlayService,
  HttpErrorReportLevelEnum,
  HttpRequestError,
  LoggerService,
  reportHttpError,
  SKIP_ERROR_REPORTING,
  SUPPRESS_ERROR_OVERLAY,
} from '@sagebionetworks/explorers/services';

export const RETRY_DELAY_MS = 1000;

/**
 * HTTP interceptor that handles errors from HTTP requests.
 *
 * This interceptor:
 * - Retries failed requests once for transient errors (network issues, 5xx)
 * - Does NOT retry client errors (4xx) as they won't succeed
 * - Shows the error overlay, unless the request sets SUPPRESS_ERROR_OVERLAY
 * - Reports errors to Sentry, unless the request sets SKIP_ERROR_REPORTING
 *   because the caller reports its own failures
 * - Asks for an app version check, since a tab from an earlier deploy may be calling an API that
 *   has since changed. If the mismatch persists through the confirmation delay, the update
 *   notice replaces any error overlay.
 * - Re-throws an `HttpRequestError`, so callers can tell expected failures from unexpected ones and
 *   recover (fallback values, redirects)
 */
export const httpErrorInterceptor: HttpInterceptorFn = (
  req: HttpRequest<any>,
  next: HttpHandlerFn,
): Observable<any> => {
  const logger = inject(LoggerService).forSource('httpErrorInterceptor');
  const errorOverlayService = inject(ErrorOverlayService);
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

      if (!req.context.get(SKIP_ERROR_REPORTING)) {
        reportHttpError(logger, 'HTTP request failed', {
          error: httpRequestError,
          level: HttpErrorReportLevelEnum.Error,
        });
      }

      // Show error overlay so users know when requests fail, unless the request
      // explicitly opts out (e.g., non-critical requests like version checks).
      const suppressOverlay = req.context.get(SUPPRESS_ERROR_OVERLAY);
      if (!suppressOverlay) {
        errorOverlayService.showError(httpRequestError.userMessage);
      }

      // Re-throw - components can catch for cleanup but don't need to show errors
      return throwError(() => httpRequestError);
    }),
  );
};
