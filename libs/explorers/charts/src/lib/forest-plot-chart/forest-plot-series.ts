import { CustomSeriesOption, EChartsOption, ScatterSeriesOption } from 'echarts';
import { DEFAULT_COLOR, DEFAULT_POINT_SIZE } from '../constants';
import { ForestPlotProps } from '../models';
import { GridLineStyle } from '../models/axis';
import { CI_LINE_WIDTH } from '../point-plot-defaults';
import { GridCoordSys } from '../types';

const CI_LABEL_GAP = 4;
const CI_LABEL_STYLE = { fill: '#4a5056', fontSize: 14, textVerticalAlign: 'middle' };

const ZERO_LINE_STYLE = { stroke: '#BCC0CA', lineWidth: 2, fill: 'none' };

export function resolveItemColor(
  itemColor: string | undefined,
  propDefault: string | undefined,
): string {
  return itemColor || propDefault || DEFAULT_COLOR;
}

export function ciLineSeries(props: ForestPlotProps): CustomSeriesOption {
  const formatCILabel = props.ciLabelFormatter ?? ((value: number) => value.toPrecision(2));
  const ciLineWidth = props.ciLineWidth ?? CI_LINE_WIDTH;

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
            style: { stroke: color, lineWidth: ciLineWidth },
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
// for x=0. Any valid y-axis category works -- only the x=0 position matters.
export function zeroLineSeries(yAxisCategories: string[]): CustomSeriesOption {
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

export function dotSeries(props: ForestPlotProps): ScatterSeriesOption {
  return {
    type: 'scatter',
    symbolSize: props.pointSize ?? DEFAULT_POINT_SIZE,
    data: props.items.map((item) => ({
      value: [item.value, item.yAxisCategory],
      itemStyle: { color: resolveItemColor(item.color, props.defaultPointColor), opacity: 1 },
    })),
    tooltip: {
      formatter: (params) => {
        const dataIndex = Array.isArray(params) ? params[0].dataIndex : params.dataIndex;
        const item = props.items[dataIndex];
        return props.pointTooltipFormatter
          ? props.pointTooltipFormatter(item)
          : `Value: ${item.value}`;
      },
    },
    z: 2,
  };
}

// Translates a GridLineStyle.type into a ZRender lineDash array. ECharts itself accepts
// the friendly `type: 'solid' | 'dashed' | 'dotted'` enum on axis splitLine config, but
// gridLineSeries renders shapes directly via ZRender (the underlying drawing layer),
// which only accepts numeric [dashLengthPx, gapLengthPx] patterns. The lengths here are
// tuned for ~1px stroke widths; if grid lines get thicker, these may need to grow too.
function lineDashFor(type: GridLineStyle['type']): number[] | undefined {
  switch (type) {
    case 'dotted':
      return [2, 4];
    case 'dashed':
      return [6, 4];
    case 'solid':
    default:
      // ECharts treats absent lineDash as a continuous solid stroke.
      return undefined;
  }
}

// Renders horizontal grid lines at each y-axis category position and vertical grid
// lines at each interior x-axis tick. Using a custom series (instead of axis splitLine)
// gives us three things axis splitLine can't: (1) per-category alignment on a category
// y-axis (axis splitLines fall between categories), (2) suppression of edge lines at
// xMin/xMax, and (3) z-ordering below series without dragging the axis line down with it.
export function gridLineSeries(
  yAxisCategories: string[],
  xMin: number,
  xMax: number,
  xTickPositions: number[],
  xAxisGridLineStyle: GridLineStyle | undefined,
  yAxisGridLineStyle: GridLineStyle | undefined,
): CustomSeriesOption {
  const referenceCategory = yAxisCategories[0];
  // Skip any tick positions that land exactly on the plot edges; the axis lines
  // already cover those.
  const interiorXTicks = xTickPositions.filter((v) => v > xMin && v < xMax);
  return {
    type: 'custom',
    clip: false,
    silent: true,
    tooltip: { show: false },
    renderItem: (params, api) => {
      const grid = params.coordSys as unknown as GridCoordSys;
      // unknown[] keeps the inner shapes loose; ECharts' CustomElementOption union is
      // narrower than what we need and forces an assertion on the return.
      const children: unknown[] = [];

      if (yAxisGridLineStyle) {
        const yDash = lineDashFor(yAxisGridLineStyle.type);
        yAxisCategories.forEach((cat) => {
          const [, y] = api.coord([xMin, cat]);
          children.push({
            type: 'line',
            shape: { x1: grid.x, y1: y, x2: grid.x + grid.width, y2: y },
            style: {
              stroke: yAxisGridLineStyle.color,
              lineWidth: yAxisGridLineStyle.width,
              ...(yDash && { lineDash: yDash }),
            },
          });
        });
      }

      if (xAxisGridLineStyle) {
        const xDash = lineDashFor(xAxisGridLineStyle.type);
        interiorXTicks.forEach((xValue) => {
          const [x] = api.coord([xValue, referenceCategory]);
          children.push({
            type: 'line',
            shape: { x1: x, y1: grid.y, x2: x, y2: grid.y + grid.height },
            style: {
              stroke: xAxisGridLineStyle.color,
              lineWidth: xAxisGridLineStyle.width,
              ...(xDash && { lineDash: xDash }),
            },
          });
        });
      }

      return { type: 'group', children } as ReturnType<
        NonNullable<CustomSeriesOption['renderItem']>
      >;
    },
    data: [{ value: [xMin, referenceCategory] }],
    z: 0,
  };
}

export function buildSeries(
  props: ForestPlotProps,
  yAxisCategories: string[],
  xMin: number,
  xMax: number,
  xTickPositions: number[],
): EChartsOption['series'] {
  return [
    ...(props.xAxisGridLineStyle || props.yAxisGridLineStyle
      ? [
          gridLineSeries(
            yAxisCategories,
            xMin,
            xMax,
            xTickPositions,
            props.xAxisGridLineStyle,
            props.yAxisGridLineStyle,
          ),
        ]
      : []),
    ...((props.showZeroLine ?? true) ? [zeroLineSeries(yAxisCategories)] : []),
    ciLineSeries(props),
    dotSeries(props),
  ];
}
