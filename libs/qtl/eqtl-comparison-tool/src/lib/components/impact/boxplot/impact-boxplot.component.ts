import { Component, computed, input } from '@angular/core';
import {
  AxisLineStyle,
  AxisTickLabelStyle,
  BoxplotBoxStyle,
  CategoryBoxplotSummary,
  TooltipStyle,
} from '@sagebionetworks/explorers/charts';
import { BoxplotDirective } from '@sagebionetworks/explorers/charts-angular';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { GenotypeExpressionSummary } from '@sagebionetworks/qtl/api-client';
import { ResolvedCellSubclass } from '@sagebionetworks/qtl/util';
import {
  IMPACT_BOXPLOT_CHART_HEIGHT_PX,
  IMPACT_BOXPLOT_COLORS,
  IMPACT_BOXPLOT_PLOT_AREA_MARGINS,
  IMPACT_BOXPLOT_TITLE_PREFIX,
} from './impact-boxplot.constants';
import { impactPlotLoadingLabel } from '../impact-plot-loading';

@Component({
  selector: 'qtl-eqtl-comparison-tool-impact-boxplot',
  imports: [BoxplotDirective, LoadingIconComponent],
  templateUrl: './impact-boxplot.component.html',
  styleUrls: ['./impact-boxplot.component.scss'],
})
export class ImpactBoxplotComponent {
  cellSubclass = input.required<ResolvedCellSubclass>();
  // Plotted in the given order, labeled by each summary's genotype.
  expressionSummaries = input.required<GenotypeExpressionSummary[]>();
  isLoading = input(false);

  protected readonly titlePrefix = IMPACT_BOXPLOT_TITLE_PREFIX;
  protected readonly chartHeight = IMPACT_BOXPLOT_CHART_HEIGHT_PX;
  protected readonly plotAreaMargins = IMPACT_BOXPLOT_PLOT_AREA_MARGINS;
  protected readonly laneBackgroundColors = [...IMPACT_BOXPLOT_COLORS.laneBackgrounds];
  protected readonly axisTickLabelStyle: AxisTickLabelStyle = {
    fontSize: '12px',
    fontWeight: 400,
    color: IMPACT_BOXPLOT_COLORS.axisTickLabel,
  };
  protected readonly axisLineStyle: AxisLineStyle = {
    width: 1,
    color: IMPACT_BOXPLOT_COLORS.axisLine,
    cap: 'butt',
  };
  protected readonly tooltipStyle: TooltipStyle = {
    backgroundColor: IMPACT_BOXPLOT_COLORS.tooltipBackground,
  };

  // TODO: confirm vibrant vs muted fill in the design pass (EX-285)
  readonly boxplotBoxStyle = computed<BoxplotBoxStyle>(() => ({
    fillColor: this.cellSubclass().cellClass.color,
    borderColor: IMPACT_BOXPLOT_COLORS.boxBorder,
    borderWidth: 1,
  }));

  protected readonly ariaLabel = computed(
    () => `${this.titlePrefix} (${this.cellSubclass().name})`,
  );

  protected readonly loadingLabel = computed(() => impactPlotLoadingLabel(this.ariaLabel()));

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
