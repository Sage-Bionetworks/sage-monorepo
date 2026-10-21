import { createEvent, fireEvent, render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { signal } from '@angular/core';
import { ErrorOverlayService } from '@sagebionetworks/explorers/services';
import { ErrorOverlayComponent } from './error-overlay.component';

const errorMessage = 'An internal server error occurred. Please try again later.';

function createMockErrorOverlayService(hasError = false, message = '') {
  const activeMessage = signal<string | null>(hasError ? message : null);
  return {
    activeMessage,
    isOverlayVisible: signal(hasError),
    showError: jest.fn((newMessage: string) => activeMessage.set(newMessage)),
    showAppUpdateAvailable: jest.fn(),
    showDataUpdateAvailable: jest.fn(),
    reloadPage: jest.fn(),
  };
}

async function setup(hasError = false, message = '') {
  const mockService = createMockErrorOverlayService(hasError, message);
  const renderResult = await render(ErrorOverlayComponent, {
    providers: [{ provide: ErrorOverlayService, useValue: mockService }],
  });
  return { ...renderResult, mockService };
}

describe('ErrorOverlayComponent', () => {
  let showModal: jest.Mock;

  beforeEach(() => {
    // jsdom does not implement modal dialogs, so this opens the dialog the way the browser would
    showModal = jest.fn(function (this: HTMLDialogElement) {
      this.setAttribute('open', '');
    });
    HTMLDialogElement.prototype.showModal = showModal;
  });

  afterEach(() => {
    delete (HTMLDialogElement.prototype as Partial<HTMLDialogElement>).showModal;
  });

  it('should not display anything when the overlay is not visible', async () => {
    await setup(false);

    expect(screen.queryByRole('alertdialog')).not.toBeInTheDocument();
  });

  it('should open as a modal dialog named by the message', async () => {
    await setup(true, errorMessage);

    expect(screen.getByRole('alertdialog', { name: errorMessage })).toBeInTheDocument();
    expect(showModal).toHaveBeenCalledTimes(1);
  });

  it('should call reloadPage when button is clicked', async () => {
    const user = userEvent.setup();
    const { mockService } = await setup(true, errorMessage);

    await user.click(screen.getByRole('button', { name: 'Reload Page' }));

    expect(mockService.reloadPage).toHaveBeenCalled();
  });

  it('should not reload when the area around the message is clicked', async () => {
    const user = userEvent.setup();
    const { mockService } = await setup(true, errorMessage);

    await user.click(screen.getByRole('alertdialog'));

    expect(mockService.reloadPage).not.toHaveBeenCalled();
  });

  it.each([
    ['the Escape key', (dialog: HTMLElement) => createEvent.keyDown(dialog, { key: 'Escape' })],
    ['a close request', () => new Event('cancel', { cancelable: true })],
  ])('should stay open on %s', async (_description, createCloseEvent) => {
    await setup(true, errorMessage);
    const dialog = screen.getByRole('alertdialog');
    const closeEvent = createCloseEvent(dialog);

    fireEvent(dialog, closeEvent);

    expect(closeEvent.defaultPrevented).toBe(true);
  });
});
