import { inject, provideAppInitializer } from '@angular/core';
import { ErrorOverlayService } from '@sagebionetworks/explorers/services';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { ErrorOverlayComponent } from './error-overlay.component';

const meta: Meta<ErrorOverlayComponent> = {
  component: ErrorOverlayComponent,
  title: 'UI/ErrorOverlay',
};
export default meta;
type Story = StoryObj<ErrorOverlayComponent>;

const showOverlay = (show: (errorOverlayService: ErrorOverlayService) => void) =>
  applicationConfig({
    providers: [provideAppInitializer(() => show(inject(ErrorOverlayService)))],
  });

export const HttpError: Story = {
  decorators: [
    showOverlay((errorOverlayService) =>
      errorOverlayService.showError('An internal server error occurred. Please try again later.'),
    ),
  ],
};

export const AppUpdate: Story = {
  decorators: [
    showOverlay((errorOverlayService) => errorOverlayService.showUpdateAvailable('Model AD')),
  ],
};
