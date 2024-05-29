import {
  DatasetComponentOption,
  ECharts,
  EChartsOption,
  SeriesOption,
} from 'echarts';
import { BoxplotProps, CategoryPoint } from '../models';
import {
  addXAxisValueToBoxplotSummaries,
  addXAxisValueToCategoryPoint,
  formatCategoryPointsForBoxplotTransform,
  getCategoryPointColor,
  getCategoryPointShape,
  getUniqueValues,
  initChart,
  setNoDataOption,
} from '../utils';
import { XAxisLabelTooltips } from '../x-axis-label-tooltips';

const titleTextStyle = {
  fontWeight: 700,
  fontSize: '18px',
  color: 'black',
};

const boxplotStyle = {
  borderColor: '#bcc0ca',
  borderWidth: 2,
};

const yAxisPadding = 0.2;
const defaultPointShape = 'circle';
const defaultPointSize = 18;
const defaultPointColor = '#8b8ad1';

export class BoxplotChart {
  chart: ECharts | undefined;
  xAxisLabelTooltips: XAxisLabelTooltips | undefined;

  constructor(
    chartDom: HTMLDivElement | HTMLCanvasElement,
    boxplotProps: BoxplotProps
  ) {
    this.chart = initChart(chartDom);
    this.setOptions(boxplotProps);
  }

  destroy() {
    this.chart?.dispose();
  }

  setXAxisLabelTooltips(xAxisCategoryToTooltipText?: Record<string, string>) {
    if (!xAxisCategoryToTooltipText || !this.chart) return;
    if (!this.xAxisLabelTooltips) {
      this.xAxisLabelTooltips = new XAxisLabelTooltips(
        this.chart,
        xAxisCategoryToTooltipText
      );
    }
    this.xAxisLabelTooltips.setXAxisCategoryToTooltipText(
      xAxisCategoryToTooltipText
    );
  }

