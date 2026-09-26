import { inject } from '@angular/core';
import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse,
} from '@angular/common/http';
import * as Sentry from '@sentry/angular';
import { Observable, throwError, timer } from 'rxjs';
import { retry, catchError } from 'rxjs/operators';
import {
  ErrorOverlayService,
  LoggerService,
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
 * - Re-throws errors so callers can recover (fallback values, redirects)
 */
export const httpErrorInterceptor: HttpInterceptorFn = (
  req: HttpRequest<any>,
  next: HttpHandlerFn,
): Observable<any> => {
  const logger = inject(LoggerService).forSource('httpErrorInterceptor');
  const errorOverlayService = inject(ErrorOverlayService);

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
      const errorMessage = buildErrorMessage(error);
      const urlPath = extractUrlPath(error.url);

      if (!req.context.get(SKIP_ERROR_REPORTING)) {
        // The message groups Sentry issues, so it carries only the endpoint and status; tags make
        // those values searchable across issues.
        Sentry.withScope((scope) => {
          scope.setTag('http.method', req.method);
          scope.setTag('http.status_code', String(error.status));
          scope.setTag('http.url', urlPath);

          logger.error(`HTTP ${error.status} ${req.method} ${urlPath}`, {
            error,
            data: { errorMessage, url: error.url, statusText: error.statusText },
          });
        });
      }

      // Show error overlay so users know when requests fail, unless the request
      // explicitly opts out (e.g., non-critical requests like version checks).
      const suppressOverlay = req.context.get(SUPPRESS_ERROR_OVERLAY);
      if (!suppressOverlay) {
        errorOverlayService.showError(errorMessage);
      }

      // Re-throw - components can catch for cleanup but don't need to show errors
      return throwError(() => new Error(errorMessage));
    }),
  );
};

/**
 * Extracts the URL path from a full URL, stripping the host and query params.
 * e.g. "https://api.example.com/v1/genes?id=123" → "/v1/genes"
 */
function extractUrlPath(url: string | null): string {
  if (!url) return 'unknown';
  try {
    return new URL(url).pathname;
  } catch {
    return url.split('?')[0];
  }
}

/**
 * Builds a user-friendly error message from an HttpErrorResponse.
 */
function buildErrorMessage(error: HttpErrorResponse): string {
  if (error.error instanceof ErrorEvent) {
    // Client-side error (network issue, etc.)
    return error.error.message || 'A network error occurred. Please check your connection.';
  }

  // Server-side error
  switch (error.status) {
    case 0:
      return 'Unable to connect to the server. Please check your connection.';
    case 404:
      return 'The requested resource was not found.';
    case 500:
      return 'An internal server error occurred. Please try again later.';
    case 502:
      return 'The server encountered a temporary error. Please try again later.';
    case 503:
      return 'The server is temporarily unavailable. Please try again later.';
    case 504:
      return 'The server is taking too long to respond. Please try again later.';
    default:
      return `An error occurred (Error ${error.status}). Please try again.`;
  }
}
