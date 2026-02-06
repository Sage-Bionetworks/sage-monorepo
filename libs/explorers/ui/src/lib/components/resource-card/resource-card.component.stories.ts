import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { ResourceCardComponent } from './resource-card.component';

const meta: Meta<ResourceCardComponent> = {
  component: ResourceCardComponent,
  title: 'UI/Cards/ResourceCardComponent',
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
type Story = StoryObj<ResourceCardComponent>;

export const InternalLinkHomeCard: Story = {
  args: {
    title: 'Gene Expression',
    description: 'View Gene Expression results for this model in the comparison tool.',
    imagePath: '/model-ad-assets/images/gene-expression.svg',
    altText: 'gene expression icon',
    link: '/comparison/expression?models=APOE4',
  },
};

export const ExternalLinkHomeCard: Story = {
  args: {
    description: "View Gene Expression results for this model on the Allen Institute's site.",
    imagePath: '/model-ad-assets/images/allen-institute-logo.svg',
    altText: 'allen institute logo',
    link: 'https://alleninstitute.org/division/brain-science',
  },
};
