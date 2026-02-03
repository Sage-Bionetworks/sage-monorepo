import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { ResourceCardsComponent } from './resource-cards.component';

const meta: Meta<ResourceCardsComponent> = {
  component: ResourceCardsComponent,
  title: 'UI/Cards/ResourceCardsComponent',
  decorators: [
    applicationConfig({
      providers: [provideRouter([]), provideHttpClient(withInterceptorsFromDi())],
    }),
  ],
};
export default meta;
type Story = StoryObj<ResourceCardsComponent>;

const cards = [
  {
    imagePath: '/model-ad-assets/images/gene-expression.svg',
    description: 'View Gene Expression results for this model in the comparison tool.',
    title: 'Gene Expression',
    link: '/comparison/expression?models=APOE4',
  },
  {
    imagePath: '/model-ad-assets/images/disease-correlation.svg',
    description: 'View Disease Correlation results for this model in the comparison tool.',
    title: 'Disease Correlation',
    link: '/comparison/correlation?models=APOE4',
  },
  {
    imagePath: '/model-ad-assets/images/allen-institute-logo.svg',
    description: "View Gene Expression results for this model on the Allen Institute's site.",
    link: 'https://alleninstitute.org/division/brain-science/',
  },
];

export const Demo: Story = {
  args: {
    cards,
  },
};
