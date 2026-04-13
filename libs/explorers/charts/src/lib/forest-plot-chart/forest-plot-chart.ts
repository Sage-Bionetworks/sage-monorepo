import { CustomSeriesOption, ECharts, EChartsOption, ScatterSeriesOption } from 'echarts';
import { CallbackDataParams } from 'echarts/types/dist/shared';
import { ForestPlotProps } from '../models';
import { initChart, setNoDataOption } from '../utils';

// --- Styling constants ---
const LINE_COLOR = '#8b8ad1';
const LINE_WIDTH = 1.5;
const CAP_HEIGHT = 8;
const POINT_SIZE = 18;
const CI_TEXT_FONT_SIZE = 12;
const CI_TEXT_COLOR = '#63676c';
const ZERO_LINE_COLOR = '#BCC0CA';
const ZERO_LINE_WIDTH = 2;

const GRID = { top: 20, right: 80, bottom: 40, containLabel: true };

const TOOLTIP: EChartsOption['tooltip'] = {
  confine: true,
  backgroundColor: '#63676C',
  textStyle: { color: 'white' },
  borderColor: 'transparent',
};

const X_AXIS_LABEL_TEXT_STYLE = { color: '#63676c', fontSize: 12 };
const Y_AXIS_LABEL_TEXT_STYLE = { color: '#63676c', fontSize: 12 };
const TITLE_TEXT_STYLE = { color: '#2a2f35', fontSize: 14, fontWeight: 'bold' as const };

// Height: ~44px per row + 80px margins
export function computeInitialHeight(n: number): string {
  return `${Math.max(n * 44 + 80, 200)}px`;
}

// Symmetric bounds from max(|ciLeft|, |ciRight|) ×1.1
export function computeXBounds(props: ForestPlotProps): [number, number] {
  if (props.xAxisMin !== undefined && props.xAxisMax !== undefined) {
    return [props.xAxisMin, props.xAxisMax];
  }
  const maxAbs = Math.max(...props.items.flatMap((i) => [Math.abs(i.ciLeft), Math.abs(i.ciRight)]));
  const bound = maxAbs * 1.1;
  return [-bound, bound];
}

function ciLineSeries(props: ForestPlotProps): CustomSeriesOption {
  const fmt = props.ciLabelFormatter ?? ((v: number) => v.toPrecision(2));

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
      const halfCap = CAP_HEIGHT / 2;

      return {
        type: 'group',
        children: [
          // horizontal CI line
          {
            type: 'line',
            shape: { x1, y1: y, x2, y2: y },
            style: { stroke: color, lineWidth: LINE_WIDTH },
          },
          // left end cap
          {
            type: 'line',
            shape: { x1, y1: y - halfCap, x2: x1, y2: y + halfCap },
            style: { stroke: color, lineWidth: LINE_WIDTH },
          },
          // right end cap
          {
            type: 'line',
            shape: { x1: x2, y1: y - halfCap, x2, y2: y + halfCap },
            style: { stroke: color, lineWidth: LINE_WIDTH },
          },
          // optional CI text labels
          ...(props.showCILabels
            ? [
                {
                  type: 'text' as const,
                  style: {
                    text: fmt(ciLeft),
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
                    text: fmt(ciRight),
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
    tooltip: { show: false },
    z: 1,
  };
}

function dotSeries(props: ForestPlotProps): ScatterSeriesOption {
  return {
    type: 'scatter',
    symbolSize: props.pointSize ?? POINT_SIZE,
    data: props.items.map((item) => ({
      value: [item.value, item.yAxisCategory],
      itemStyle: { color: item.color ?? props.defaultPointColor ?? LINE_COLOR },
    })),
    tooltip: {
      formatter: (params) => {
        const p = params as CallbackDataParams;
        const item = props.items[p.dataIndex];
        return props.pointTooltipFormatter
          ? props.pointTooltipFormatter(item, p)
          : `Value: ${item.value}`;
      },
    },
    markLine: {
      symbol: 'none',
      silent: true,
      data: [{ xAxis: 0 }],
      lineStyle: { color: ZERO_LINE_COLOR, width: ZERO_LINE_WIDTH, type: 'solid' },
      label: { show: false },
    },
    z: 2,
  };
}

export class ForestPlotChart {
  chart: ECharts | undefined;

  constructor(chartDom: HTMLDivElement | HTMLCanvasElement, props: ForestPlotProps) {
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
      ...new Set(props.items.map((i) => i.yAxisCategory)),
    ];
    const [xMin, xMax] = computeXBounds(props);
    const yAxisLabelTooltipFormatter = props.yAxisLabelTooltipFormatter;

    const option: EChartsOption = {
      grid: { ...GRID, top: props.title ? 60 : 20 },
      xAxis: {
        type: 'value',
        min: xMin,
        max: xMax,
        name: props.xAxisTitle,
        nameLocation: 'middle',
        nameGap: 30,
        splitLine: props.showGridLines
          ? { show: true, lineStyle: { color: '#e0e0e0' } }
          : { show: false },
        axisLabel: X_AXIS_LABEL_TEXT_STYLE,
      },
      yAxis: {
        type: 'category',
        data: yAxisCategories,
        inverse: true,
        triggerEvent: Boolean(yAxisLabelTooltipFormatter),
        tooltip: {
          show: Boolean(yAxisLabelTooltipFormatter),
          ...(yAxisLabelTooltipFormatter && {
            formatter: (params: CallbackDataParams) => yAxisLabelTooltipFormatter(params.name),
            extraCssText: 'border: unset; opacity: 0.9; background-color: #63676c',
          }),
        },
        axisLabel: Y_AXIS_LABEL_TEXT_STYLE,
      } as EChartsOption['yAxis'],
      series: [ciLineSeries(props), dotSeries(props)],
      tooltip: TOOLTIP,
      title: props.title
        ? [{ text: props.title, left: 'center', textStyle: TITLE_TEXT_STYLE }]
        : [],
      aria: { enabled: true },
    };

    // notMerge must be set to true to override any existing options set on the chart
    this.chart.setOption(option, true);
  }
}
