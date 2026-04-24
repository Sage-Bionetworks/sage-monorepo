import { geneMock1, noHGNCgeneMock } from '@sagebionetworks/agora/testing';
import { ResourceCardsComponent } from '@sagebionetworks/explorers/ui';
import { Gene } from '@sagebionetworks/agora/api-client';
import { render, screen } from '@testing-library/angular';
import { GeneResourcesComponent } from './gene-resources.component';

async function setup(gene: Gene = geneMock1) {
  return render(GeneResourcesComponent, {
    imports: [ResourceCardsComponent],
    componentInputs: { gene },
  });
}

describe('GeneResourcesComponent', () => {
  it('should create', async () => {
    const { fixture } = await setup();
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should not display Target Enabling Resources section when is_tep and is_adi are false', async () => {
    await setup({ ...geneMock1, is_tep: false, is_adi: false });

    expect(screen.queryByText('Target Enabling Resources')).not.toBeInTheDocument();
  });

  it('should display Target Enabling Resources section when is_tep is true', async () => {
    await setup({ ...geneMock1, is_tep: true, is_adi: false });

    expect(screen.getByText('Target Enabling Resources')).toBeInTheDocument();
    expect(screen.getByText(/TREAT-AD resources/i)).toBeInTheDocument();
    expect(screen.getByText(/Target Portfolio and Progress Dashboard/i)).toBeInTheDocument();
    expect(screen.queryByText(/AD Informer Set/i)).not.toBeInTheDocument();
  });

  it('should display AD Informer Set card when is_adi is true', async () => {
    await setup({ ...geneMock1, is_tep: false, is_adi: true });

    expect(screen.getByText('Target Enabling Resources')).toBeInTheDocument();
    expect(screen.getByText(/AD Informer Set/i)).toBeInTheDocument();
    expect(screen.queryByText(/Target Portfolio and Progress Dashboard/i)).not.toBeInTheDocument();
  });

  it('should display Drug Development Resources section', async () => {
    await setup();

    expect(screen.getByText('Drug Development Resources')).toBeInTheDocument();
    expect(screen.getByText(/expert reviews and evaluations/i)).toBeInTheDocument();
    expect(screen.getByText(/genome-scale experiments/i)).toBeInTheDocument();
    expect(screen.getByText(/Druggable Genome/i)).toBeInTheDocument();
  });

  it('should display Additional Resources section', async () => {
    await setup();

    expect(screen.getByText('Additional Resources')).toBeInTheDocument();
    expect(screen.getByText(/network and enrichment analyses/i)).toBeInTheDocument();
    expect(screen.getByText(/news and information resources about AD/i)).toBeInTheDocument();
    expect(screen.getByText(/publications related to this target on PubMed/i)).toBeInTheDocument();
    expect(screen.getByText(/protein sequence and functional information/i)).toBeInTheDocument();
  });

  it('should use hgnc_symbol in Pub AD link when available', async () => {
    await setup(geneMock1);

    const pubAdCard = screen.getByText(/dementia-related publication/i).closest('a');
    expect(pubAdCard).toHaveAttribute(
      'href',
      'https://adexplorer.medicine.iu.edu/pubad/external/MSN',
    );
  });

  it('should use default Pub AD link when hgnc_symbol is not available', async () => {
    await setup(noHGNCgeneMock);

    const pubAdCard = screen.getByText(/dementia-related publication/i).closest('a');
    expect(pubAdCard).toHaveAttribute('href', 'https://adexplorer.medicine.iu.edu/pubad');
  });
});
