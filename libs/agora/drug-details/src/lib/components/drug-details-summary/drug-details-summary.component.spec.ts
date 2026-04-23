import { Drug } from '@sagebionetworks/agora/api-client';
import { drugMock } from '@sagebionetworks/agora/testing';
import { render, screen } from '@testing-library/angular';
import { DrugDetailsSummaryComponent } from './drug-details-summary.component';

async function setup(drugOverrides: Partial<Drug> = {}) {
  return render(DrugDetailsSummaryComponent, {
    componentInputs: {
      drug: { ...drugMock, ...drugOverrides },
    },
  });
}

describe('DrugDetailsSummaryComponent', () => {
  it('should display modality', async () => {
    await setup();
    expect(screen.getByText('Modality')).toBeInTheDocument();
    expect(screen.getByText(drugMock.modality)).toBeInTheDocument();
  });

  it('should display max clinical trial phase', async () => {
    await setup();
    expect(screen.getByText('Max Clinical Trial Phase')).toBeInTheDocument();
    expect(screen.getByText(drugMock.maximum_clinical_trial_phase as string)).toBeInTheDocument();
  });

  it('should display year of first approval', async () => {
    await setup();
    expect(screen.getByText('Year of first approval')).toBeInTheDocument();
    expect(screen.getByText(String(drugMock.year_of_first_approval))).toBeInTheDocument();
  });

  describe('mechanisms of action', () => {
    it('should display mechanisms when present', async () => {
      await setup();
      expect(screen.getByText('Mechanisms of Action')).toBeInTheDocument();
      expect(screen.getByText('Cytochrome P450 19A1 inhibitor')).toBeInTheDocument();
    });

    it('should not display mechanisms section when empty', async () => {
      await setup({ mechanisms_of_action: [] });
      expect(screen.queryByText('Mechanisms of Action')).not.toBeInTheDocument();
    });
  });

  describe('linked targets', () => {
    it('should display linked targets when present', async () => {
      await setup();
      expect(screen.getByText('Linked Targets')).toBeInTheDocument();
      const link = screen.getByRole('link', { name: 'CYP19A1' });
      expect(link).toBeInTheDocument();
      expect(link).toHaveAttribute('href', 'genes/ENSG00000137869');
    });

    it('should fall back to ensembl_gene_id when hgnc_symbol is missing', async () => {
      await setup({
        linked_targets: [{ ensembl_gene_id: 'ENSG00000000001', hgnc_symbol: '' }],
      });
      const link = screen.getByRole('link', { name: 'ENSG00000000001' });
      expect(link).toBeInTheDocument();
      expect(link).toHaveAttribute('href', 'genes/ENSG00000000001');
    });

    it('should not display linked targets section when empty', async () => {
      await setup({ linked_targets: [] });
      expect(screen.queryByText('Linked Targets')).not.toBeInTheDocument();
    });
  });
});
