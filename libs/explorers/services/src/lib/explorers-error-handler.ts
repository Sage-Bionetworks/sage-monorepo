import { ErrorHandler, inject, Injectable } from '@angular/core';
import * as Sentry from '@sentry/angular';
import { ErrorOverlayService } from './error-overlay.service';
import { HttpRequestError } from './http-request-error';

/**
 * The explorer apps' global `ErrorHandler`, the safety net for errors no call site handles. An
 * `HttpRequestError` that reaches it was not handled by the code that sent the request, so the
 * page may be missing content: it shows the error overlay to tell users. Every error, HTTP or not,
 * then goes to Sentry's handler, which reports it at `error` level.
 */
@Injectable()
export class ExplorersErrorHandler implements ErrorHandler {
  private readonly errorOverlayService = inject(ErrorOverlayService);
  private readonly sentryErrorHandler = Sentry.createErrorHandler();

  handleError(error: unknown): void {
    if (error instanceof HttpRequestError) {
      this.errorOverlayService.showRequestError(error);
    }
    this.sentryErrorHandler.handleError(error);
  }
}
