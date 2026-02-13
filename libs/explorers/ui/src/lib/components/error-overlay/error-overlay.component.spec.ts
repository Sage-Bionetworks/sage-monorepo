import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { signal } from '@angular/core';
import { ErrorOverlayService } from '@sagebionetworks/explorers/services';
import { ErrorOverlayComponent } from './error-overlay.component';

function createMockErrorOverlayService(hasError = false, errorMessage = '') {
  const errorMessageSignal = signal<string | null>(hasError ? errorMessage : null);
  return {
    hasActiveError: signal(hasError),
    activeErrorMessage: signal(errorMessage || 'Something went wrong.'),
    showError: jest.fn((message: string) => {
      errorMessageSignal.set(message);
    }),
    reloadPage: jest.fn(),
  };
}

async function setup(hasError = false, errorMessage = '') {
  const mockService = createMockErrorOverlayService(hasError, errorMessage);
  const renderResult = await render(ErrorOverlayComponent, {
    providers: [{ provide: ErrorOverlayService, useValue: mockService }],
  });
  return { ...renderResult, mockService };
}

describe('ErrorOverlayComponent', () => {
  it('should not display anything when hasActiveError is false', async () => {
    await setup(false);
    expect(screen.queryByText('Reload Page')).not.toBeInTheDocument();
  });

  it('should display overlay and banner when hasActiveError is true', async () => {
    const { container } = await setup(true, 'Test error');
    expect(container.querySelector('.error-overlay')).toBeInTheDocument();
    expect(container.querySelector('.warning-banner')).toBeInTheDocument();
  });

  it('should display the error message', async () => {
    const errorMessage = 'Something went wrong';
    await setup(true, errorMessage);
    expect(screen.getByText(errorMessage)).toBeInTheDocument();
  });

  it('should call reloadPage when button is clicked', async () => {
    const user = userEvent.setup();
    const { mockService } = await setup(true, 'Error');

    const reloadButton = screen.getByRole('button', { name: 'Reload Page' });
    await user.click(reloadButton);

    expect(mockService.reloadPage).toHaveBeenCalled();
  });

  it('should not trigger any action when overlay background is clicked', async () => {
    const user = userEvent.setup();
    const { container, mockService } = await setup(true, 'Error');

    const overlay = container.querySelector('.error-overlay') as HTMLElement;
    await user.click(overlay);

    expect(mockService.reloadPage).not.toHaveBeenCalled();
  });
});
