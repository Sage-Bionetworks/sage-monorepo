import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { NotFoundComponent } from './not-found.component';

const meta: Meta<NotFoundComponent> = {
  component: NotFoundComponent,
  title: 'Pages/NotFound',
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
type Story = StoryObj<NotFoundComponent>;

export const NotFound: Story = {
  args: {
    supportEmail: 'agora@sagebionetworks.org',
    backgroundImagePath: 'agora-assets/images/hero-background.svg',
  },
};
