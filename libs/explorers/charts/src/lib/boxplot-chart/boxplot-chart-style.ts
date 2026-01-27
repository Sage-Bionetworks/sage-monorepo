import type { SeriesOption } from 'echarts';
import {
  DEFAULT_POINT_SIZE,
  GRAY_BACKGROUND_COLOR,
  Y_AXIS_TICK_LABELS_MAX_WIDTH,
} from '../constants';
import { BoxplotChartStyle } from '../models/boxplot';

export const minimalBoxplotChartStyle: BoxplotChartStyle = {
  boxplotItemStyle: {
    borderColor: '#bcc0ca',
    borderWidth: 2,
    color: 'transparent',
  },
  titleTextStyle: {
    fontWeight: 700,
    fontSize: '18px',
    color: 'black',
  },
  xAxisLabelTextStyle: {
    color: 'black',
    fontWeight: 'bold',
    fontSize: '14px',
  },
  yAxisTitleTextStyle: {
    fontWeight: 700,
    fontSize: '18px',
    color: 'black',
  },
  yAxisSplitLine: { show: false },
  yAxisTickLabelMaxWidth: Y_AXIS_TICK_LABELS_MAX_WIDTH,
  pointSymbolSize: DEFAULT_POINT_SIZE,
  tooltip: {
    confine: true,
    position: 'top',
    backgroundColor: '#63676C',
    borderColor: 'transparent',
    textStyle: {
      color: 'white',
    },
    extraCssText:
      'opacity: 0.9; width: auto; max-width: 300px; white-space: pre-wrap; text-align: center;',
  },
  grid: {
    // The total left margin for the y-axis. Use a hardcoded value to ensure y-axis labels and plot areas
    // are aligned across multiple charts. ECharts' `containLabel: true` will shift the plot area if tick
    // labels or axis names change, so it can't guarantee cross-chart alignment.
    left: Y_AXIS_TICK_LABELS_MAX_WIDTH + 18,
    right: 20,
    containLabel: false,
  },
};

export const grayGridBoxplotChartStyle: BoxplotChartStyle = {
  ...minimalBoxplotChartStyle,
  getBoxplotMarkArea: (xAxisCategories: string[]) => {
    const markArea: SeriesOption['markArea'] = {
      itemStyle: { color: GRAY_BACKGROUND_COLOR },
      data: xAxisCategories.map((_, idx) => {
        const spacing = 0.4;
        const pcIndex = idx + 1;
        return [{ xAxis: pcIndex - spacing }, { xAxis: pcIndex + spacing }];
      }),
    };
    return markArea;
  },
  xAxisLabelTextStyle: {
    ...minimalBoxplotChartStyle.xAxisLabelTextStyle,
    fontWeight: 'normal',
  },
  yAxisTitleTextStyle: {
    ...minimalBoxplotChartStyle.yAxisTitleTextStyle,
    fontWeight: 400,
    fontSize: '14px',
  },
  yAxisSplitLine: { show: true },
  pointSymbolSize: DEFAULT_POINT_SIZE / 2,
  tooltip: {
    ...minimalBoxplotChartStyle.tooltip,
    backgroundColor: 'white',
    borderRadius: 0,
    textStyle: {
      color: '#22252A',
    },
  },
  grid: {
    ...minimalBoxplotChartStyle.grid,
    left: minimalBoxplotChartStyle.grid.left - 3, // ensure y-axis title with smaller font is still at the leftmost edge of the plot
  },
};
