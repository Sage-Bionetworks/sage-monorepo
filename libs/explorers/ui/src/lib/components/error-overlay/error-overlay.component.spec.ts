import { render, screen, fireEvent } from '@testing-library/angular';
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
  return await render(ErrorOverlayComponent, {
    providers: [{ provide: ErrorOverlayService, useValue: mockService }],
  });
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
    const mockService = createMockErrorOverlayService(true, 'Error');
    await render(ErrorOverlayComponent, {
      providers: [{ provide: ErrorOverlayService, useValue: mockService }],
    });

    const reloadButton = screen.getByRole('button', { name: 'Reload Page' });
    fireEvent.click(reloadButton);

    expect(mockService.reloadPage).toHaveBeenCalled();
  });

  it('should not trigger any action when overlay background is clicked', async () => {
    const mockService = createMockErrorOverlayService(true, 'Error');
    const { container } = await render(ErrorOverlayComponent, {
      providers: [{ provide: ErrorOverlayService, useValue: mockService }],
    });

    const overlay = container.querySelector('.error-overlay') as HTMLElement;
    fireEvent.click(overlay);

    expect(mockService.reloadPage).not.toHaveBeenCalled();
  });
});
