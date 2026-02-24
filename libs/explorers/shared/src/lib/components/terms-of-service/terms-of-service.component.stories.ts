import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { provideMarkdown } from 'ngx-markdown';
import { TermsOfServiceComponent } from './terms-of-service.component';

const meta: Meta<TermsOfServiceComponent> = {
  component: TermsOfServiceComponent,
  title: 'Pages/TermsOfService',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideLoadingIconColors(),
        provideMarkdown(),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<TermsOfServiceComponent>;

export const TermsOfService: Story = {
  args: {
    heroBackgroundImagePath: 'agora-assets/images/hero-background.svg',
  },
};
