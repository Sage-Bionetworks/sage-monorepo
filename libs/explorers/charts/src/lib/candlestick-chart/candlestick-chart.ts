import { CustomSeriesOption, ECharts, EChartsOption, ScatterSeriesOption } from 'echarts';
import { CallbackDataParams } from 'echarts/types/dist/shared';
import { DARK_TOOLTIP, DEFAULT_COLOR, DEFAULT_POINT_SIZE } from '../constants';
import { CandlestickProps } from '../models';
import { AXIS_STYLE, CI_LINE_WIDTH, GRID_TOP, TEXT_STYLE } from '../point-plot-defaults';
import { GridCoordSys } from '../types';
import { initChart, setNoDataOption } from '../utils';

const REFERENCE_LINE_STYLE = { lineWidth: 1, fill: 'none' };

const X_AXIS_LABEL_TEXT_STYLE = {
  color: AXIS_STYLE.tickLabelColor,
  fontSize: 16,
  fontWeight: 'bold' as const,
};
const Y_AXIS_LABEL_TEXT_STYLE = { color: AXIS_STYLE.tickLabelColor, fontSize: 14 };

const Y_AXIS_TITLE_GAP = 60;
const GRID_BOTTOM_WITHOUT_X_AXIS_TITLE = 30;

const GRID = { left: 80, right: 30, containLabel: true };

export const DEFAULT_CHART_HEIGHT = '475px';
export const DEFAULT_REFERENCE_LINE_COLOR = 'red';

export function resolveItemColor(
  itemColor: string | undefined,
  propDefault: string | undefined,
): string {
  return itemColor || propDefault || DEFAULT_COLOR;
}

export function computeYBounds(props: CandlestickProps): [number, number] {
  if (props.yAxisMin !== undefined && props.yAxisMax !== undefined) {
    return [props.yAxisMin, props.yAxisMax];
  }
  const maxAbs = props.items.reduce(
    (acc, item) => Math.max(acc, Math.abs(item.ciLower), Math.abs(item.ciUpper)),
    0,
  );
  const bound = maxAbs === 0 ? 1 : maxAbs * 1.1;
  return [-bound, bound];
}

function ciLineSeries(props: CandlestickProps): CustomSeriesOption {
  return {
    type: 'custom',
    clip: false,
    data: props.items.map((item) => ({
      value: [item.xAxisCategory, item.ciLower, item.ciUpper],
      itemStyle: { color: resolveItemColor(item.color, props.defaultLineColor) },
    })),
    renderItem: (_params, api) => {
      const xCategory = api.value(0) as string;
      const ciLower = api.value(1) as number;
      const ciUpper = api.value(2) as number;
      const [x, y1] = api.coord([xCategory, ciLower]);
      const [, y2] = api.coord([xCategory, ciUpper]);
      const color = api.visual('color') as string;

      return {
        type: 'line',
        shape: { x1: x, y1, x2: x, y2 },
        style: { stroke: color, lineWidth: CI_LINE_WIDTH },
      };
    },
    silent: true,
    tooltip: { show: false },
    z: 1,
  };
}

// ECharts custom series requires at least one data point to trigger renderItem. We pass
// [referenceCategory, referenceValue] so api.coord() gives the pixel y-position for the
// reference value. Any valid x-axis category works -- only the y position matters. The
// caller must pass a non-empty `referenceCategory`.
function referenceLineSeries(
  referenceCategory: string,
  referenceLineValue: number,
  referenceLineColor: string,
): CustomSeriesOption {
  return {
    type: 'custom',
    clip: false,
    itemStyle: { color: 'transparent' },
    renderItem: (params, api) => {
      const [, refY] = api.coord([referenceCategory, referenceLineValue]);
      const gridBounds = params.coordSys as unknown as GridCoordSys;
      return {
        type: 'line',
        shape: {
          x1: gridBounds.x,
          y1: refY,
          x2: gridBounds.x + gridBounds.width,
          y2: refY,
        },
        style: { ...REFERENCE_LINE_STYLE, stroke: referenceLineColor },
      };
    },
    data: [{ value: [referenceCategory, referenceLineValue] }],
    silent: true,
    z: 0,
    tooltip: { show: false },
  };
}

