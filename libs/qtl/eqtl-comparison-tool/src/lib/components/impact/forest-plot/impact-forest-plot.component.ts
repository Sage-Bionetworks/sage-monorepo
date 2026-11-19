import { Component, computed, input } from '@angular/core';
import {
  AxisLineStyle,
  AxisTickLabelStyle,
  ForestPlotItem,
  GridLineStyle,
} from '@sagebionetworks/explorers/charts';
import { ForestPlotDirective } from '@sagebionetworks/explorers/charts-angular';
import { CELL_CLASSES, CellClassId } from '@sagebionetworks/qtl/config';
import { computeEffectSizeXAxis } from './impact-forest-plot-x-axis';
import {
  IMPACT_FOREST_PLOT_CI_LINE_WIDTH_PX,
  IMPACT_FOREST_PLOT_CHART_HEIGHT_PX,
  IMPACT_FOREST_PLOT_COLORS,
  IMPACT_FOREST_PLOT_PLOT_AREA_MARGINS,
  IMPACT_FOREST_PLOT_POINT_SIZE_PX,
  IMPACT_FOREST_PLOT_ROW_HOVER_HIGHLIGHT_STYLE,
  IMPACT_FOREST_PLOT_TARGET_TICK_INTERVALS,
  IMPACT_FOREST_PLOT_TITLE,
  IMPACT_FOREST_PLOT_TOOLTIP_STYLE,
  IMPACT_FOREST_PLOT_TOOLTIP_VALUE_DECIMALS,
  IMPACT_FOREST_PLOT_X_AXIS_LABEL_MARGIN_PX,
  IMPACT_FOREST_PLOT_X_AXIS_TITLE,
  IMPACT_FOREST_PLOT_Y_AXIS_LABEL_MARGIN_PX,
} from './impact-forest-plot.constants';

export interface CellClassEffectSize {
  cell_type_id: CellClassId;
  cell_class: string;
  cell_class_short: string;
  value: number;
  ci_left: number;
  ci_right: number;
}

const TICK_LABEL_FONT_SIZE = '12px';

function toCategoryLabel(effectSize: CellClassEffectSize): string {
  return effectSize.cell_class_short.toUpperCase();
}

@Component({
  selector: 'qtl-eqtl-comparison-tool-impact-forest-plot',
  imports: [ForestPlotDirective],
  templateUrl: './impact-forest-plot.component.html',
  styleUrls: ['./impact-forest-plot.component.scss'],
})
export class ImpactForestPlotComponent {
  selectedCellClassId = input.required<CellClassId>();
  // TODO: confirm whether cell class or cell subclass effect sizes drive this plot (EX-283)
  effectSizes = input.required<CellClassEffectSize[]>();

  protected readonly title = IMPACT_FOREST_PLOT_TITLE;
  protected readonly xAxisTitle = IMPACT_FOREST_PLOT_X_AXIS_TITLE;
  protected readonly chartHeight = IMPACT_FOREST_PLOT_CHART_HEIGHT_PX;
  protected readonly plotAreaMargins = IMPACT_FOREST_PLOT_PLOT_AREA_MARGINS;
  protected readonly pointSize = IMPACT_FOREST_PLOT_POINT_SIZE_PX;
  protected readonly ciLineWidth = IMPACT_FOREST_PLOT_CI_LINE_WIDTH_PX;
  protected readonly rowHoverHighlightStyle = IMPACT_FOREST_PLOT_ROW_HOVER_HIGHLIGHT_STYLE;
  protected readonly axisLineStyle: AxisLineStyle = {
    width: 1,
    color: IMPACT_FOREST_PLOT_COLORS.axisLine,
    cap: 'butt',
  };
  protected readonly xAxisTickLabelStyle: AxisTickLabelStyle = {
    fontSize: TICK_LABEL_FONT_SIZE,
    fontWeight: 400,
    color: IMPACT_FOREST_PLOT_COLORS.tickLabel,
    margin: IMPACT_FOREST_PLOT_X_AXIS_LABEL_MARGIN_PX,
  };
  protected readonly yAxisTickLabelStyle: AxisTickLabelStyle = {
    fontSize: TICK_LABEL_FONT_SIZE,
    fontWeight: 400,
    color: IMPACT_FOREST_PLOT_COLORS.categoryLabel,
    margin: IMPACT_FOREST_PLOT_Y_AXIS_LABEL_MARGIN_PX,
  };
  protected readonly xAxisGridLineStyle: GridLineStyle = {
    width: 1,
    color: IMPACT_FOREST_PLOT_COLORS.xAxisGridLine,
    type: 'dotted',
  };
  protected readonly yAxisGridLineStyle: GridLineStyle = {
    width: 1,
    color: IMPACT_FOREST_PLOT_COLORS.yAxisGridLine,
    type: 'solid',
  };
  protected readonly tooltipStyle = IMPACT_FOREST_PLOT_TOOLTIP_STYLE;

  private readonly orderedEffectSizes = computed(() =>
    [...this.effectSizes()].sort((a, b) => a.cell_class.localeCompare(b.cell_class)),
  );

  readonly items = computed<ForestPlotItem[]>(() =>
    this.orderedEffectSizes().map((effectSize) => ({
      yAxisCategory: toCategoryLabel(effectSize),
      value: effectSize.value,
      ciLeft: effectSize.ci_left,
      ciRight: effectSize.ci_right,
      color: CELL_CLASSES[effectSize.cell_type_id].color,
    })),
  );

  readonly selectedCategory = computed(() => {
    const selected = this.effectSizes().find(
      (effectSize) => effectSize.cell_type_id === this.selectedCellClassId(),
    );
    return selected && toCategoryLabel(selected);
  });

  readonly xAxis = computed(() =>
    computeEffectSizeXAxis(this.items(), IMPACT_FOREST_PLOT_TARGET_TICK_INTERVALS),
  );

  private readonly effectSizeByCategory = computed(
    () =>
      new Map(this.effectSizes().map((effectSize) => [toCategoryLabel(effectSize), effectSize])),
  );

  protected readonly categoryTooltipFormatter = (category: string): string => {
    const effectSize = this.effectSizeByCategory().get(category);
    return effectSize
      ? `${effectSize.cell_class}\n${effectSize.value.toFixed(IMPACT_FOREST_PLOT_TOOLTIP_VALUE_DECIMALS)}`
      : category;
  };

  protected readonly pointTooltipFormatter = (item: ForestPlotItem): string =>
    this.categoryTooltipFormatter(item.yAxisCategory);
}
