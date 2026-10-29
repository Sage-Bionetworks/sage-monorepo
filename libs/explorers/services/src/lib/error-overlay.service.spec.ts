import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { getAppUpdateMessage, getDataUpdateMessage } from '@sagebionetworks/explorers/constants';
import { ErrorOverlayService } from './error-overlay.service';
import {
  formatHttpErrorReportMessage,
  HttpErrorReportLevelEnum,
  HttpRequestError,
  UNEXPECTED_ERROR_USER_MESSAGE,
} from './http-request-error';
import { SourceLogger } from './logger.service';

const siteName = 'Model AD';
const firstErrorMessage = 'The requested resource was not found.';
const secondErrorMessage = 'An internal server error occurred. Please try again later.';

describe('ErrorOverlayService', () => {
  let service: ErrorOverlayService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ErrorOverlayService);
  });

  it("shows a failed request's message for users", () => {
    const error = new HttpRequestError(
      new HttpErrorResponse({
        status: HttpStatusCode.ServiceUnavailable,
        url: 'https://modeladexplorer.org/api/v1/models/mouse/LOAD1',
      }),
      'GET',
    );

    service.showRequestError(error);

    expect(service.activeMessage()).toBe(error.userMessage);
  });

  it('reports a failed request and shows its message for users', () => {
    const operation = 'Error loading model';
    const logger = { warn: jest.fn(), error: jest.fn() };
    const error = new HttpRequestError(
      new HttpErrorResponse({
        status: HttpStatusCode.ServiceUnavailable,
        url: 'https://modeladexplorer.org/api/v1/models/mouse/LOAD1',
      }),
      'GET',
    );

    service.reportAndShow(logger as unknown as SourceLogger, operation, {
      error,
      level: HttpErrorReportLevelEnum.Error,
    });

    expect(logger.error).toHaveBeenCalledWith(
      formatHttpErrorReportMessage(operation, error),
      expect.objectContaining({ error }),
    );
    expect(service.activeMessage()).toBe(error.userMessage);
  });

  it('shows a generic message for anything other than a failed request', () => {
    service.showRequestError(new TypeError("Cannot read properties of undefined (reading 'name')"));

    expect(service.activeMessage()).toBe(UNEXPECTED_ERROR_USER_MESSAGE);
  });

  it('is hidden until something is shown', () => {
    expect(service.isOverlayVisible()).toBe(false);
    expect(service.activeMessage()).toBeNull();
  });

  it('shows an error message', () => {
    service.showError(firstErrorMessage);

    expect(service.isOverlayVisible()).toBe(true);
    expect(service.activeMessage()).toBe(firstErrorMessage);
  });

  it('keeps the first error when another error follows', () => {
    service.showError(firstErrorMessage);
    service.showError(secondErrorMessage);

    expect(service.activeMessage()).toBe(firstErrorMessage);
  });

  it('shows the app update notice for the site', () => {
    service.showAppUpdateAvailable(siteName);

    expect(service.isOverlayVisible()).toBe(true);
    expect(service.activeMessage()).toBe(getAppUpdateMessage(siteName));
  });

  it('replaces an active error with the app update notice', () => {
    service.showError(firstErrorMessage);
    service.showAppUpdateAvailable(siteName);

    expect(service.activeMessage()).toBe(getAppUpdateMessage(siteName));
  });

  it('keeps the app update notice when an error follows', () => {
    service.showAppUpdateAvailable(siteName);
    service.showError(firstErrorMessage);

    expect(service.activeMessage()).toBe(getAppUpdateMessage(siteName));
  });

  it('shows the data update notice for the site', () => {
    service.showDataUpdateAvailable(siteName);

    expect(service.isOverlayVisible()).toBe(true);
    expect(service.activeMessage()).toBe(getDataUpdateMessage(siteName));
  });

  it('replaces an active error with the data update notice', () => {
    service.showError(firstErrorMessage);
    service.showDataUpdateAvailable(siteName);

    expect(service.activeMessage()).toBe(getDataUpdateMessage(siteName));
  });

  it('keeps the data update notice when an error follows', () => {
    service.showDataUpdateAvailable(siteName);
    service.showError(firstErrorMessage);

    expect(service.activeMessage()).toBe(getDataUpdateMessage(siteName));
  });

  it('replaces the data update notice with the app update notice', () => {
    service.showDataUpdateAvailable(siteName);
    service.showAppUpdateAvailable(siteName);

    expect(service.activeMessage()).toBe(getAppUpdateMessage(siteName));
  });

  it('keeps the app update notice when a data update follows', () => {
    service.showAppUpdateAvailable(siteName);
    service.showDataUpdateAvailable(siteName);

    expect(service.activeMessage()).toBe(getAppUpdateMessage(siteName));
  });
});
