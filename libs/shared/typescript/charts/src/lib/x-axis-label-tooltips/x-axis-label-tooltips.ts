import { ECharts } from 'echarts';

/**
 * Manually add tooltip to chart x-axis labels.
 * See https://github.com/apache/echarts/issues/19616 and https://github.com/apache/echarts/issues/11903/
 * Based on https://codepen.io/plainheart/pen/jOGBrmJ.
 */
export class XAxisLabelTooltips {
  chart: ECharts | undefined;
  xAxisCategoryToTooltipText: Record<string, string>;

  constructor(chart: ECharts, xAxisCategoryToTooltipText: Record<string, string>) {
    this.xAxisCategoryToTooltipText = xAxisCategoryToTooltipText;

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
        const fullText = this.xAxisCategoryToTooltipText[e.value as string];
        axisTooltipContent.innerText = fullText;
        axisTooltipStyle.left = currLabel.transform[4] - axisTooltipDOM.offsetWidth / 2 + 'px';
        axisTooltipStyle.top = currLabel.transform[5] - axisTooltipDOM.offsetHeight - 15 + 'px';
        axisTooltipStyle.transform = '';
        axisTooltipStyle.visibility = 'visible';
      })
      .on('mouseout', () => {
        // hide tooltip
        axisTooltipStyle.visibility = 'hidden';
        axisTooltipStyle.transform = 'scale(0)';
      });
  }

  setXAxisCategoryToTooltipText(xAxisCategoryToTooltipText: Record<string, string>) {
    this.xAxisCategoryToTooltipText = xAxisCategoryToTooltipText;
  }
}
