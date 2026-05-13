import { ECharts, EChartsOption } from 'echarts';
import { DARK_TOOLTIP } from '../constants';
import { ForestPlotProps } from '../models';
import { AXIS_STYLE, GRID_TOP, TEXT_STYLE } from '../point-plot-defaults';
import { buildTooltip, initChart, setNoDataOption } from '../utils';
import { buildXAxis, buildYAxis, computeXBounds, computeXTickPositions } from './forest-plot-axis';
import { RowHoverController } from './forest-plot-row-hover';
import { buildSeries } from './forest-plot-series';

const GRID_BOTTOM_WITHOUT_X_AXIS_TITLE = 40;
const GRID_DEFAULT_MARGINS = GRID_TOP.withoutTitle + GRID_BOTTOM_WITHOUT_X_AXIS_TITLE;

const GRID = { right: 80, containLabel: true };

const TOP_X_AXIS_LABEL_HEIGHT = 30;

export function computeInitialHeight(rowCount: number): string {
  // ~44px per row + GRID_DEFAULT_MARGINS; 490px floor
  return `${Math.max(rowCount * 44 + GRID_DEFAULT_MARGINS, 490)}px`;
}

export class ForestPlotChart {
  chart: ECharts | undefined;
  private rowHoverController: RowHoverController | null = null;

  constructor(chartDom: HTMLDivElement | HTMLCanvasElement, props: ForestPlotProps) {
    // Height is set once at construction time based on the number of rows to render.
    // If row count changes after construction (via setOptions), the DOM height will not update.
    // This is intentional for the current use case where row count is fixed.
    this.chart = initChart(
      chartDom,
      computeInitialHeight(props.yAxisCategories?.length ?? props.items.length),
    );
    this.setOptions(props);
  }

  destroy() {
    this.rowHoverController?.detach();
    this.rowHoverController = null;
    this.chart?.dispose();
  }

  setOptions(props: ForestPlotProps) {
    if (!this.chart) return;

    if (!props.items.length) {
      setNoDataOption(this.chart, props.noDataStyle ?? 'textOnly');
      return;
    }

    const yAxisCategories = props.yAxisCategories ?? [
      ...new Set(props.items.map((item) => item.yAxisCategory)),
    ];
    const [xMin, xMax] = computeXBounds(props);
    // Tick positions are anchored to integer multiples of the interval (not to xMin),
    // so an interval of 0.2 within [-0.1, 0.7] yields [0, 0.2, 0.4, 0.6] rather than
    // [-0.1, 0.1, 0.3, 0.5, 0.7]. We pass these positions to ECharts via customValues
    // so axis ticks align with our gridLineSeries.
    const xAxisInterval = props.xAxisInterval ?? (xMax - xMin) / AXIS_STYLE.valueAxisSplitNumber;
    const xTickPositions = computeXTickPositions(xMin, xMax, xAxisInterval);
    const tooltip = buildTooltip(DARK_TOOLTIP, props.tooltipStyle);

    // Detach the previous row-hover controller before re-rendering so its listener
    // doesn't fire against stale yAxisCategories during setOption. A new controller
    // (with fresh hover state) is created if row-hover is still enabled.
    this.rowHoverController?.detach();
    this.rowHoverController = props.rowHoverHighlightStyle
      ? new RowHoverController(this.chart, yAxisCategories)
      : null;

    const option: EChartsOption = {
      grid: {
        ...GRID,
        top:
          (props.title ? GRID_TOP.withTitle : GRID_TOP.withoutTitle) +
          (props.showXAxisLabelsOnTop ? TOP_X_AXIS_LABEL_HEIGHT : 0),
        bottom: props.xAxisTitle ? AXIS_STYLE.titleGap : GRID_BOTTOM_WITHOUT_X_AXIS_TITLE,
      },
      xAxis: buildXAxis(props, xMin, xMax, xTickPositions),
      yAxis: buildYAxis(
        props,
        yAxisCategories,
        tooltip,
        this.rowHoverController?.getYAxisLabelFormatter(),
      ),
      series: buildSeries(props, yAxisCategories, xMin, xMax, xTickPositions),
      tooltip,
      // Top-level axisPointer enables the y-axis pointer to track the cursor on
      // mousemove (instead of only on click or when a tooltip is active).
      ...(props.rowHoverHighlightStyle && {
        axisPointer: { show: true, triggerOn: 'mousemove' as const },
      }),
      title: props.title
        ? [{ text: props.title, left: 'center', textStyle: TEXT_STYLE.title }]
        : [],
      aria: { enabled: true },
    };

    // notMerge must be set to true to override any existing options set on the chart
    this.chart.setOption(option, true);

    this.rowHoverController?.attach();
  }
}
