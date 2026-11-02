import * as Sentry from '@sentry/node';
import type { ErrorRequestHandler } from 'express';

const MIN_CLIENT_ERROR_STATUS = 400;
const MIN_SERVER_ERROR_STATUS = 500;
const MAX_ERROR_STATUS = 599;
const UNHANDLED_MECHANISM = { type: 'express', handled: false };

/**
 * Express error middleware that reports server errors to Sentry, marked unhandled since no route
 * recovered from them, then passes the error on so the response is unchanged. Register it after
 * every route.
 */
export function sentryExpressErrorHandler(): ErrorRequestHandler {
  return (error, _request, _response, next) => {
    if (resolveResponseStatus(error) >= MIN_SERVER_ERROR_STATUS) {
      Sentry.captureException(error, { mechanism: UNHANDLED_MECHANISM });
    }
    next(error);
  };
}

/** The status Express's default error handler responds with for this error. */
function resolveResponseStatus(error: unknown): number {
  if (typeof error === 'object' && error !== null) {
    const { status, statusCode } = error as { status?: unknown; statusCode?: unknown };
    const declaredStatus = typeof status === 'number' ? status : statusCode;
    if (
      typeof declaredStatus === 'number' &&
      declaredStatus >= MIN_CLIENT_ERROR_STATUS &&
      declaredStatus <= MAX_ERROR_STATUS
    ) {
      return declaredStatus;
    }
  }
  return MIN_SERVER_ERROR_STATUS;
}
