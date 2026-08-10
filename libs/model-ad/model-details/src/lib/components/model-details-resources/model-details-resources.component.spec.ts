import { ResourceCardData } from '@sagebionetworks/explorers/models';
import { ResourceCardsComponent } from '@sagebionetworks/explorers/ui';
import { render, screen } from '@testing-library/angular';
import { ModelDetailsResourcesComponent } from './model-details-resources.component';

const modelSpecificCards: ResourceCardData[] = [
  {
    imagePath: 'model-ad-assets/images/jax-logo.svg',
    description: 'View detailed information about this AD model on JAX.',
    link: 'https://www.jax.org/strain/12345',
  },
];

const additionalCards: ResourceCardData[] = [
  {
    imagePath: 'model-ad-assets/images/agora-logo.svg',
    description: 'View evidence about the role of human genes in AD.',
    link: 'https://agora.adknowledgeportal.org/',
  },
];

async function setup(
  componentInputs: {
    modelSpecificResourceCards?: ResourceCardData[];
    additionalResourceCards?: ResourceCardData[];
  } = {},
) {
  return render(ModelDetailsResourcesComponent, {
    imports: [ResourceCardsComponent],
    componentInputs,
  });
}

describe('ModelDetailsResourcesComponent', () => {
  it('should display both sections when both card lists are provided', async () => {
    await setup({
      modelSpecificResourceCards: modelSpecificCards,
      additionalResourceCards: additionalCards,
    });

    expect(screen.getByText('Model-Specific Resources')).toBeInTheDocument();
    expect(screen.getByText(/jax/i)).toBeInTheDocument();

    expect(screen.getByText('Additional Resources')).toBeInTheDocument();
    expect(screen.getByText(/human genes in ad/i)).toBeInTheDocument();
  });

  it('should not display the model-specific section when no model-specific cards are provided', async () => {
    await setup({ additionalResourceCards: additionalCards });

    expect(screen.queryByText('Model-Specific Resources')).not.toBeInTheDocument();
    expect(screen.getByText('Additional Resources')).toBeInTheDocument();
  });

  it('should not display the additional section when no additional cards are provided', async () => {
    await setup({ modelSpecificResourceCards: modelSpecificCards });

    expect(screen.queryByText('Additional Resources')).not.toBeInTheDocument();
    expect(screen.getByText('Model-Specific Resources')).toBeInTheDocument();
  });

  it('should not display any section when no cards are provided', async () => {
    await setup();

    expect(screen.queryByText('Model-Specific Resources')).not.toBeInTheDocument();
    expect(screen.queryByText('Additional Resources')).not.toBeInTheDocument();
  });
});
