import { ResourceCardsComponent } from '@sagebionetworks/explorers/ui';
import { modelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import { ModelDetailsResourcesComponent } from './model-details-resources.component';

async function setup(model = modelMock) {
  return render(ModelDetailsResourcesComponent, {
    imports: [ResourceCardsComponent],
    componentInputs: {
      model: model,
    },
  });
}

describe('ModelDetailsResourcesComponent', () => {
  it('should display all model-specific resource cards', async () => {
    await setup();

    const sectionTitle = screen.getByText('Model-Specific Resources');
    expect(sectionTitle).toBeInTheDocument();

    expect(screen.getByText(/ad knowledge portal/i)).toBeInTheDocument();
    expect(screen.getByText(/alzforum/i)).toBeInTheDocument();
    expect(screen.getByText(/jax/i)).toBeInTheDocument();
  });

  it('should display all additional resource cards', async () => {
    await setup();

    const sectionTitle = screen.getByText('Additional Resources');
    expect(sectionTitle).toBeInTheDocument();

    expect(screen.getByText(/human genes in ad/i)).toBeInTheDocument();
    expect(screen.getByText(/allen brain atlas/i)).toBeInTheDocument();
    expect(screen.getByText(/model-ad program/i)).toBeInTheDocument();
    expect(screen.getByText(/mouse genome informatics/i)).toBeInTheDocument();
    expect(screen.getByText(/model-ad preclinical testing core/i)).toBeInTheDocument();
  });

  it('should not display alzforum card when alzforum_id is missing', async () => {
    await setup({ ...modelMock, alzforum_id: '' });
    expect(screen.queryByText(/alzforum/i)).not.toBeInTheDocument();
  });
});
