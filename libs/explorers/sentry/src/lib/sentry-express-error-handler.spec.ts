import * as Sentry from '@sentry/node';
import type { NextFunction, Request, Response } from 'express';
import { sentryExpressErrorHandler } from './sentry-express-error-handler';

jest.mock('@sentry/node', () => ({
  captureException: jest.fn(),
}));

const request = {} as Request;
const response = {} as Response;

function withStatus(message: string, status: number): Error {
  return Object.assign(new Error(message), { status });
}

describe('sentryExpressErrorHandler', () => {
  const handler = sentryExpressErrorHandler();
  let next: jest.MockedFunction<NextFunction>;

  beforeEach(() => {
    next = jest.fn();
    (Sentry.captureException as jest.Mock).mockClear();
  });

  it('reports an error without a status, which Express answers with a 500', () => {
    const error = new Error('unknown method selected: bogus');

    handler(error, request, response, next);

    expect(Sentry.captureException).toHaveBeenCalledWith(error, expect.anything());
  });

  it('reports an error that declares a server error status', () => {
    const error = withStatus('Service Unavailable', 503);

    handler(error, request, response, next);

    expect(Sentry.captureException).toHaveBeenCalledWith(error, expect.anything());
  });

  it('reports an error whose statusCode declares a server error status', () => {
    const error = Object.assign(new Error('Bad Gateway'), { statusCode: 502 });

    handler(error, request, response, next);

    expect(Sentry.captureException).toHaveBeenCalledWith(error, expect.anything());
  });

  it('reports an error whose status is not an error status, which Express answers with a 500', () => {
    const error = withStatus('OK', 200);

    handler(error, request, response, next);

    expect(Sentry.captureException).toHaveBeenCalledWith(error, expect.anything());
  });

  it('reports a thrown non-Error value, which Express answers with a 500', () => {
    const thrownValue = 'unknown method selected: bogus';

    handler(thrownValue, request, response, next);

    expect(Sentry.captureException).toHaveBeenCalledWith(thrownValue, expect.anything());
  });

  it('marks a reported error as unhandled', () => {
    handler(new Error('database unavailable'), request, response, next);

    expect(Sentry.captureException).toHaveBeenCalledWith(
      expect.anything(),
      expect.objectContaining({ mechanism: expect.objectContaining({ handled: false }) }),
    );
  });

  it('does not report an error that declares a client error status', () => {
    handler(withStatus('Not Found', 404), request, response, next);

    expect(Sentry.captureException).not.toHaveBeenCalled();
  });

  it('passes the error on so the response is unchanged', () => {
    const clientError = withStatus('Not Found', 404);
    const serverError = new Error('database unavailable');

    handler(clientError, request, response, next);
    handler(serverError, request, response, next);

    expect(next).toHaveBeenNthCalledWith(1, clientError);
    expect(next).toHaveBeenNthCalledWith(2, serverError);
  });
});