  setOptions(boxplotProps: BoxplotProps) {
    if (!this.chart) return;

    const {
      points,
      summaries,
      title,
      xAxisTitle,
      yAxisTitle,
      yAxisMin,
      yAxisMax,
      xAxisCategoryToTooltipText,
      pointTooltipFormatter,
    } = boxplotProps;

    if (points.length === 0) {
      setNoDataOption(this.chart);
      return;
    }

    const xAxisCategories = getUniqueValues(
      points,
      'xAxisCategory'
    ) as string[];
    const pointCategories = getUniqueValues(
      points,
      'pointCategory'
    ) as string[];
    const hasPointCategories = pointCategories.length > 0;

    const dataForScatterPoints = addXAxisValueToCategoryPoint(
      points,
      xAxisCategories,
      pointCategories,
      0.05
    );
    const dataForStaticBoxplotSummaries = summaries
      ? addXAxisValueToBoxplotSummaries(summaries, xAxisCategories)
      : undefined;
    const dataForDynamicBoxplots = formatCategoryPointsForBoxplotTransform(
      dataForScatterPoints,
      xAxisCategories
    );

    const datasetOpts: DatasetComponentOption[] = [
      // 0: static boxplots
      {
        dimensions: dataForStaticBoxplotSummaries
          ? Object.keys(dataForStaticBoxplotSummaries[0])
          : undefined,
        source: dataForStaticBoxplotSummaries,
      },
      // 1: points
      {
        dimensions: Object.keys(dataForScatterPoints[0]),
        source: dataForScatterPoints,
      },
      // 2: points data formatted for boxplot transform
      { source: dataForDynamicBoxplots },
      // 3: dynamic boxplot data
      {
        fromDatasetIndex: 2,
        transform: {
          type: 'boxplot',
        },
      },
    ];

    const seriesOpts: SeriesOption[] = [];
    if (summaries) {
      // static boxplots
      seriesOpts.push({
        type: 'boxplot',
        datasetIndex: 0,
        encode: {
          x: 'xAxisValue',
          y: ['min', 'firstQuartile', 'median', 'thirdQuartile', 'max'],
        },
        z: 1, // ensure that boxplot is shown beneath points
        itemStyle: boxplotStyle,
        silent: true,
      });
    } else {
      // dynamic boxplots
      seriesOpts.push({
        type: 'boxplot',
        datasetIndex: 3,
        tooltip: {
          show: false,
        },
        z: 1, // ensure that boxplot is shown beneath points
        itemStyle: boxplotStyle,
        silent: true,
      });
    }

    // points
    seriesOpts.push({
      type: 'scatter',
      datasetIndex: 1,
      encode: {
        x: 'xAxisValue',
        y: 'value',
      },
      symbolSize: defaultPointSize,
      symbol: (point: CategoryPoint) => {
        return hasPointCategories
          ? getCategoryPointShape(point, pointCategories)
          : defaultPointShape;
      },
      itemStyle: {
        color: (params) => {
          return hasPointCategories
            ? getCategoryPointColor(
                params.value as CategoryPoint,
                pointCategories
              )
            : defaultPointColor;
        },
      },
      tooltip: {
        formatter: (param) => {
          if (pointTooltipFormatter) {
            return pointTooltipFormatter(param.data as CategoryPoint);
          }
          const pt = param.data as CategoryPoint;
          return `${pt.value}`;
        },
      },
    });

    const option: EChartsOption = {
      title: [
        {
          text: title,
          left: 'center',
          textStyle: titleTextStyle,
        },
        // Add x-axis title as a title rather than xAxis.name, because
        // setting via xAxis.name causes cursor to change to pointer when
        // x-axis label tooltips are used
        {
          text: xAxisTitle,
          textStyle: titleTextStyle,
          left: 'center',
          top: 'bottom',
        },
      ],
      aria: {
        enabled: true,
      },
      dataset: datasetOpts,
      // Use two xAxes:
      //  - value: used to jitter points with multiple pointCategories, where
      //           xAxisCategory is mapped to 1-based index values for both
      //           boxplots and points. Axis is not displayed.
      //  - category: used to display the xAxisCategory and allows xAxis tooltips
      //           to be displayed, since the event will contain the xAxis label
      //           value, not the displayed text.
      xAxis: [
        {
          type: 'value',
          axisLine: {
            onZero: false,
          },
          // Specify min/max values so the value xAxis aligns with the category xAxis
          min: 0.5,
          max: function (value) {
            return Math.round((value.max + 0.5) * 2) / 2;
          },
          axisTick: { show: false },
          splitLine: { show: false },
          axisLabel: {
            showMinLabel: false,
            showMaxLabel: false,
            show: false,
          },
        },
        {
          type: 'category',
          data: xAxisCategories,
          axisLabel: {
            color: 'black',
            fontWeight: 'bold',
            fontSize: '14px',
          },
          axisTick: {
            alignWithLabel: true,
          },
          axisLine: {
            onZero: false,
          },
          triggerEvent: Boolean(xAxisCategoryToTooltipText),
          position: 'bottom',
        },
      ],
      yAxis: {
        type: 'value',
        name: yAxisTitle,
        nameLocation: 'middle',
        nameGap: 50,
        nameTextStyle: titleTextStyle,
        axisLine: {
          show: true,
        },
        splitLine: {
          show: false,
        },
        min: yAxisMin ? yAxisMin - yAxisPadding : undefined,
        max: yAxisMax ? yAxisMax + yAxisPadding : undefined,
      },
      tooltip: {
        position: 'top',
        backgroundColor: '#63676C',
        borderColor: 'none',
        textStyle: {
          color: 'white',
        },
        extraCssText: 'opacity: 0.9',
      },
      series: seriesOpts,
    };

    this.chart.setOption(option);
    this.setXAxisLabelTooltips(xAxisCategoryToTooltipText);
  }
}
