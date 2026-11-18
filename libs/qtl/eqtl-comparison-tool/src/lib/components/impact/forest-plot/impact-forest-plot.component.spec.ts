import { By } from '@angular/platform-browser';
import { ForestPlotDirective } from '@sagebionetworks/explorers/charts-angular';
import { CELL_CLASSES } from '@sagebionetworks/qtl/config';
import { render, screen, within } from '@testing-library/angular';
import { CellClassEffectSize, ImpactForestPlotComponent } from './impact-forest-plot.component';
import {
  IMPACT_FOREST_PLOT_EXAMPLE_EFFECT_SIZES,
  IMPACT_FOREST_PLOT_TITLE,
  IMPACT_FOREST_PLOT_X_AXIS_TITLE,
} from './impact-forest-plot.constants';

const astrocyte: keyof typeof CELL_CLASSES = 'Astrocyte';
const endothelial: keyof typeof CELL_CLASSES = 'Endothelial';

async function setup(
  selectedCellClass = astrocyte,
  effectSizes: CellClassEffectSize[] = IMPACT_FOREST_PLOT_EXAMPLE_EFFECT_SIZES,
) {
  const { fixture } = await render(ImpactForestPlotComponent, {
    componentInputs: { selectedCellClass, effectSizes },
  });
  const forestPlot = fixture.debugElement
    .query(By.directive(ForestPlotDirective))
    .injector.get(ForestPlotDirective);
  return { forestPlot };
}

function findExampleEffectSize(cellClass: keyof typeof CELL_CLASSES): CellClassEffectSize {
  const effectSize = IMPACT_FOREST_PLOT_EXAMPLE_EFFECT_SIZES.find(
    (example) => example.cell_class === cellClass,
  );
  if (!effectSize) throw new Error(`No example effect size for ${cellClass}`);
  return effectSize;
}

describe('ImpactForestPlotComponent', () => {
  it('captions the chart and titles its x-axis', async () => {
    await setup();
    const figure = screen.getByRole('figure', { name: IMPACT_FOREST_PLOT_TITLE });
    expect(within(figure).getByText(IMPACT_FOREST_PLOT_X_AXIS_TITLE)).toBeVisible();
  });

  it('lists the cell classes top to bottom in the design order, as uppercase abbreviations', async () => {
    const { forestPlot } = await setup();
    expect(forestPlot.items().map((item) => item.yAxisCategory)).toEqual([
      'VASCULAR',
      'IMMUNE',
      'ASTRO',
      'OPC',
      'OLIGO',
      'IN',
      'EN',
    ]);
  });

  it('plots each cell class’s effect size and confidence interval in its cell class color', async () => {
    const { forestPlot } = await setup();
    const astrocyteEffectSize = findExampleEffectSize(astrocyte);
    expect(forestPlot.items()).toContainEqual({
      yAxisCategory: 'ASTRO',
      value: astrocyteEffectSize.value,
      ciLeft: astrocyteEffectSize.ci_left,
      ciRight: astrocyteEffectSize.ci_right,
      color: CELL_CLASSES[astrocyte].color,
    });
  });

  it('bolds the row of the selected cell class', async () => {
    const { forestPlot } = await setup(astrocyte);
    expect(forestPlot.selectedYAxisCategory()).toBe('ASTRO');
  });

  it('bolds no row when the selected cell class has no effect size', async () => {
    const { forestPlot } = await setup(endothelial);
    expect(forestPlot.selectedYAxisCategory()).toBeUndefined();
  });

  it('shows the full cell class and its effect size, on two lines, when hovering a point', async () => {
    const { forestPlot } = await setup();
    const immuneItem = forestPlot.items().find((item) => item.yAxisCategory === 'IMMUNE');
    expect(immuneItem && forestPlot.pointTooltipFormatter()?.(immuneItem)).toBe(
      'Immune Cell\n-0.22',
    );
  });

  it('shows the same full cell class and effect size when hovering a row label', async () => {
    const { forestPlot } = await setup();
    expect(forestPlot.yAxisLabelTooltipFormatter()?.('OPC')).toBe(
      'Oligodendrocyte Progenitor Cell\n0.52',
    );
  });

  it('pins its left-aligned tooltips above the hovered point or label', async () => {
    const { forestPlot } = await setup();
    expect(forestPlot.tooltipStyle()).toMatchObject({ pinAbove: true, textAlign: 'left' });
  });
});
