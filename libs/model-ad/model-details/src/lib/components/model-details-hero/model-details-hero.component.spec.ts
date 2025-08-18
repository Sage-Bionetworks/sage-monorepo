import { modelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import { ModelDetailsHeroComponent } from './model-details-hero.component';

async function setup(model = modelMock) {
  return render(ModelDetailsHeroComponent, {
    imports: [],
    componentInputs: {
      model: model,
    },
  });
}

describe('ModelDetailsHeroComponent', () => {
  it('should display model name', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 1, name: modelMock.name })).toBeInTheDocument();
  });

  it('should display model type', async () => {
    await setup();
    expect(screen.getByText(`Mouse model of ${modelMock.model_type}`)).toBeInTheDocument();
  });

  it('should display contributing group', async () => {
    await setup();
    expect(
      screen.getByText(`Developed by the ${modelMock.contributing_group} MODEL-AD Center`),
    ).toBeInTheDocument();
  });

  it('should display matched controls', async () => {
    await setup();
    expect(screen.getByText(modelMock.matched_controls.join(', '))).toBeInTheDocument();
  });

  it('should display JAX stock number as link', async () => {
    await setup();
    const jaxLink = screen.getByRole('link', { name: modelMock.jax_id.toString() });
    expect(jaxLink.getAttribute('href')).toBe(`https://www.jax.org/strain/${modelMock.jax_id}`);
    expect(jaxLink.getAttribute('target')).toBe('_blank');
  });

  it('should display RRID', async () => {
    await setup();
    expect(screen.getByText(`RRID: ${modelMock.rrid}`)).toBeInTheDocument();
  });

  describe('modified genes', () => {
    it('should use mouse species for link for mouse ensembl gene id', async () => {
      await setup();
      const gene = modelMock.genetic_info[0];
      const ensemblLink = screen.getByRole('link', { name: gene.ensembl_gene_id });
      expect(ensemblLink.getAttribute('href')).toContain('Mus_musculus');
    });

    it('should use human species for link for human ensembl gene id', async () => {
      const humanGene = { ...modelMock.genetic_info[1], ensembl_gene_id: 'ENSG00000139618' };
      await setup({ ...modelMock, genetic_info: [humanGene] });
      const ensemblLink = screen.getByRole('link', { name: humanGene.ensembl_gene_id });
      expect(ensemblLink.getAttribute('href')).toContain('Homo_sapiens');
    });

    it('should display allele type', async () => {
      await setup();
      const gene = modelMock.genetic_info[2];
      expect(screen.getByText(`Type: ${gene.allele_type}`)).toBeInTheDocument();
    });

    it('should create MGI allele link', async () => {
      await setup();
      const gene = modelMock.genetic_info[1]; // allele without HTML tags
      const mgiLink = screen.getByRole('link', { name: gene.allele });
      expect(mgiLink.getAttribute('href')).toBe(
        `https://www.informatics.jax.org/allele/MGI:${gene.mgi_allele_id}`,
      );
    });

    it('should display one gene correctly', async () => {
      await setup({ ...modelMock, genetic_info: [modelMock.genetic_info[0]] });
      expect(screen.getByText('Modified Gene')).toBeVisible();
    });

    it('should display multiple genes correctly', async () => {
      await setup();
      expect(screen.getByText('Modified Genes')).toBeVisible();
      modelMock.genetic_info.forEach((gene) => {
        expect(screen.getByRole('link', { name: gene.ensembl_gene_id })).toBeInTheDocument();
      });
    });
  });
});
