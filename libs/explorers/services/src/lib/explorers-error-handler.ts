import { ErrorHandler, inject, Injectable } from '@angular/core';
import * as Sentry from '@sentry/angular';
import { ErrorOverlayService } from './error-overlay.service';
import { HttpErrorReportLevelEnum, HttpRequestError, reportHttpError } from './http-request-error';
import { LoggerService } from './logger.service';

export const UNHANDLED_HTTP_ERROR_MESSAGE = 'Unhandled HTTP request failure';

/**
 * The explorer apps' global `ErrorHandler`, the safety net for errors no call site handles. An
 * `HttpRequestError` that reaches it was not handled by the code that sent the request, so the
 * page may be missing content: it shows the error overlay to tell users, and reports through
 * `reportHttpError`, like the call sites, so a 500 the API already reported stays a warning here.
 * Every other error goes to Sentry's handler, which reports it at `error` level.
 */
@Injectable()
export class ExplorersErrorHandler implements ErrorHandler {
  private readonly errorOverlayService = inject(ErrorOverlayService);
  private readonly logger = inject(LoggerService).forSource('ExplorersErrorHandler');
  private readonly sentryErrorHandler = Sentry.createErrorHandler();

  handleError(error: unknown): void {
    if (!(error instanceof HttpRequestError)) {
      this.sentryErrorHandler.handleError(error);
      return;
    }
    this.errorOverlayService.showRequestError(error);
    reportHttpError(this.logger, UNHANDLED_HTTP_ERROR_MESSAGE, {
      error,
      level: HttpErrorReportLevelEnum.Error,
    });
  }
}
