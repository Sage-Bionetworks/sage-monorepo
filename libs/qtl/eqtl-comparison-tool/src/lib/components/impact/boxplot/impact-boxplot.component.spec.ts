import { By } from '@angular/platform-browser';
import { BoxplotDirective } from '@sagebionetworks/explorers/charts-angular';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { eqtlPhenotypeMock } from '@sagebionetworks/qtl/testing';
import { ResolvedCellSubclass, resolveCellSubclass } from '@sagebionetworks/qtl/util';
import { render, screen, within } from '@testing-library/angular';
import { impactPlotLoadingLabel } from '../impact-plot-loading';
import { ImpactBoxplotComponent } from './impact-boxplot.component';
import { IMPACT_BOXPLOT_TITLE_PREFIX } from './impact-boxplot.constants';

const astrocyteSubclass = resolveCellSubclass('astro');
const adaptiveImmuneSubclass = resolveCellSubclass('adaptiveimmune');

async function setup(cellSubclass: ResolvedCellSubclass = astrocyteSubclass) {
  const { fixture } = await render(ImpactBoxplotComponent, {
    providers: [provideLoadingIconColors()],
    componentInputs: { cellSubclass, expressionSummaries: eqtlPhenotypeMock.expression_summaries },
  });
  const boxplot = fixture.debugElement
    .query(By.directive(BoxplotDirective))
    .injector.get(BoxplotDirective);
  return { boxplot };
}

describe('ImpactBoxplotComponent', () => {
  it('captions the chart with the cell subclass', async () => {
    await setup();
    expect(
      within(screen.getByRole('figure')).getByText(
        `${IMPACT_BOXPLOT_TITLE_PREFIX} (${astrocyteSubclass.name})`,
      ),
    ).toBeVisible();
  });

  it('plots one box per genotype, in the given order', async () => {
    const { boxplot } = await setup();
    const genotypes = eqtlPhenotypeMock.expression_summaries.map((summary) => summary.genotype);
    expect(boxplot.xAxisCategories).toEqual(genotypes);
    expect(boxplot.summaries?.map((summary) => summary.xAxisCategory)).toEqual(genotypes);
  });

  it('carries each summary statistic over to its box', async () => {
    const { boxplot } = await setup();
    const [firstSummary] = eqtlPhenotypeMock.expression_summaries;
    expect(boxplot.summaries?.[0]).toEqual({
      xAxisCategory: firstSummary.genotype,
      min: firstSummary.min,
      firstQuartile: firstSummary.first_quartile,
      median: firstSummary.median,
      thirdQuartile: firstSummary.third_quartile,
      max: firstSummary.max,
    });
  });

  it('fills the boxes with the color of the subclass’s cell class', async () => {
    const { boxplot } = await setup(adaptiveImmuneSubclass);
    expect(boxplot.boxplotBoxStyle?.fillColor).toBe(adaptiveImmuneSubclass.cellClass.color);
  });

  it('shows a named loading status in place of the chart while loading, keeping the caption', async () => {
    const boxplotName = `${IMPACT_BOXPLOT_TITLE_PREFIX} (${astrocyteSubclass.name})`;
    const { fixture } = await render(ImpactBoxplotComponent, {
      providers: [provideLoadingIconColors()],
      componentInputs: {
        cellSubclass: astrocyteSubclass,
        expressionSummaries: [],
        isLoading: true,
      },
    });
    expect(screen.getByRole('status', { name: impactPlotLoadingLabel(boxplotName) })).toBeVisible();
    expect(screen.getByRole('figure', { name: boxplotName })).toBeVisible();
    expect(fixture.debugElement.query(By.directive(BoxplotDirective))).toBeNull();
  });
});
