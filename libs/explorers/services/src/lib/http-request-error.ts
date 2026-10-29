import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import * as Sentry from '@sentry/angular';
import { SourceLogger } from './logger.service';

export const HttpErrorReportLevelEnum = {
  Warn: 'warn',
  Error: 'error',
} as const;
export type HttpErrorReportLevel =
  (typeof HttpErrorReportLevelEnum)[keyof typeof HttpErrorReportLevelEnum];

/** The status Angular reports when a request gets no response at all, also used for a timeout. */
export const NO_RESPONSE_STATUS = 0;

export const UNKNOWN_URL_PATH = 'unknown';

export const UNEXPECTED_ERROR_USER_MESSAGE = 'An unexpected error occurred. Please try again.';

export const HttpSentryTagEnum = {
  Method: 'http.method',
  StatusCode: 'http.status_code',
  Url: 'http.url',
} as const;

export interface HttpErrorReportContext {
  error: unknown;
  level: HttpErrorReportLevel;
  /** Searchable context, such as which comparison tool view the request was made for. */
  tags?: Record<string, string>;
}

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

/** A message suitable for showing to users for any caught error, e.g. in the error overlay. */
export function getUserMessage(error: unknown): string {
  return error instanceof HttpRequestError ? error.userMessage : UNEXPECTED_ERROR_USER_MESSAGE;
}

/** The message `reportHttpError` reports a failed request under. */
export function formatHttpErrorReportMessage(operation: string, error: HttpRequestError): string {
  return `${operation} (HTTP ${error.status} ${error.method})`;
}

/**
 * Reports a failed request through the caller's logger as `<operation> (HTTP <status> <method>)`,
 * with `http.*` tags, so Sentry groups each call site's failures by operation, method, and status.
 * The path is only a tag: it can hold item IDs, and in the message it would split one outage into
 * an issue per item. `operation` names what the caller was doing and must stay fixed, since it is
 * part of the grouping. `level` is the call site's judgment of how serious the failure is, except that a
 * 500 is reported as a warning at most: the API reports the exception behind its own 500, with the
 * stack trace, on the same trace as this event.
 *
 * Anything other than an `HttpRequestError`, such as an error thrown while mapping a response, is
 * a bug rather than a failed request, so it is reported as an error under `operation` alone,
 * whatever `level` says.
 */
export function reportHttpError(
  logger: SourceLogger,
  operation: string,
  { error, level, tags = {} }: HttpErrorReportContext,
): void {
  Sentry.withScope((scope) => {
    scope.setTags(tags);

    if (!(error instanceof HttpRequestError)) {
      logger.error(operation, { error });
      return;
    }

    scope.setTag(HttpSentryTagEnum.Method, error.method);
    scope.setTag(HttpSentryTagEnum.StatusCode, String(error.status));
    scope.setTag(HttpSentryTagEnum.Url, error.urlPath);

    const message = formatHttpErrorReportMessage(operation, error);
    const data = {
      userMessage: error.userMessage,
      url: error.cause.url,
      statusText: error.cause.statusText,
    };
    const reportLevel =
      error.status === HttpStatusCode.InternalServerError ? HttpErrorReportLevelEnum.Warn : level;
    if (reportLevel === HttpErrorReportLevelEnum.Warn) {
      logger.warn(message, { ...data, error });
    } else {
      logger.error(message, { error, data });
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
