import { EChartsOption } from 'echarts';
import { CallbackDataParams } from 'echarts/types/dist/shared';
import { ForestPlotProps } from '../models';
import { AXIS_STYLE, TEXT_STYLE } from '../point-plot-defaults';

const X_AXIS_LABEL_TEXT_STYLE = { color: AXIS_STYLE.tickLabelColor, fontSize: 14 };
const Y_AXIS_LABEL_TEXT_STYLE = { color: AXIS_STYLE.tickLabelColor, fontSize: 16 };

// Symmetric bounds from max(|ciLeft|, |ciRight|) *1.1
export function computeXBounds(props: ForestPlotProps): [number, number] {
  if (props.xAxisMin !== undefined && props.xAxisMax !== undefined) {
    return [props.xAxisMin, props.xAxisMax];
  }
  const maxAbs = props.items.reduce(
    (acc, item) => Math.max(acc, Math.abs(item.ciLeft), Math.abs(item.ciRight)),
    0,
  );
  const bound = maxAbs === 0 ? 1 : maxAbs * 1.1;
  return [-bound, bound];
}

// Returns multiples of `interval` (anchored to 0) that fall within [xMin, xMax].
// Ticks land on round values regardless of xMin -- e.g. interval 0.2 within [-0.1, 0.7]
// yields [0, 0.2, 0.4, 0.6], not the xMin-anchored [-0.1, 0.1, 0.3, 0.5, 0.7].
export function computeXTickPositions(xMin: number, xMax: number, interval: number): number[] {
  if (interval <= 0) return [];
  // Epsilon absorbs floating-point drift in the boundary divisions -- e.g. -0.6 / 0.2
  // computes to -2.9999..., which without nudging would ceil to -2 and skip the -0.6 tick.
  const epsilon = 1e-9;
  const firstK = Math.ceil(xMin / interval - epsilon);
  const lastK = Math.floor(xMax / interval + epsilon);
  const positions: number[] = [];
  for (let k = firstK; k <= lastK; k++) {
    // `+ 0` normalizes -0 to 0 for strict-equality assertions.
    positions.push(k * interval + 0);
  }
  return positions;
}

export function buildXAxis(
  props: ForestPlotProps,
  xMin: number,
  xMax: number,
  xTickPositions: number[],
): EChartsOption['xAxis'] {
  // With auto-computed bounds (±maxAbs * 1.1) and the default interval, xMin and xMax
  // land on tick multiples, so customValues includes labels right at the chart edges
  // where they tend to clip against the axis-line end. Suppress them in that case.
  // When the consumer explicitly sets bounds, we show every tick they asked for.
  const boundsAreExplicit = props.xAxisMin !== undefined && props.xAxisMax !== undefined;

  const bottomXAxis = {
    type: 'value' as const,
    // Ensure axes render above the row-hover axisPointer band
    z: 10,
    min: xMin,
    max: xMax,
    name: props.xAxisTitle,
    nameLocation: 'middle' as const,
    nameGap: AXIS_STYLE.titleGap,
    nameTextStyle: TEXT_STYLE.axisName,
    axisLine: {
      show: true,
      lineStyle: { ...AXIS_STYLE.line, ...props.xAxisLineStyle },
    },
    // splitLine is rendered via gridLineSeries so we control alignment + z-order
    splitLine: { show: false },
    // customValues pins ticks/labels to the exact positions our gridLineSeries
    // draws at, anchored to round multiples of the interval.
    axisTick: { customValues: xTickPositions },
    axisLabel: {
      ...X_AXIS_LABEL_TEXT_STYLE,
      formatter: (tickValue: number) => tickValue.toFixed(props.xAxisLabelPrecision ?? 2),
      customValues: xTickPositions,
      ...(!boundsAreExplicit && { showMinLabel: false, showMaxLabel: false }),
      ...props.xAxisTickLabelStyle,
    },
    // x-axis pointer is never used; only the y-axis carries the row-hover band
    axisPointer: { show: false },
  };

  if (props.showXAxisLabelsOnTop) {
    return [
      bottomXAxis,
      {
        ...bottomXAxis,
        position: 'top',
        name: undefined,
        // Top axis carries labels only -- no axis line or ticks
        axisLine: { show: false },
        axisTick: { show: false },
      },
    ];
  }
  return bottomXAxis;
}

export function buildYAxis(
  props: ForestPlotProps,
  yAxisCategories: string[],
  tooltip: EChartsOption['tooltip'],
  rowHoverLabelFormatter: ((cat: string) => string) | undefined,
): EChartsOption['yAxis'] {
  const yAxisLabelTooltipFormatter = props.yAxisLabelTooltipFormatter;
  const rowHoverHighlightStyle = props.rowHoverHighlightStyle;
  const yAxisTickLabelStyle = props.yAxisTickLabelStyle;

  return {
    type: 'category',
    // Ensure axes render above the row-hover axisPointer band
    z: 10,
    data: yAxisCategories,
    inverse: true,
    axisTick: { show: false },
    // The zero reference at x=0 is drawn by zeroLineSeries (a custom series), not by the
    // y-axis line. So the y-axis line is hidden by default. When yAxisLineStyle is provided,
    // we show it as a frame line at the left edge -- onZero: false prevents ECharts from
    // collapsing it onto x=0 where it would overlap the zero reference.
    axisLine: props.yAxisLineStyle
      ? { show: true, onZero: false, lineStyle: props.yAxisLineStyle }
      : { show: false },
    triggerEvent: Boolean(yAxisLabelTooltipFormatter),
    tooltip: {
      ...tooltip,
      show: Boolean(yAxisLabelTooltipFormatter),
      ...(yAxisLabelTooltipFormatter && {
        formatter: (params: CallbackDataParams) => yAxisLabelTooltipFormatter(params.name),
      }),
    },
    axisLabel: {
      ...Y_AXIS_LABEL_TEXT_STYLE,
      margin: 30,
      ...yAxisTickLabelStyle,
      ...(rowHoverLabelFormatter && {
        formatter: rowHoverLabelFormatter,
        rich: { b: { ...yAxisTickLabelStyle, fontWeight: 700 } },
      }),
    },
    // splitLine is rendered via gridLineSeries so it aligns with category ticks
    splitLine: { show: false },
    // Row-hover highlight: a line-type axisPointer on the y-axis paints a horizontal
    // band centered on the row tick. lineStyle.width controls the band thickness in
    // pixels (kept smaller than the row pitch so adjacent rows don't overlap).
    axisPointer: rowHoverHighlightStyle
      ? {
          show: true,
          type: 'line' as const,
          triggerEmphasis: false,
          triggerTooltip: false,
          label: { show: false },
          lineStyle: {
            // Default for axisPointer.lineStyle is 'dashed' -- force solid so the
            // band fills continuously across the row instead of showing gaps.
            type: 'solid' as const,
            width: rowHoverHighlightStyle.thickness,
            color: rowHoverHighlightStyle.backgroundColor,
          },
        }
      : { show: false },
  };
}
