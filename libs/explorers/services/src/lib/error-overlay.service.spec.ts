import { TestBed } from '@angular/core/testing';
import { getAppUpdateMessage } from '@sagebionetworks/explorers/constants';
import { ErrorOverlayService } from './error-overlay.service';

const siteName = 'Model AD';
const firstErrorMessage = 'The requested resource was not found.';
const secondErrorMessage = 'An internal server error occurred. Please try again later.';

describe('ErrorOverlayService', () => {
  let service: ErrorOverlayService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ErrorOverlayService);
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
    service.showUpdateAvailable(siteName);

    expect(service.isOverlayVisible()).toBe(true);
    expect(service.activeMessage()).toBe(getAppUpdateMessage(siteName));
  });

  it('replaces an active error with the app update notice', () => {
    service.showError(firstErrorMessage);
    service.showUpdateAvailable(siteName);

    expect(service.activeMessage()).toBe(getAppUpdateMessage(siteName));
  });

  it('keeps the app update notice when an error follows', () => {
    service.showUpdateAvailable(siteName);
    service.showError(firstErrorMessage);

    expect(service.activeMessage()).toBe(getAppUpdateMessage(siteName));
  });
});
