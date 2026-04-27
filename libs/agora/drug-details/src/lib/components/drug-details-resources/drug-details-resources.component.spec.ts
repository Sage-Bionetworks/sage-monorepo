import { drugMock } from '@sagebionetworks/agora/testing';
import { ResourceCardsComponent } from '@sagebionetworks/explorers/ui';
import { render, screen } from '@testing-library/angular';
import { DrugDetailsResourcesComponent } from './drug-details-resources.component';

async function setup(drug = drugMock) {
  return render(DrugDetailsResourcesComponent, {
    imports: [ResourceCardsComponent],
    componentInputs: {
      drug: drug,
    },
  });
}

describe('DrugDetailsResourcesComponent', () => {
  it('should display related resource cards', async () => {
    await setup();

    const sectionTitle = screen.getByText('Related Resources');
    expect(sectionTitle).toBeInTheDocument();

    expect(screen.getByText(/alzgps/i)).toBeInTheDocument();
    expect(screen.getByText(/ad therapeutics/i)).toBeInTheDocument();
    expect(screen.getByText(/fda/i)).toBeInTheDocument();
    expect(screen.getByText(/open targets/i)).toBeInTheDocument();
    expect(screen.getByText(/druggable genome/i)).toBeInTheDocument();
    expect(screen.getByText(/pubmed/i)).toBeInTheDocument();
    expect(screen.getByText(/taca/i)).toBeInTheDocument();
  });
});
