import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { applicationConfig } from '@storybook/angular';
import type { Meta, StoryObj } from '@storybook/angular';
import { LinkBarComponent } from './link-bar.component';

interface LinkBarStoryArgs {
  content: string;
  link: string;
  altText?: string;
}

const meta: Meta<LinkBarStoryArgs> = {
  component: LinkBarComponent,
  title: 'UI/LinkBarComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
      ],
    }),
  ],
  render: (args) => ({
    props: args,
    template: `
      <explorers-link-bar [link]="link" [altText]="altText">${args.content}</explorers-link-bar>
    `,
    moduleMetadata: { imports: [LinkBarComponent] },
  }),
};
export default meta;
type Story = StoryObj<LinkBarStoryArgs>;

export const Default: Story = {
  args: {
    content: 'View all QTL studies',
    link: '/studies',
  },
};

export const WithBoldPrefix: Story = {
  args: {
    content: '<b>Studies:</b>&nbsp;Browse all',
    link: '/studies',
  },
};

export const WithAltText: Story = {
  args: {
    content: 'View all QTL studies',
    link: '/studies',
    altText: 'Navigate to the QTL studies page',
  },
};
