import { CustomSeriesOption, ECharts, EChartsOption, ScatterSeriesOption } from 'echarts';
import { CallbackDataParams } from 'echarts/types/dist/shared';
import { DARK_TOOLTIP } from '../constants';
import { ForestPlotProps } from '../models';
import { initChart, setNoDataOption } from '../utils';

// --- Styling constants ---
const LINE_COLOR = '#8b8ad1';
const LINE_WIDTH = 1.5;
const POINT_SIZE = 18;
const CI_TEXT_FONT_SIZE = 14;
const CI_TEXT_COLOR = '#4a5056';
const ZERO_LINE_COLOR = '#BCC0CA';
const ZERO_LINE_WIDTH = 2;
const AXIS_LINE_COLOR = '#989898';
const AXIS_TICK_LABEL_COLOR = '#000';
const X_AXIS_LABEL_TEXT_STYLE = { color: AXIS_TICK_LABEL_COLOR, fontSize: 14 };
const Y_AXIS_LABEL_TEXT_STYLE = { color: AXIS_TICK_LABEL_COLOR, fontSize: 16 };
const TITLE_TEXT_STYLE = { color: '#24334f', fontSize: 14, fontWeight: 'bold' as const };

// --- Layout constants ---
const GRID = { right: 80, containLabel: true };

// Height: ~44px per row + 60px margins; 490px floor
export function computeInitialHeight(rowCount: number): string {
  return `${Math.max(rowCount * 44 + 60, 490)}px`;
}

// Symmetric bounds from max(|ciLeft|, |ciRight|) ×1.1
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

function ciLineSeries(props: ForestPlotProps): CustomSeriesOption {
  const formatCILabel = props.ciLabelFormatter ?? ((value: number) => value.toPrecision(2));

  return {
    type: 'custom',
    clip: false,
    data: props.items.map((item) => ({
      value: [item.ciLeft, item.ciRight, item.yAxisCategory],
      itemStyle: { color: item.color ?? props.defaultLineColor ?? LINE_COLOR },
    })),
    renderItem: (_params, api) => {
      const ciLeft = api.value(0) as number;
      const ciRight = api.value(1) as number;
      const yCategory = api.value(2) as string;
      const [x1, y] = api.coord([ciLeft, yCategory]);
      const [x2] = api.coord([ciRight, yCategory]);
      const color = (api.visual('color') as string) || props.defaultLineColor || LINE_COLOR;

      return {
        type: 'group',
        children: [
          // horizontal CI line
          {
            type: 'line',
            shape: { x1, y1: y, x2, y2: y },
            style: { stroke: color, lineWidth: LINE_WIDTH },
          },
          // optional CI text labels
          ...(props.showCILabels
            ? [
                {
                  type: 'text' as const,
                  style: {
                    text: formatCILabel(ciLeft),
                    x: x1 - 4,
                    y,
                    textAlign: 'right' as const,
                    textVerticalAlign: 'middle' as const,
                    fill: CI_TEXT_COLOR,
                    fontSize: CI_TEXT_FONT_SIZE,
                  },
                },
                {
                  type: 'text' as const,
                  style: {
                    text: formatCILabel(ciRight),
                    x: x2 + 4,
                    y,
                    textAlign: 'left' as const,
                    textVerticalAlign: 'middle' as const,
                    fill: CI_TEXT_COLOR,
                    fontSize: CI_TEXT_FONT_SIZE,
                  },
                },
              ]
            : []),
        ],
      };
    },
    silent: true,
    tooltip: { show: false },
    z: 1,
  };
}

// ECharts custom series requires at least one data point to trigger renderItem. We pass
// [0, referenceCategory] so api.coord([0, referenceCategory]) gives the pixel x-position
// for x=0. Any valid y-axis category works — only the x=0 position matters.
function zeroLineSeries(yAxisCategories: string[]): CustomSeriesOption {
  const referenceCategory = yAxisCategories[0];
  return {
    type: 'custom',
    clip: false,
    itemStyle: { color: 'transparent' },
    renderItem: (params, api) => {
      const [zeroX] = api.coord([0, referenceCategory]);
      const gridBounds = params.coordSys as unknown as {
        x: number;
        y: number;
        width: number;
        height: number;
      };
      return {
        type: 'line',
        shape: { x1: zeroX, y1: gridBounds.y, x2: zeroX, y2: gridBounds.y + gridBounds.height },
        style: { stroke: ZERO_LINE_COLOR, lineWidth: ZERO_LINE_WIDTH, fill: 'none' },
      };
    },
    data: [{ value: [0, referenceCategory] }],
    silent: true,
    z: 0,
    tooltip: { show: false },
  };
}

function dotSeries(props: ForestPlotProps): ScatterSeriesOption {
  return {
    type: 'scatter',
    symbolSize: props.pointSize ?? POINT_SIZE,
    data: props.items.map((item) => ({
      value: [item.value, item.yAxisCategory],
      itemStyle: { color: item.color ?? props.defaultPointColor ?? LINE_COLOR, opacity: 1 },
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

export class ForestPlotChart {
  chart: ECharts | undefined;

  constructor(chartDom: HTMLDivElement | HTMLCanvasElement, props: ForestPlotProps) {
    // Height is set once at construction time. If item count changes after construction
    // (via setOptions), the DOM height will not update. This is intentional for the
    // current use case where item count is fixed.
    this.chart = initChart(chartDom, computeInitialHeight(props.items.length));
    this.setOptions(props);
  }

  destroy() {
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
    const yAxisLabelTooltipFormatter = props.yAxisLabelTooltipFormatter;

    const option: EChartsOption = {
      grid: { ...GRID, top: props.title ? 60 : 20, bottom: props.xAxisTitle ? 50 : 40 },
      xAxis: {
        type: 'value',
        min: xMin,
        max: xMax,
        name: props.xAxisTitle,
        nameLocation: 'middle',
        nameGap: 50,
        nameTextStyle: { ...TITLE_TEXT_STYLE, fontSize: 18 },
        axisLine: { show: true, lineStyle: { width: ZERO_LINE_WIDTH, color: AXIS_LINE_COLOR } },
        splitLine: { show: false },
        axisLabel: {
          ...X_AXIS_LABEL_TEXT_STYLE,
          formatter: (tickValue: number) => tickValue.toFixed(2),
        },
      },
      yAxis: {
        type: 'category',
        data: yAxisCategories,
        inverse: true,
        axisTick: { show: false },
        // Hide the y-axis line; the custom zero line handles the x=0 marker
        axisLine: { show: false },
        triggerEvent: Boolean(yAxisLabelTooltipFormatter),
        tooltip: {
          ...DARK_TOOLTIP,
          show: Boolean(yAxisLabelTooltipFormatter),
          ...(yAxisLabelTooltipFormatter && {
            formatter: (params: CallbackDataParams) => yAxisLabelTooltipFormatter(params.name),
          }),
        },
        axisLabel: { ...Y_AXIS_LABEL_TEXT_STYLE, margin: 30 },
      } as EChartsOption['yAxis'],
      series: [zeroLineSeries(yAxisCategories), ciLineSeries(props), dotSeries(props)],
      tooltip: DARK_TOOLTIP,
      title: props.title
        ? [{ text: props.title, left: 'center', textStyle: TITLE_TEXT_STYLE }]
        : [],
      aria: { enabled: true },
    };

    // notMerge must be set to true to override any existing options set on the chart
    this.chart.setOption(option, true);
  }
}
