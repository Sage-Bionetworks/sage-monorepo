import { Drug } from '@sagebionetworks/agora/api-client';
import { drugMock } from '@sagebionetworks/agora/testing';
import { render, screen } from '@testing-library/angular';
import { DrugDetailsHeroComponent } from './drug-details-hero.component';

async function setup(drugOverrides: Partial<Drug> = {}) {
  return render(DrugDetailsHeroComponent, {
    componentInputs: {
      drug: { ...drugMock, ...drugOverrides },
    },
  });
}

describe('DrugDetailsHeroComponent', () => {
  it('should display drug name', async () => {
    await setup();
    expect(
      screen.getByRole('heading', { level: 1, name: drugMock.common_name }),
    ).toBeInTheDocument();
  });

  describe('badges', () => {
    it('should display "Nominated Drug" badge for solo nominations', async () => {
      await setup({
        drug_nominations: [drugMock.drug_nominations[0]],
      });
      expect(screen.getByText(/Nominated Drug/)).toBeInTheDocument();
    });

    it('should display "Nominated Combination Therapy" badge with link for combo nominations', async () => {
      await setup({
        drug_nominations: [drugMock.drug_nominations[1]],
      });
      expect(screen.getByText(/Nominated Combination Therapy, with/)).toBeInTheDocument();
      const link = screen.getByRole('link', { name: /Irinotecan/ });
      expect(link).toBeInTheDocument();
      expect(link).toHaveAttribute('href', '/drugs/CHEMBL481');
    });

    it('should display both badges when drug has solo and combo nominations', async () => {
      await setup();
      expect(screen.getByText(/Nominated Drug/)).toBeInTheDocument();
      expect(screen.getByText(/Nominated Combination Therapy, with/)).toBeInTheDocument();
    });
  });

  describe('description', () => {
    it('should display description when present', async () => {
      await setup();
      expect(screen.getByText(drugMock.description as string)).toBeInTheDocument();
    });

    it('should not display description when null', async () => {
      await setup({ description: null });
      expect(screen.queryByText(drugMock.description as string)).not.toBeInTheDocument();
    });
  });

  describe('chembl id', () => {
    it('should display chembl id', async () => {
      await setup();
      expect(screen.getByText('CHEMBL ID')).toBeInTheDocument();
      expect(screen.getByRole('link', { name: drugMock.chembl_id })).toBeInTheDocument();
    });
  });

  describe('drug bank id', () => {
    it('should display drug bank id when present', async () => {
      await setup();
      expect(screen.getByText('DrugBank ID')).toBeInTheDocument();
      expect(screen.getByRole('link', { name: 'DB01006' })).toBeInTheDocument();
    });

    it('should not display drug bank id when not present', async () => {
      await setup({ drug_bank_id: '' });
      expect(screen.queryByText('DrugBank ID')).not.toBeInTheDocument();
    });
  });

  describe('iupac id', () => {
    it('should display iupac id when present', async () => {
      await setup({ iupac_id: 'test-iupac-id' });
      expect(screen.getByText('IUPAC ID:')).toBeInTheDocument();
      expect(screen.getByText('test-iupac-id')).toBeInTheDocument();
    });

    it('should not display iupac id when null', async () => {
      await setup();
      expect(screen.queryByText('IUPAC ID:')).not.toBeInTheDocument();
    });
  });

  describe('aliases', () => {
    it('should display aliases when present', async () => {
      await setup();
      expect(screen.getByText(drugMock.aliases.join(', '))).toBeInTheDocument();
    });

    it('should not display aliases when empty', async () => {
      await setup({ aliases: [] });
      expect(screen.queryByText(drugMock.aliases.join(', '))).not.toBeInTheDocument();
    });
  });
});
