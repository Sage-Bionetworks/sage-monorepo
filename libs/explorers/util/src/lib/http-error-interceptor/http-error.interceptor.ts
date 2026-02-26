import { inject } from '@angular/core';
import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { Router } from '@angular/router';
import * as Sentry from '@sentry/angular';
import { Observable, throwError, timer } from 'rxjs';
import { retry, catchError } from 'rxjs/operators';
import { SUPPRESS_ERROR_OVERLAY } from '@sagebionetworks/explorers/constants';
import { ErrorOverlayService, LoggerService } from '@sagebionetworks/explorers/services';

/**
 * HTTP interceptor that handles errors from HTTP requests.
 *
 * This interceptor:
 * - Retries failed requests once for transient errors (network issues, 5xx)
 * - Does NOT retry client errors (4xx) as they won't succeed
 * - Shows error overlay for all errors so users are informed when requests fail
 * - Logs all errors for debugging
 * - Re-throws errors for consistent error handling
 *
 * Errors are handled centrally here to provide a consistent user experience.
 * Components should catch errors for cleanup but don't need to show their own
 * error messages.
 */
export const httpErrorInterceptor: HttpInterceptorFn = (
  req: HttpRequest<any>,
  next: HttpHandlerFn,
): Observable<any> => {
  const logger = inject(LoggerService);
  const errorOverlayService = inject(ErrorOverlayService);
  const router = inject(Router);

  return next(req).pipe(
    retry({
      count: 1,
      delay: (error: HttpErrorResponse) => {
        // Only retry on network errors or server errors (5xx)
        if (error.status === 0 || (error.status >= 500 && error.status < 600)) {
          return timer(1000); // Wait 1 second before retry
        }
        // Don't retry client errors (4xx) - they won't succeed
        throw error;
      },
    }),
    catchError((error: HttpErrorResponse) => {
      const errorMessage = buildErrorMessage(error);
      const urlPath = extractUrlPath(error.url);

      // Log error with Sentry context for proper grouping by endpoint + status
      Sentry.withScope((scope) => {
        scope.setFingerprint(['http-error', String(error.status), urlPath]);
        scope.setTag('http.method', req.method);
        scope.setTag('http.status_code', String(error.status));
        scope.setTag('http.url', urlPath);
        scope.setExtra('errorResponse', {
          url: error.url,
          status: error.status,
          statusText: error.statusText,
        });

        // grouping by status + method + urlPath
        const sentryError = new Error(`HTTP ${error.status} ${req.method} ${urlPath}`);
        logger.error(`HTTP Error: ${errorMessage}`, sentryError);
      });

      // Show error overlay for all errors so users know when requests fail,
      // but not if the user is already on the error page or if the request
      // explicitly opts out (e.g., non-critical requests like version checks).
      // TODO - in the future, we may want to track specific routes through configuration (MG-771)
      const suppressOverlay = req.context.get(SUPPRESS_ERROR_OVERLAY);
      const isOnErrorPage = router.url.includes('/not-found');
      if (!suppressOverlay && !isOnErrorPage) {
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
    return url;
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
