import { CustomSeriesOption, ECharts, EChartsOption, ScatterSeriesOption } from 'echarts';
import { CallbackDataParams } from 'echarts/types/dist/shared';
import { DARK_TOOLTIP, DEFAULT_COLOR, DEFAULT_POINT_SIZE } from '../constants';
import { ForestPlotProps } from '../models';
import { AXIS_STYLE, CI_LINE_WIDTH, GRID_TOP, TEXT_STYLE } from '../point-plot-defaults';
import { GridCoordSys } from '../types';
import { initChart, setNoDataOption } from '../utils';

const CI_LABEL_GAP = 4;
const CI_LABEL_STYLE = { fill: '#4a5056', fontSize: 14, textVerticalAlign: 'middle' };

const ZERO_LINE_STYLE = { stroke: '#BCC0CA', lineWidth: 2, fill: 'none' };

const X_AXIS_LABEL_TEXT_STYLE = { color: AXIS_STYLE.tickLabelColor, fontSize: 14 };
const Y_AXIS_LABEL_TEXT_STYLE = { color: AXIS_STYLE.tickLabelColor, fontSize: 16 };

const GRID_BOTTOM_WITHOUT_X_AXIS_TITLE = 40;
const GRID_DEFAULT_MARGINS = GRID_TOP.withoutTitle + GRID_BOTTOM_WITHOUT_X_AXIS_TITLE;

const GRID = { right: 80, containLabel: true };

export function resolveItemColor(
  itemColor: string | undefined,
  propDefault: string | undefined,
): string {
  return itemColor || propDefault || DEFAULT_COLOR;
}

export function computeInitialHeight(rowCount: number): string {
  // ~44px per row + GRID_DEFAULT_MARGINS; 490px floor
  return `${Math.max(rowCount * 44 + GRID_DEFAULT_MARGINS, 490)}px`;
}

// Symmetric bounds from max(|ciLeft|, |ciRight|) x1.1
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
      itemStyle: { color: resolveItemColor(item.color, props.defaultLineColor) },
    })),
    renderItem: (_params, api) => {
      const ciLeft = api.value(0) as number;
      const ciRight = api.value(1) as number;
      const yCategory = api.value(2) as string;
      const [x1, y] = api.coord([ciLeft, yCategory]);
      const [x2] = api.coord([ciRight, yCategory]);
      const color = api.visual('color') as string;

      return {
        type: 'group',
        children: [
          // horizontal CI line
          {
            type: 'line',
            shape: { x1, y1: y, x2, y2: y },
            style: { stroke: color, lineWidth: CI_LINE_WIDTH },
          },
          // optional CI text labels
          ...(props.showCILabels
            ? [
                {
                  type: 'text' as const,
                  style: {
                    ...CI_LABEL_STYLE,
                    text: formatCILabel(ciLeft),
                    x: x1 - CI_LABEL_GAP,
                    y,
                    textAlign: 'right',
                  },
                },
                {
                  type: 'text' as const,
                  style: {
                    ...CI_LABEL_STYLE,
                    text: formatCILabel(ciRight),
                    x: x2 + CI_LABEL_GAP,
                    y,
                    textAlign: 'left',
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
      const gridBounds = params.coordSys as unknown as GridCoordSys;
      return {
        type: 'line',
        shape: { x1: zeroX, y1: gridBounds.y, x2: zeroX, y2: gridBounds.y + gridBounds.height },
        style: ZERO_LINE_STYLE,
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
    symbolSize: props.pointSize ?? DEFAULT_POINT_SIZE,
    data: props.items.map((item) => ({
      value: [item.value, item.yAxisCategory],
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

export class ForestPlotChart {
  chart: ECharts | undefined;

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
      grid: {
        ...GRID,
        top: props.title ? GRID_TOP.withTitle : GRID_TOP.withoutTitle,
        bottom: props.xAxisTitle ? AXIS_STYLE.titleGap : GRID_BOTTOM_WITHOUT_X_AXIS_TITLE,
      },
      xAxis: {
        type: 'value',
        min: xMin,
        max: xMax,
        name: props.xAxisTitle,
        nameLocation: 'middle',
        nameGap: AXIS_STYLE.titleGap,
        nameTextStyle: TEXT_STYLE.axisName,
        axisLine: {
          show: true,
          lineStyle: AXIS_STYLE.line,
        },
        splitLine: { show: false },
        splitNumber: AXIS_STYLE.valueAxisSplitNumber,
        axisLabel: {
          ...X_AXIS_LABEL_TEXT_STYLE,
          formatter: (tickValue: number) => tickValue.toFixed(2),
          showMinLabel: false,
          showMaxLabel: false,
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
        ? [{ text: props.title, left: 'center', textStyle: TEXT_STYLE.title }]
        : [],
      aria: { enabled: true },
    };

    // notMerge must be set to true to override any existing options set on the chart
    this.chart.setOption(option, true);
  }
}
