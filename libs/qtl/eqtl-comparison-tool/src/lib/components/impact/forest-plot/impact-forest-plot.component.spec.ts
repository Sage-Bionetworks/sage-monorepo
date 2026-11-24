import { By } from '@angular/platform-browser';
import { ForestPlotDirective } from '@sagebionetworks/explorers/charts-angular';
import { CellClassId } from '@sagebionetworks/qtl/config';
import { coloredCellClassEffectSizesMock } from '@sagebionetworks/qtl/testing';
import { render, screen, within } from '@testing-library/angular';
import {
  ColoredCellClassEffectSize,
  ImpactForestPlotComponent,
} from './impact-forest-plot.component';
import {
  IMPACT_FOREST_PLOT_TITLE,
  IMPACT_FOREST_PLOT_X_AXIS_TITLE,
} from './impact-forest-plot.constants';

const astrocyte: CellClassId = 'class_astro';

async function setup(
  selectedCellClassId: string = astrocyte,
  effectSizes: ColoredCellClassEffectSize[] = coloredCellClassEffectSizesMock,
) {
  const { fixture } = await render(ImpactForestPlotComponent, {
    componentInputs: { selectedCellClassId, effectSizes },
  });
  const forestPlot = fixture.debugElement
    .query(By.directive(ForestPlotDirective))
    .injector.get(ForestPlotDirective);
  return { forestPlot };
}

function findExampleEffectSize(cellClassId: CellClassId): ColoredCellClassEffectSize {
  const effectSize = coloredCellClassEffectSizesMock.find(
    (example) => example.cell_type_id === cellClassId,
  );
  if (!effectSize) throw new Error(`No example effect size for ${cellClassId}`);
  return effectSize;
}

describe('ImpactForestPlotComponent', () => {
  it('captions the chart and titles its x-axis', async () => {
    await setup();
    const figure = screen.getByRole('figure', { name: IMPACT_FOREST_PLOT_TITLE });
    expect(within(figure).getByText(IMPACT_FOREST_PLOT_X_AXIS_TITLE)).toBeVisible();
  });

  it('lists the cell classes top to bottom alphabetically by full name, as uppercase abbreviations', async () => {
    const { forestPlot } = await setup();
    expect(forestPlot.items().map((item) => item.yAxisCategory)).toEqual([
      'ASTRO',
      'EN',
      'IMMUNE',
      'IN',
      'OLIGO',
      'OPC',
      'VASCULAR',
    ]);
  });

  it('plots each cell class’s effect size and confidence interval in the color it was given', async () => {
    const { forestPlot } = await setup();
    const astrocyteEffectSize = findExampleEffectSize(astrocyte);
    expect(forestPlot.items()).toContainEqual({
      yAxisCategory: 'ASTRO',
      value: astrocyteEffectSize.value,
      ciLeft: astrocyteEffectSize.ci_left,
      ciRight: astrocyteEffectSize.ci_right,
      color: astrocyteEffectSize.color,
    });
  });

  it('bolds the row of the selected cell class', async () => {
    const { forestPlot } = await setup(astrocyte);
    expect(forestPlot.selectedYAxisCategory()).toBe('ASTRO');
  });

  it('bolds no row when the selected cell class has no effect size', async () => {
    const effectSizesWithoutAstrocyte = coloredCellClassEffectSizesMock.filter(
      (effectSize) => effectSize.cell_type_id !== astrocyte,
    );
    const { forestPlot } = await setup(astrocyte, effectSizesWithoutAstrocyte);
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

  it("shows a point's tooltip when hovering its confidence interval line too", async () => {
    const { forestPlot } = await setup();
    expect(forestPlot.showCILineTooltip()).toBe(true);
  });

  it('pins its left-aligned tooltips above the hovered point or label', async () => {
    const { forestPlot } = await setup();
    expect(forestPlot.tooltipStyle()).toMatchObject({ pinAbove: true, textAlign: 'left' });
  });
});
