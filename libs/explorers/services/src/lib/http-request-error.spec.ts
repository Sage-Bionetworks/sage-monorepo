import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import * as Sentry from '@sentry/angular';
import {
  HttpErrorReportLevel,
  HttpErrorReportLevelEnum,
  HttpRequestError,
  getUserMessage,
  HttpSentryTagEnum,
  isNotFoundError,
  NO_RESPONSE_STATUS,
  reportHttpError,
  UNEXPECTED_ERROR_USER_MESSAGE,
  UNKNOWN_URL_PATH,
} from './http-request-error';
import { SourceLogger } from './logger.service';

jest.mock('@sentry/angular', () => ({
  withScope: jest.fn((callback: (scope: unknown) => void) => callback(mockScope)),
}));

const mockScope = { setTag: jest.fn(), setTags: jest.fn() };

const GENES_PATH = '/api/v1/genes/ENSG00000130203';
const GENES_URL = `https://modeladexplorer.org${GENES_PATH}?panel=rna`;
const OTHER_GENE_URL = 'https://modeladexplorer.org/api/v1/genes/ENSG00000095970';

function buildResponse(status: number, url: string | null = GENES_URL, error: unknown = null) {
  return new HttpErrorResponse({ status, statusText: 'Status Text', url: url ?? undefined, error });
}

function buildError(status: number) {
  return new HttpRequestError(buildResponse(status), 'GET');
}

describe('HttpRequestError', () => {
  it('names the status, method, and path without host or query in its message', () => {
    const error = buildError(HttpStatusCode.NotFound);

    expect(error.message).toBe(`HTTP ${HttpStatusCode.NotFound} GET ${GENES_PATH}`);
    expect(error.status).toBe(HttpStatusCode.NotFound);
    expect(error.method).toBe('GET');
    expect(error.urlPath).toBe(GENES_PATH);
  });

  it('keeps the original response as its cause', () => {
    const response = buildResponse(HttpStatusCode.NotFound);

    expect(new HttpRequestError(response, 'GET').cause).toBe(response);
  });

  it('is an Error, so loggers link it as the event cause with its stack', () => {
    const error = buildError(HttpStatusCode.NotFound);

    expect(error).toBeInstanceOf(Error);
    expect(error.name).toBe('HttpRequestError');
  });

  it('strips the query from a relative URL', () => {
    const error = new HttpRequestError(
      buildResponse(HttpStatusCode.NotFound, `${GENES_PATH}?panel=rna`),
      'GET',
    );

    expect(error.urlPath).toBe(GENES_PATH);
  });

  it('reports an unknown path when the response has no URL', () => {
    const error = new HttpRequestError(buildResponse(HttpStatusCode.NotFound, null), 'GET');

    expect(error.urlPath).toBe(UNKNOWN_URL_PATH);
  });

  it.each([
    [NO_RESPONSE_STATUS, 'Unable to connect to the server. Please check your connection.'],
    [HttpStatusCode.NotFound, 'The requested resource was not found.'],
    [
      HttpStatusCode.InternalServerError,
      'An internal server error occurred. Please try again later.',
    ],
    [
      HttpStatusCode.BadGateway,
      'The server encountered a temporary error. Please try again later.',
    ],
    [
      HttpStatusCode.ServiceUnavailable,
      'The server is temporarily unavailable. Please try again later.',
    ],
    [
      HttpStatusCode.GatewayTimeout,
      'The server is taking too long to respond. Please try again later.',
    ],
    [
      HttpStatusCode.Conflict,
      `An error occurred (Error ${HttpStatusCode.Conflict}). Please try again.`,
    ],
  ])('gives users a message for status %s', (status, userMessage) => {
    expect(buildError(status).userMessage).toBe(userMessage);
  });

  it("gives users a network error's own message", () => {
    const networkError = new ErrorEvent('error', { message: 'Failed to fetch' });
    const error = new HttpRequestError(
      buildResponse(NO_RESPONSE_STATUS, GENES_URL, networkError),
      'GET',
    );

    expect(error.userMessage).toBe('Failed to fetch');
  });
});

describe('isNotFoundError', () => {
  it('is true for a failed request answered with a 404', () => {
    expect(isNotFoundError(buildError(HttpStatusCode.NotFound))).toBe(true);
  });

  it('is false for a failed request answered with another status', () => {
    expect(isNotFoundError(buildError(HttpStatusCode.InternalServerError))).toBe(false);
  });

  it('is false for anything other than a failed request', () => {
    expect(isNotFoundError(new Error('not found'))).toBe(false);
  });
});

