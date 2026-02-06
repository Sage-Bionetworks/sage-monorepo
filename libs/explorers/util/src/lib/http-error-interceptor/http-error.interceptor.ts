import { inject } from '@angular/core';
import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError, timer } from 'rxjs';
import { retry, catchError } from 'rxjs/operators';
import { AppError } from '@sagebionetworks/explorers/models';
import { LoggerService } from '@sagebionetworks/explorers/services';

/**
 * HTTP interceptor that handles errors from HTTP requests.
 *
 * This interceptor:
 * - Retries failed requests once for transient errors (network issues, 5xx)
 * - Does NOT retry client errors (4xx) as they won't succeed
 * - Logs errors for debugging
 * - Re-throws errors as AppError for consistent error handling
 *
 * Error notifications are NOT shown here. Instead:
 * - Components can catch errors and throw AppError with isUserFacingError=true
 *   and a custom message, which GlobalErrorHandler will display
 * - If no component catches the error, it bubbles up as AppError with
 *   isUserFacingError=false (no toast shown)
 */
export const httpErrorInterceptor: HttpInterceptorFn = (
  req: HttpRequest<any>,
  next: HttpHandlerFn,
): Observable<any> => {
  const logger = inject(LoggerService);

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

      // Log error for debugging
      logger.error(`HTTP Error: ${errorMessage}`, error);

      // Re-throw as AppError with isUserFacingError=false
      // Components can catch and throw their own AppError with custom message and isUserFacingError=true
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
    case 400:
      return 'Invalid request. Please check your input and try again.';
    case 401:
      return 'You are not authorized. Please log in and try again.';
    case 403:
      return 'You do not have permission to perform this action.';
    case 404:
      return 'The requested resource was not found.';
    case 500:
      return 'An internal server error occurred. Please try again later.';
    case 502:
    case 503:
    case 504:
      return 'The server is temporarily unavailable. Please try again later.';
    default:
      return `An error occurred (Error ${error.status}). Please try again.`;
  }
}
