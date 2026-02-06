import { ECharts, EChartsOption } from 'echarts';
import { grayGridBaseChartTheme, minimalBaseChartTheme } from '../chart-theme/base-theme';
import { LegendProps } from '../models';
import { initChart } from '../utils';

export class LegendChart {
  private readonly INITIAL_CHART_HEIGHT = '25px';
  chart: ECharts | undefined;

  constructor(chartDom: HTMLDivElement | HTMLCanvasElement, legendProps: LegendProps) {
    this.chart = initChart(chartDom, this.INITIAL_CHART_HEIGHT);
    this.setOptions(legendProps);
  }

  destroy() {
    this.chart?.dispose();
  }

  setOptions(legendProps: LegendProps) {
    if (!this.chart) return;

    const { pointStyles, chartStyle } = legendProps;

    const chartTheme = chartStyle === 'grayGrid' ? grayGridBaseChartTheme : minimalBaseChartTheme;

    const option: EChartsOption = {
      legend: {
        data: pointStyles.map((item) => item.label),
        orient: 'horizontal',
        left: 'left',
        top: 'bottom',
        itemHeight: chartTheme.pointSymbolSize,
        itemWidth: chartTheme.pointSymbolSize,
        selectedMode: false,
      },
      xAxis: { show: false },
      yAxis: { show: false },
      series: pointStyles.map((item) => ({
        name: item.label,
        type: 'scatter',
        symbol: item.shape,
        data: [[0, 0]], // single dummy point
        itemStyle: {
          color: item.color,
          opacity: item.opacity,
        },
        symbolSize: chartTheme.pointSymbolSize,
      })),
    };

    // notMerge must be set to true to override any existing options set on the chart
    this.chart.setOption(option, true);
  }
}
