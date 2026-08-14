import { GeneticInfo } from '@sagebionetworks/model-ad/api-client';
import { marmosetModelMock, mouseModelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import { ModelDetailsModifiedGenesComponent } from './model-details-modified-genes.component';

async function setup(geneticInfo: GeneticInfo[] = mouseModelMock.genetic_info) {
  return render(ModelDetailsModifiedGenesComponent, {
    componentInputs: {
      geneticInfo,
    },
  });
}

describe('ModelDetailsModifiedGenesComponent', () => {
  it('should display a singular heading for one gene', async () => {
    await setup([mouseModelMock.genetic_info[0]]);
    expect(screen.getByText('Modified Gene')).toBeVisible();
  });

  it('should display a plural heading for multiple genes', async () => {
    await setup();
    expect(screen.getByText('Modified Genes')).toBeVisible();
    mouseModelMock.genetic_info.forEach((gene) => {
      expect(screen.getByRole('link', { name: gene.ensembl_gene_id })).toBeInTheDocument();
    });
  });

  it('should display allele type', async () => {
    await setup();
    const gene = mouseModelMock.genetic_info[2];
    expect(screen.getByText(`Type: ${gene.allele_type}`)).toBeInTheDocument();
  });

  describe('ensembl link', () => {
    it('should use mouse species for mouse ensembl gene id', async () => {
      await setup();
      const gene = mouseModelMock.genetic_info[0];
      const ensemblLink = screen.getByRole('link', { name: gene.ensembl_gene_id });
      expect(ensemblLink.getAttribute('href')).toContain('Mus_musculus');
    });

    it('should use marmoset species for marmoset ensembl gene id', async () => {
      await setup(marmosetModelMock.genetic_info);
      const gene = marmosetModelMock.genetic_info[0];
      const ensemblLink = screen.getByRole('link', { name: gene.ensembl_gene_id });
      expect(ensemblLink.getAttribute('href')).toContain('Callithrix_jacchus');
    });

    it('should use human species for human ensembl gene id', async () => {
      const humanGene = { ...mouseModelMock.genetic_info[1], ensembl_gene_id: 'ENSG00000139618' };
      await setup([humanGene]);
      const ensemblLink = screen.getByRole('link', { name: humanGene.ensembl_gene_id });
      expect(ensemblLink.getAttribute('href')).toContain('Homo_sapiens');
    });

    it('should use https', async () => {
      await setup();
      const gene = mouseModelMock.genetic_info[0];
      const ensemblLink = screen.getByRole('link', { name: gene.ensembl_gene_id });
      expect(ensemblLink.getAttribute('href')).toMatch(/^https:/);
    });

    it('should use latest ensembl release version', async () => {
      await setup();
      const gene = mouseModelMock.genetic_info[0];
      const ensemblLink = screen.getByRole('link', { name: gene.ensembl_gene_id });
      expect(ensemblLink.getAttribute('href')).toContain('sep2025');
    });
  });

  describe('allele', () => {
    it('should create MGI allele link', async () => {
      await setup();
      const gene = mouseModelMock.genetic_info[1]; // allele without HTML tags
      const mgiLink = screen.getByRole('link', { name: gene.allele });
      expect(mgiLink.getAttribute('href')).toBe(
        `https://www.informatics.jax.org/allele/MGI:${gene.mgi_allele_id}`,
      );
    });

    it('should not display allele for genes without one', async () => {
      await setup(marmosetModelMock.genetic_info);
      expect(screen.queryByText('Allele:', { exact: false })).not.toBeInTheDocument();
      const mgiLinks = screen
        .getAllByRole('link')
        .filter((link) => link.getAttribute('href')?.includes('informatics.jax.org'));
      expect(mgiLinks).toHaveLength(0);
    });
  });
});
