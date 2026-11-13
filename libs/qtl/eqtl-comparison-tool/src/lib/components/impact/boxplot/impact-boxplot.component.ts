import { Component, computed, input } from '@angular/core';
import {
  AxisLineStyle,
  AxisTickLabelStyle,
  BoxplotBoxStyle,
  CategoryBoxplotSummary,
  TooltipStyle,
} from '@sagebionetworks/explorers/charts';
import { BoxplotDirective } from '@sagebionetworks/explorers/charts-angular';
import { CELL_CLASSES, CELL_SUBCLASSES } from '@sagebionetworks/qtl/config';
import {
  IMPACT_BOXPLOT_CHART_HEIGHT_PX,
  IMPACT_BOXPLOT_COLORS,
  IMPACT_BOXPLOT_GRID_MARGINS,
  IMPACT_BOXPLOT_TITLE_PREFIX,
} from './impact-boxplot.constants';

export interface GenotypeExpressionSummary {
  genotype: string;
  min: number;
  first_quartile: number;
  median: number;
  third_quartile: number;
  max: number;
}

@Component({
  selector: 'qtl-eqtl-comparison-tool-impact-boxplot',
  imports: [BoxplotDirective],
  templateUrl: './impact-boxplot.component.html',
  styleUrls: ['./impact-boxplot.component.scss'],
})
export class ImpactBoxplotComponent {
  cellSubclass = input.required<keyof typeof CELL_SUBCLASSES>();
  // Plotted in the given order, labeled by each summary's genotype.
  expressionSummaries = input.required<GenotypeExpressionSummary[]>();

  protected readonly titlePrefix = IMPACT_BOXPLOT_TITLE_PREFIX;
  protected readonly chartHeight = IMPACT_BOXPLOT_CHART_HEIGHT_PX;
  protected readonly gridMargins = IMPACT_BOXPLOT_GRID_MARGINS;
  protected readonly laneBackgroundColors = [...IMPACT_BOXPLOT_COLORS.laneBackgrounds];
  protected readonly axisTickLabelStyle: AxisTickLabelStyle = {
    fontSize: '12px',
    fontWeight: 400,
    color: IMPACT_BOXPLOT_COLORS.axisTickLabel,
  };
  protected readonly axisLineStyle: AxisLineStyle = {
    width: 1,
    color: IMPACT_BOXPLOT_COLORS.axisLine,
  };
  protected readonly tooltipStyle: TooltipStyle = {
    backgroundColor: IMPACT_BOXPLOT_COLORS.tooltipBackground,
  };

  // TODO: confirm vibrant vs muted fill in the design pass (EX-285)
  readonly boxplotBoxStyle = computed<BoxplotBoxStyle>(() => ({
    fillColor: CELL_CLASSES[CELL_SUBCLASSES[this.cellSubclass()].cellClass].color,
    borderColor: IMPACT_BOXPLOT_COLORS.boxBorder,
    borderWidth: 1,
  }));

  readonly genotypes = computed(() =>
    this.expressionSummaries().map((summary) => summary.genotype),
  );

  readonly summaries = computed<CategoryBoxplotSummary[]>(() =>
    this.expressionSummaries().map((summary) => ({
      xAxisCategory: summary.genotype,
      min: summary.min,
      firstQuartile: summary.first_quartile,
      median: summary.median,
      thirdQuartile: summary.third_quartile,
      max: summary.max,
    })),
  );
}
