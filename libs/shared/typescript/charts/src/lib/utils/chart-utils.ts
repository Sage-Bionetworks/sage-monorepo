import * as echarts from 'echarts';
import { ECharts, EChartsOption } from 'echarts';

// Chart must have initial height to be visible
export function ensureChartDomHasHeight(
  chartDom: HTMLDivElement | HTMLCanvasElement,
) {
  const computedHeight = window
    .getComputedStyle(chartDom, null)
    .getPropertyValue('height');
  const inlineHeight = chartDom.style.height;
  if (
    !computedHeight ||
    computedHeight === '0px' ||
    !inlineHeight ||
    inlineHeight === '0px'
  ) {
    chartDom.style.height = '350px';
  }
}

export function initChart(chartDom: HTMLDivElement | HTMLCanvasElement) {
  ensureChartDomHasHeight(chartDom);
  const chart = echarts.init(chartDom);
  resizeChartOnWindowResize(chart);
  return chart;
}

// ensure chart resizes -- may need to update to use ResizeObserver to handle non-window resize events
// See ngx-echarts: https://github.com/xieziyu/ngx-echarts/blob/master/projects/ngx-echarts/src/lib/ngx-echarts.directive.ts
export function resizeChartOnWindowResize(chart: ECharts) {
  window.onresize = function () {
    chart.resize();
  };
}

export function setNoDataOption(chart: ECharts) {
  const noDataOptions: EChartsOption = {
    title: {
      text: 'No data is currently available.',
      left: 'center',
      top: 'middle',
      textStyle: {
        color: 'rgb(174, 181, 188)',
        fontStyle: 'italic',
        fontWeight: 400,
        fontSize: 18,
      },
    },
    aria: {
      enabled: true,
      label: {
        description: 'No data is currently available.',
      },
    },
  };

  // notMerge must be set to true to override any existing options set on the chart
  chart.setOption(noDataOptions, true);
}
