import { By } from '@angular/platform-browser';
import { BoxplotDirective } from '@sagebionetworks/explorers/charts-angular';
import { CELL_CLASSES, CELL_SUBCLASSES, CellSubclassId } from '@sagebionetworks/qtl/config';
import { render, screen, within } from '@testing-library/angular';
import { ImpactBoxplotComponent } from './impact-boxplot.component';
import {
  IMPACT_BOXPLOT_EXAMPLE_SUMMARIES,
  IMPACT_BOXPLOT_TITLE_PREFIX,
} from './impact-boxplot.constants';

const astrocyteSubclass: CellSubclassId = 'astro';
const adaptiveImmuneSubclass: CellSubclassId = 'adaptiveimmune';

async function setup(cellSubclassId = astrocyteSubclass) {
  const { fixture } = await render(ImpactBoxplotComponent, {
    componentInputs: { cellSubclassId, expressionSummaries: IMPACT_BOXPLOT_EXAMPLE_SUMMARIES },
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
        `${IMPACT_BOXPLOT_TITLE_PREFIX} (${CELL_SUBCLASSES[astrocyteSubclass].name})`,
      ),
    ).toBeVisible();
  });

  it('plots one box per genotype, in the given order', async () => {
    const { boxplot } = await setup();
    const genotypes = IMPACT_BOXPLOT_EXAMPLE_SUMMARIES.map((summary) => summary.genotype);
    expect(boxplot.xAxisCategories).toEqual(genotypes);
    expect(boxplot.summaries?.map((summary) => summary.xAxisCategory)).toEqual(genotypes);
  });

  it('carries each summary statistic over to its box', async () => {
    const { boxplot } = await setup();
    const [firstSummary] = IMPACT_BOXPLOT_EXAMPLE_SUMMARIES;
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
    expect(boxplot.boxplotBoxStyle?.fillColor).toBe(
      CELL_CLASSES[CELL_SUBCLASSES[adaptiveImmuneSubclass].cellClassId].color,
    );
  });
});
