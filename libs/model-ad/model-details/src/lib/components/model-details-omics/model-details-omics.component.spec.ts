import { ResourceCardsComponent } from '@sagebionetworks/explorers/ui';
import { render, screen } from '@testing-library/angular';
import { ModelDetailsOmicsComponent } from './model-details-omics.component';

async function setup() {
  return render(ModelDetailsOmicsComponent, {
    imports: [ResourceCardsComponent],
    componentInputs: {
      modelName: 'TEST123',
    },
  });
}

describe('ModelDetailsOmicsComponent', () => {
  it('should display all resource cards', async () => {
    await setup();

    const sectionTitle = screen.getByText('Available Data');
    expect(sectionTitle).toBeInTheDocument();

    expect(screen.getByText('Gene Expression')).toBeInTheDocument();
    expect(screen.getByText('Disease Correlation')).toBeInTheDocument();
  });
});
