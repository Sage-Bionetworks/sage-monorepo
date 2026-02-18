import { inject } from '@angular/core';
import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError, timer } from 'rxjs';
import { retry, catchError } from 'rxjs/operators';
import { AppError } from '@sagebionetworks/explorers/models';
import { ErrorOverlayService, LoggerService } from '@sagebionetworks/explorers/services';

/**
 * HTTP interceptor that handles errors from HTTP requests.
 *
 * This interceptor:
 * - Retries failed requests once for transient errors (network issues, 5xx)
 * - Does NOT retry client errors (4xx) as they won't succeed
 * - Shows error overlay for all errors so users are informed when requests fail
 * - Logs all errors for debugging
 * - Re-throws errors as AppError for consistent error handling
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

      // Log error for debugging (also sends to Sentry)
      logger.error(`HTTP Error: ${errorMessage}`, error);
      logger.trackError(error);

      // Show error overlay for all errors so users know when requests fail,
      // but not if the user is already on the error page.
      // TODO - in the future, we may want to track specific routes through configuration (MG-771)
      const isOnErrorPage = router.url.includes('/not-found');
      if (!isOnErrorPage) {
        errorOverlayService.showError(errorMessage);
      }

      // Re-throw as AppError - components can catch for cleanup but don't need to show errors
      return throwError(() => new AppError(errorMessage, false));
    }),
  );
};

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
