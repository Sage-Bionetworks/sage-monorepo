import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { ResourceCardData } from '@sagebionetworks/explorers/models';
import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular';
import { ModelDetailsResourcesComponent } from './model-details-resources.component';

const meta: Meta<ModelDetailsResourcesComponent> = {
  component: ModelDetailsResourcesComponent,
  title: 'Model Details/ModelDetailsResourcesComponent',
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
type Story = StoryObj<ModelDetailsResourcesComponent>;

const modelSpecificResourceCards: ResourceCardData[] = [
  {
    imagePath: 'explorers-assets/images/ad-knowledge-portal-logo.svg',
    description:
      "Explore all of the data and metadata that's available for this model in the AD Knowledge Portal.",
    link: 'https://adknowledgeportal.synapse.org/',
  },
  {
    imagePath: 'model-ad-assets/images/jax-logo.svg',
    description: 'View detailed information about this AD model on JAX.',
    link: 'https://www.jax.org/strain/030922',
  },
];

const additionalResourceCards: ResourceCardData[] = [
  {
    imagePath: 'model-ad-assets/images/agora-logo.svg',
    description: 'View evidence about the role of human genes in AD.',
    link: 'https://agora.adknowledgeportal.org/',
  },
  {
    imagePath: 'model-ad-assets/images/model-ad-logo.svg',
    description: 'Learn about the MODEL-AD program.',
    link: 'https://www.model-ad.org/',
  },
];

export const Default: Story = {
  args: {
    modelSpecificResourceCards,
    additionalResourceCards,
  },
};

export const AdditionalResourcesOnly: Story = {
  args: {
    additionalResourceCards,
  },
};
