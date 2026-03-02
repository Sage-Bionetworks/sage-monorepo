import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { ErrorPageComponent } from './error-page.component';

const meta: Meta<ErrorPageComponent> = {
  component: ErrorPageComponent,
  title: 'Pages/ErrorPage',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<ErrorPageComponent>;

export const ErrorPage: Story = {
  args: {
    supportEmail: 'agora@sagebionetworks.org',
    backgroundImagePath: 'agora-assets/images/hero-background.svg',
  },
};