function dotSeries(props: CandlestickProps): ScatterSeriesOption {
  return {
    type: 'scatter',
    symbolSize: props.pointSize ?? DEFAULT_POINT_SIZE,
    data: props.items.map((item) => ({
      value: [item.xAxisCategory, item.value],
      itemStyle: { color: resolveItemColor(item.color, props.defaultPointColor), opacity: 1 },
    })),
    tooltip: {
      formatter: (rawParams) => {
        const params = rawParams as CallbackDataParams;
        const item = props.items[params.dataIndex];
        return props.pointTooltipFormatter
          ? props.pointTooltipFormatter(item)
          : `Value: ${item.value}`;
      },
    },
    z: 2,
  };
}

export class CandlestickChart {
  chart: ECharts | undefined;

  constructor(chartDom: HTMLDivElement | HTMLCanvasElement, props: CandlestickProps) {
    this.chart = initChart(chartDom, DEFAULT_CHART_HEIGHT);
    this.setOptions(props);
  }

  destroy() {
    this.chart?.dispose();
  }

  setOptions(props: CandlestickProps) {
    if (!this.chart) return;

    if (!props.items.length) {
      setNoDataOption(this.chart, props.noDataStyle ?? 'textOnly');
      return;
    }

    const xAxisCategories = props.xAxisCategories ?? [
      ...new Set(props.items.map((item) => item.xAxisCategory)),
    ];
    const [yMin, yMax] = computeYBounds(props);
    const xAxisLabelTooltipFormatter = props.xAxisLabelTooltipFormatter;

    const series: (CustomSeriesOption | ScatterSeriesOption)[] = [];
    if (props.referenceLineValue !== undefined) {
      // Anchor on the first item's category so it is always defined, even if a caller
      // passes an explicit empty xAxisCategories array alongside non-empty items.
      series.push(
        referenceLineSeries(
          props.items[0].xAxisCategory,
          props.referenceLineValue,
          props.referenceLineColor ?? DEFAULT_REFERENCE_LINE_COLOR,
        ),
      );
    }
    series.push(ciLineSeries(props), dotSeries(props));

    const option: EChartsOption = {
      grid: {
        ...GRID,
        top: props.title ? GRID_TOP.withTitle : GRID_TOP.withoutTitle,
        bottom: props.xAxisTitle ? AXIS_STYLE.titleGap : GRID_BOTTOM_WITHOUT_X_AXIS_TITLE,
      },
      xAxis: {
        type: 'category',
        data: xAxisCategories,
        name: props.xAxisTitle,
        nameLocation: 'middle',
        nameGap: AXIS_STYLE.titleGap,
        nameTextStyle: TEXT_STYLE.axisName,
        axisTick: { show: false },
        axisLine: { show: true, lineStyle: AXIS_STYLE.line },
        triggerEvent: Boolean(xAxisLabelTooltipFormatter),
        tooltip: {
          ...DARK_TOOLTIP,
          show: Boolean(xAxisLabelTooltipFormatter),
          ...(xAxisLabelTooltipFormatter && {
            formatter: (params: CallbackDataParams) => xAxisLabelTooltipFormatter(params.name),
          }),
        },
        axisLabel: { ...X_AXIS_LABEL_TEXT_STYLE, margin: 12 },
      },
      yAxis: {
        type: 'value',
        min: yMin,
        max: yMax,
        name: props.yAxisTitle,
        nameLocation: 'middle',
        nameGap: Y_AXIS_TITLE_GAP,
        nameTextStyle: TEXT_STYLE.axisName,
        axisLine: { show: true, lineStyle: AXIS_STYLE.line },
        splitLine: { show: false },
        splitNumber: AXIS_STYLE.valueAxisSplitNumber,
        axisLabel: {
          ...Y_AXIS_LABEL_TEXT_STYLE,
          formatter: (tickValue: number) => tickValue.toFixed(1),
        },
      },
      series,
      tooltip: DARK_TOOLTIP,
      title: props.title
        ? [{ text: props.title, left: 'center', textStyle: TEXT_STYLE.title }]
        : [],
      aria: { enabled: true },
    };

    // notMerge must be set to true to override any existing options set on the chart
    this.chart.setOption(option, true);
  }
}