describe('getUserMessage', () => {
  it("is a failed request's own message for users", () => {
    const error = buildError(HttpStatusCode.ServiceUnavailable);

    expect(getUserMessage(error)).toBe(error.userMessage);
  });

  it('is a generic message for anything other than a failed request', () => {
    expect(getUserMessage(new TypeError('boom'))).toBe(UNEXPECTED_ERROR_USER_MESSAGE);
  });
});

describe('reportHttpError', () => {
  const OPERATION = 'Error fetching gene expression rows';

  let logger: jest.Mocked<Pick<SourceLogger, 'warn' | 'error'>>;

  beforeEach(() => {
    logger = { warn: jest.fn(), error: jest.fn() };
    jest.clearAllMocks();
  });

  function report(error: unknown, level: HttpErrorReportLevel, tags?: Record<string, string>) {
    reportHttpError(logger as unknown as SourceLogger, OPERATION, { error, level, tags });
  }

  it('reports at the call site level under the operation, endpoint, and status', () => {
    const error = buildError(HttpStatusCode.ServiceUnavailable);

    report(error, HttpErrorReportLevelEnum.Error);

    expect(logger.error).toHaveBeenCalledWith(
      `${OPERATION} (HTTP ${HttpStatusCode.ServiceUnavailable} GET)`,
      {
        error,
        data: { userMessage: error.userMessage, url: GENES_URL, statusText: 'Status Text' },
      },
    );
    expect(logger.warn).not.toHaveBeenCalled();
  });

  it('leaves the path out of the message, so failures for different items group together', () => {
    report(buildError(HttpStatusCode.ServiceUnavailable), HttpErrorReportLevelEnum.Error);
    report(
      new HttpRequestError(buildResponse(HttpStatusCode.ServiceUnavailable, OTHER_GENE_URL), 'GET'),
      HttpErrorReportLevelEnum.Error,
    );

    const [firstMessage, secondMessage] = logger.error.mock.calls.map(([message]) => message);
    expect(firstMessage).toBe(secondMessage);
    expect(firstMessage).not.toContain(GENES_PATH);
  });

  it('reports a warning when the call site asks for one', () => {
    const error = buildError(HttpStatusCode.NotFound);

    report(error, HttpErrorReportLevelEnum.Warn);

    expect(logger.warn).toHaveBeenCalledWith(
      `${OPERATION} (HTTP ${HttpStatusCode.NotFound} GET)`,
      expect.objectContaining({ error }),
    );
    expect(logger.error).not.toHaveBeenCalled();
  });

  it('reports a 500 as a warning at most, since the API reports its own exception', () => {
    const error = buildError(HttpStatusCode.InternalServerError);

    report(error, HttpErrorReportLevelEnum.Error);

    expect(logger.warn).toHaveBeenCalledWith(
      `${OPERATION} (HTTP ${HttpStatusCode.InternalServerError} GET)`,
      expect.objectContaining({ error }),
    );
    expect(logger.error).not.toHaveBeenCalled();
  });

  it.each([NO_RESPONSE_STATUS, HttpStatusCode.BadGateway, HttpStatusCode.GatewayTimeout])(
    'reports a %s at the call site level, since the API never saw the failure',
    (status) => {
      report(buildError(status), HttpErrorReportLevelEnum.Error);

      expect(logger.error).toHaveBeenCalledTimes(1);
    },
  );

  it('tags the event with the method, status, and path', () => {
    report(buildError(HttpStatusCode.NotFound), HttpErrorReportLevelEnum.Error);

    expect(Sentry.withScope).toHaveBeenCalledTimes(1);
    expect(mockScope.setTag).toHaveBeenCalledWith(HttpSentryTagEnum.Method, 'GET');
    expect(mockScope.setTag).toHaveBeenCalledWith(
      HttpSentryTagEnum.StatusCode,
      String(HttpStatusCode.NotFound),
    );
    expect(mockScope.setTag).toHaveBeenCalledWith(HttpSentryTagEnum.Url, GENES_PATH);
  });

  it("tags the event with the call site's context", () => {
    const tags = { 'comparison_tool.page': 'Differential Expression' };

    report(buildError(HttpStatusCode.NotFound), HttpErrorReportLevelEnum.Error, tags);

    expect(mockScope.setTags).toHaveBeenCalledWith(tags);
  });

  it('reports anything other than a failed request as an error under the operation alone', () => {
    const error = new TypeError("Cannot read properties of undefined (reading 'items')");
    const tags = { 'comparison_tool.page': 'Differential Expression' };

    report(error, HttpErrorReportLevelEnum.Warn, tags);

    expect(logger.error).toHaveBeenCalledWith(OPERATION, { error });
    expect(logger.warn).not.toHaveBeenCalled();
    expect(mockScope.setTags).toHaveBeenCalledWith(tags);
  });
});
