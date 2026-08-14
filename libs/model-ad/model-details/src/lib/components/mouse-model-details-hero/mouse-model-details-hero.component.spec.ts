import { mouseModelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import { MouseModelDetailsHeroComponent } from './mouse-model-details-hero.component';

async function setup(model = mouseModelMock) {
  return render(MouseModelDetailsHeroComponent, {
    imports: [],
    componentInputs: {
      model: model,
    },
  });
}

describe('MouseModelDetailsHeroComponent', () => {
  it('should display model name', async () => {
    await setup();
    expect(
      screen.getByRole('heading', { level: 1, name: mouseModelMock.name }),
    ).toBeInTheDocument();
  });

  it('should display model type', async () => {
    await setup();
    expect(screen.getByText(`Mouse model of ${mouseModelMock.model_type}`)).toBeInTheDocument();
  });

  it('should display contributing group', async () => {
    await setup();
    expect(
      screen.getByText(`Developed by the ${mouseModelMock.contributing_group} MODEL-AD Center`),
    ).toBeInTheDocument();
  });

  it('should display modified genes', async () => {
    await setup();
    expect(screen.getByText('Modified Genes')).toBeVisible();
    mouseModelMock.genetic_info.forEach((gene) => {
      expect(screen.getByRole('link', { name: gene.ensembl_gene_id })).toBeInTheDocument();
    });
  });

  it('should display a singular heading for one matched control', async () => {
    await setup({ ...mouseModelMock, matched_controls: ['B6129'] });
    expect(screen.getByText('Matched Control')).toBeVisible();
    expect(screen.getByRole('link', { name: 'B6129' })).toBeInTheDocument();
  });

  it('should display a plural heading for multiple matched controls', async () => {
    const matchedControls = ['B6129', 'C57BL/6J'];
    await setup({ ...mouseModelMock, matched_controls: matchedControls });
    expect(screen.getByText('Matched Controls')).toBeVisible();
    matchedControls.forEach((matchedControl) => {
      expect(screen.getByRole('link', { name: matchedControl })).toBeInTheDocument();
    });
  });

  it('should display JAX stock number as link', async () => {
    await setup();
    const jaxLink = screen.getByRole('link', { name: mouseModelMock.jax_id.toString() });
    expect(jaxLink.getAttribute('href')).toBe(
      `https://www.jax.org/strain/${mouseModelMock.jax_id}`,
    );
    expect(jaxLink.getAttribute('target')).toBe('_blank');
  });

  it('should display RRID', async () => {
    await setup();
    expect(screen.getByText(`RRID: ${mouseModelMock.rrid}`)).toBeInTheDocument();
  });
});
