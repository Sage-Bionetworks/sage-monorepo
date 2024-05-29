import * as echarts from 'echarts';
import { ECharts, EChartsOption } from 'echarts';

// Chart must have initial height to be visible
export function ensureChartDomHasHeight(
  chartDom: HTMLDivElement | HTMLCanvasElement
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

// Manually add tooltip, see https://github.com/apache/echarts/issues/19616 and https://github.com/apache/echarts/issues/11903
// Based on https://codepen.io/plainheart/pen/jOGBrmJ
export function addXAxisLabelTooltips(
  xAxisCategoryToTooltipText: Record<string, string>,
  chart: ECharts
) {
  // prepare custom axis tooltip DOM
  const axisTooltipDOM = document.createElement('div');
  const axisTooltipContent = document.createElement('div');
  const axisTooltipArrow = document.createElement('div');
  axisTooltipDOM.appendChild(axisTooltipContent);
  axisTooltipDOM.appendChild(axisTooltipArrow);

  // style tooltip DOM element
  axisTooltipArrow.style.cssText =
    'position: absolute; width: 6px; height: 6px; left: 50%; bottom: -3px; transform: translateX(-50%) rotate(45deg); background-color: #63676C;';
  axisTooltipDOM.style.cssText =
    'position: absolute; visibility: hidden; text-align: center; padding: 10px; font-size: 14px; background-color: #63676C; color: white; z-index: 200; cursor: pointer; border-radius: 4px; pointer-events: none; font-family: sans-serif; opacity: 0.9;';
  const axisTooltipStyle = axisTooltipDOM.style;

  // append to ECharts container
  chart.getDom().appendChild(axisTooltipDOM);

  chart
    .on('mouseover', (e) => {
      if (e['targetType'] !== 'axisLabel' || e['componentType'] !== 'xAxis') {
        return;
      }

      const currLabel = e.event?.target;
      if (!currLabel) return;

      // show tooltip
      const fullText = xAxisCategoryToTooltipText[e.value as string];
      axisTooltipContent.innerText = fullText;
      axisTooltipStyle.left =
        currLabel.transform[4] - axisTooltipDOM.offsetWidth / 2 + 'px';
      axisTooltipStyle.top =
        currLabel.transform[5] - axisTooltipDOM.offsetHeight - 15 + 'px';
      axisTooltipStyle.transform = '';
      axisTooltipStyle.visibility = 'visible';
    })
    .on('mouseout', () => {
      // hide tooltip
      axisTooltipStyle.visibility = 'hidden';
      axisTooltipStyle.transform = 'scale(0)';
    });
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
  chart.setOption(noDataOptions);
}
