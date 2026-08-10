import { marmosetModelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import { MarmosetModelDetailsHeroComponent } from './marmoset-model-details-hero.component';

async function setup(model = marmosetModelMock) {
  return render(MarmosetModelDetailsHeroComponent, {
    componentInputs: {
      model: model,
    },
  });
}

describe('MarmosetModelDetailsHeroComponent', () => {
  it('should display model name', async () => {
    await setup();
    expect(
      screen.getByRole('heading', { level: 1, name: marmosetModelMock.name }),
    ).toBeInTheDocument();
  });

  it('should display model type', async () => {
    await setup();
    expect(
      screen.getByText(`Marmoset model of ${marmosetModelMock.model_type}`),
    ).toBeInTheDocument();
  });

  it('should display the consortium attribution', async () => {
    await setup();
    expect(screen.getByText('Developed by the MARMO-AD Consortium')).toBeInTheDocument();
  });

  it('should display modified genes', async () => {
    await setup();
    expect(screen.getByText('Modified Gene')).toBeVisible();
    const gene = marmosetModelMock.genetic_info[0];
    expect(screen.getByRole('link', { name: gene.ensembl_gene_id })).toBeInTheDocument();
  });

  it('should not display mouse-only sections', async () => {
    await setup();
    expect(screen.queryByText('Matched Controls')).not.toBeInTheDocument();
    expect(screen.queryByText('ALSO KNOWN AS')).not.toBeInTheDocument();
    expect(screen.queryByText('JAX Stock Number:', { exact: false })).not.toBeInTheDocument();
    expect(screen.queryByText('RRID:', { exact: false })).not.toBeInTheDocument();
  });
});
