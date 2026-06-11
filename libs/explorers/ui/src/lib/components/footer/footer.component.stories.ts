import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { FooterComponent } from './footer.component';

const meta: Meta<FooterComponent> = {
  component: FooterComponent,
  title: 'UI/Footer',
  decorators: [
    applicationConfig({
      providers: [provideRouter([]), provideLocationMocks()],
    }),
  ],
};
export default meta;
type Story = StoryObj<FooterComponent>;

const links = [
  { label: 'About', routerLink: ['/about'] },
  { label: 'Help', url: 'https://help.example.org', target: '_blank' as const },
  { label: 'News', routerLink: ['/news'] },
  { label: 'Terms of Service', routerLink: ['/terms-of-service'] },
];

export const Footer: Story = {
  args: {
    footerLogoPath: 'agora-assets/images/footer-logo.svg',
    footerLinks: links,
    siteVersion: '2.0.0',
    dataVersion: 'syn13363290-v17',
  },
};
