import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import * as Sentry from '@sentry/angular';
import { NO_RESPONSE_STATUS } from './app-update.service';
import { SourceLogger } from './logger.service';

export type HttpErrorReportLevel = 'warn' | 'error';

export const UNKNOWN_URL_PATH = 'unknown';

export const NON_HTTP_ERROR_MESSAGE = 'Non-HTTP error in an HTTP request pipeline';

/**
 * A failed HTTP request, as re-thrown by `httpErrorInterceptor`. Its message names the status,
 * method, and URL path, and the original `HttpErrorResponse` is its `cause`.
 */
export class HttpRequestError extends Error {
  override readonly name = 'HttpRequestError';
  override readonly cause: HttpErrorResponse;
  readonly status: number;
  readonly method: string;
  readonly urlPath: string;
  /** A message suitable for showing to users, e.g. in the error overlay. */
  readonly userMessage: string;

  constructor(response: HttpErrorResponse, method: string) {
    const urlPath = extractUrlPath(response.url);
    super(`HTTP ${response.status} ${method} ${urlPath}`, { cause: response });
    this.cause = response;
    this.status = response.status;
    this.method = method;
    this.urlPath = urlPath;
    this.userMessage = buildUserMessage(response);
  }
}

export function isNotFoundError(error: unknown): boolean {
  return error instanceof HttpRequestError && error.status === HttpStatusCode.NotFound;
}

/**
 * Reports a failed request through the caller's logger, under the `HTTP <status> <method> <path>`
 * message and with `http.*` tags, so Sentry groups each call site's failures by endpoint and
 * status. `level` is the call site's judgment of how serious the failure is, except that a 500 is
 * reported as a warning at most: the API reports the exception behind its own 500, with the stack
 * trace, on the same trace as this event.
 *
 * Anything other than an `HttpRequestError`, such as an error thrown while mapping a response, is
 * a bug rather than a failed request, so it is reported as an error whatever `level` says.
 */
export function reportHttpError(
  logger: SourceLogger,
  error: unknown,
  level: HttpErrorReportLevel,
): void {
  if (!(error instanceof HttpRequestError)) {
    logger.error(NON_HTTP_ERROR_MESSAGE, { error });
    return;
  }

  const reportLevel = error.status === HttpStatusCode.InternalServerError ? 'warn' : level;
  const data = {
    userMessage: error.userMessage,
    url: error.cause.url,
    statusText: error.cause.statusText,
  };

  Sentry.withScope((scope) => {
    scope.setTag('http.method', error.method);
    scope.setTag('http.status_code', String(error.status));
    scope.setTag('http.url', error.urlPath);

    if (reportLevel === 'warn') {
      logger.warn(error.message, { ...data, error });
    } else {
      logger.error(error.message, { error, data });
    }
  });
}

/**
 * Extracts the URL path from a full URL, stripping the host and query params.
 * e.g. "https://api.example.com/v1/genes?id=123" → "/v1/genes"
 */
function extractUrlPath(url: string | null): string {
  if (!url) return UNKNOWN_URL_PATH;
  try {
    return new URL(url).pathname;
  } catch {
    return url.split('?')[0];
  }
}

function buildUserMessage(response: HttpErrorResponse): string {
  if (response.error instanceof ErrorEvent) {
    // Client-side error (network issue, etc.)
    return response.error.message || 'A network error occurred. Please check your connection.';
  }

  switch (response.status) {
    case NO_RESPONSE_STATUS:
      return 'Unable to connect to the server. Please check your connection.';
    case HttpStatusCode.NotFound:
      return 'The requested resource was not found.';
    case HttpStatusCode.InternalServerError:
      return 'An internal server error occurred. Please try again later.';
    case HttpStatusCode.BadGateway:
      return 'The server encountered a temporary error. Please try again later.';
    case HttpStatusCode.ServiceUnavailable:
      return 'The server is temporarily unavailable. Please try again later.';
    case HttpStatusCode.GatewayTimeout:
      return 'The server is taking too long to respond. Please try again later.';
    default:
      return `An error occurred (Error ${response.status}). Please try again.`;
  }
}
