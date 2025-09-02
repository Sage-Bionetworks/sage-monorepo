import { DatasetComponentOption, ECharts, EChartsOption, SeriesOption } from 'echarts';
import { BoxplotProps, CategoryPoint } from '../models';
import {
  addXAxisValueToBoxplotSummaries,
  addXAxisValueToCategoryPoint,
  formatCategoryPointsForBoxplotTransform,
  getCategoryPointColor,
  getCategoryPointShape,
  getCategoryPointStyle,
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
const defaultPointOpacity = 0.8;

const Y_AXIS_TICK_LABELS_MAX_WIDTH = 80;
const SPACE_FOR_Y_AXIS_NAME = 40;

export class BoxplotChart {
  chart: ECharts | undefined;
  xAxisLabelTooltips: XAxisLabelTooltips | undefined;

  constructor(chartDom: HTMLDivElement | HTMLCanvasElement, boxplotProps: BoxplotProps) {
    this.chart = initChart(chartDom);
    this.setOptions(boxplotProps);
  }

  destroy() {
    this.chart?.dispose();
  }

  setXAxisLabelTooltips(xAxisCategoryToTooltipText?: Record<string, string>) {
    if (!xAxisCategoryToTooltipText || !this.chart) return;
    if (!this.xAxisLabelTooltips) {
      this.xAxisLabelTooltips = new XAxisLabelTooltips(this.chart, xAxisCategoryToTooltipText);
    }
    this.xAxisLabelTooltips.setXAxisCategoryToTooltipText(xAxisCategoryToTooltipText);
  }

  setOptions(boxplotProps: BoxplotProps) {
    if (!this.chart) return;

    const {
      points,
      summaries,
      title,
      xAxisTitle,
      xAxisLabelFormatter,
      yAxisTitle,
      yAxisMin,
      yAxisMax,
      xAxisCategoryToTooltipText,
      pointTooltipFormatter,
      pointCategoryColors,
      pointCategoryShapes,
      pointOpacity,
    } = boxplotProps;

    const showLegend = boxplotProps.showLegend || false;
    const noDataStyle = boxplotProps.noDataStyle || 'textOnly';

    const noPoints = points.length === 0;
    const noSummaries = summaries == null || summaries.length === 0;

    if (noPoints && noSummaries) {
      setNoDataOption(this.chart, noDataStyle);
      return;
    }

    const xAxisCategories =
      boxplotProps.xAxisCategories ??
      (noPoints && summaries
        ? (getUniqueValues(summaries, 'xAxisCategory') as string[])
        : (getUniqueValues(points, 'xAxisCategory') as string[]));
    const pointCategories = getUniqueValues(points, 'pointCategory') as string[];
    const hasPointCategories = pointCategories.length > 0;

    const dataForScatterPoints = addXAxisValueToCategoryPoint(
      points,
      xAxisCategories,
      pointCategories,
      0.1,
      0.02,
    );
    const dataForStaticBoxplotSummaries = summaries
      ? addXAxisValueToBoxplotSummaries(summaries, xAxisCategories)
      : undefined;
    const dataForDynamicBoxplotTransforms = formatCategoryPointsForBoxplotTransform(
      dataForScatterPoints,
      xAxisCategories,
    );

    const basePointsDatasetId = 'points';
    const datasetOpts: DatasetComponentOption[] = [
      {
        id: 'static-boxplot-summaries',
        dimensions: dataForStaticBoxplotSummaries
          ? Object.keys(dataForStaticBoxplotSummaries[0])
          : undefined,
        source: dataForStaticBoxplotSummaries,
      },
      {
        id: basePointsDatasetId,
        dimensions: noPoints ? undefined : Object.keys(dataForScatterPoints[0]),
        source: dataForScatterPoints,
      },
      {
        id: 'points-formatted-for-boxplot-transform',
        source: dataForDynamicBoxplotTransforms,
      },
      {
        id: 'dynamic-boxplot-summaries',
        fromDatasetId: 'points-formatted-for-boxplot-transform',
        transform: {
          type: 'boxplot',
        },
      },
    ];

    // points dataset for each pointCategory
    const pointDatasetIds = hasPointCategories ? pointCategories : [basePointsDatasetId];
    if (hasPointCategories) {
      pointCategories.forEach((pointCategory) => {
        datasetOpts.push({
          id: pointCategory,
          fromDatasetId: basePointsDatasetId,
          transform: {
            type: 'filter',
            config: {
              dimension: 'pointCategory',
              value: pointCategory,
            },
          },
        });
      });
    }

    const seriesOpts: SeriesOption[] = [];

    // boxplots
    const boxplotSeriesBase: SeriesOption = {
      type: 'boxplot',
      z: 1,
      itemStyle: boxplotStyle,
      boxWidth: [7, 115],
      silent: true,
      tooltip: {
        show: false,
      },
    };
    if (summaries) {
      seriesOpts.push({
        ...boxplotSeriesBase,
        datasetId: 'static-boxplot-summaries',
        xAxisId: 'value-x-axis',
        encode: {
          x: 'xAxisValue',
          y: ['min', 'firstQuartile', 'median', 'thirdQuartile', 'max'],
        },
      });
    } else {
      seriesOpts.push({
        ...boxplotSeriesBase,
        datasetId: 'dynamic-boxplot-summaries',
        xAxisId: 'value-x-axis',
      });
    }

    // points series for each points dataset
    pointDatasetIds.forEach((id) => {
      seriesOpts.push({
        type: 'scatter',
        datasetId: id,
        name: id,
        xAxisId: 'value-x-axis',
        encode: {
          x: 'xAxisValue',
          y: 'value',
        },
        symbolSize: defaultPointSize,
        symbol:
          id === 'points'
            ? defaultPointShape
            : getCategoryPointStyle(
                id,
                hasPointCategories,
                pointCategoryShapes,
                getCategoryPointShape,
                pointCategories,
                defaultPointShape,
              ),
        itemStyle: {
          color:
            id === 'points'
              ? defaultPointColor
              : getCategoryPointStyle(
                  id,
                  hasPointCategories,
                  pointCategoryColors,
                  getCategoryPointColor,
                  pointCategories,
                  defaultPointColor,
                ),
          opacity: pointOpacity || defaultPointOpacity,
        },
        tooltip: {
          formatter: (params) => {
            if (pointTooltipFormatter) {
              return pointTooltipFormatter(params.data as CategoryPoint, params);
            }
            const pt = params.data as CategoryPoint;
            return `${pt.value}`;
          },
        },
      });
    });

    const titles = [];
    if (xAxisTitle) {
      titles.push(
        // Add x-axis title as a title rather than xAxis.name, because
        // setting via xAxis.name causes cursor to change to pointer when
        // x-axis label tooltips are used
        {
          text: xAxisTitle,
          textStyle: titleTextStyle,
          left: 'center',
          top: 'bottom',
        },
      );
    }
    if (title) {
      titles.push({
        text: title,
        left: 'center',
        top: 'top',
        textStyle: titleTextStyle,
      });
    }

    const option: EChartsOption = {
      legend: {
        show: showLegend,
        data: pointDatasetIds,
        orient: 'horizontal',
        left: 'left',
        top: 'bottom',
        itemHeight: defaultPointSize,
        itemWidth: defaultPointSize,
        selectedMode: false,
      },
      grid: {
        top: title ? 60 : 20,
        left: Y_AXIS_TICK_LABELS_MAX_WIDTH + SPACE_FOR_Y_AXIS_NAME,
        right: 20,
        containLabel: false,
      },
      title: titles,
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
          id: 'value-x-axis',
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
          id: 'category-x-axis',
          type: 'category',
          data: xAxisCategories,
          axisLabel: {
            color: 'black',
            fontWeight: 'bold',
            fontSize: '14px',
            interval: 0, // ensure all labels are shown
            formatter: (value) => {
              if (xAxisLabelFormatter) {
                return xAxisLabelFormatter(value);
              }
              return value;
            },
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
        nameGap: Y_AXIS_TICK_LABELS_MAX_WIDTH,
        nameTextStyle: titleTextStyle,
        axisLine: {
          show: true,
        },
        axisLabel: {
          width: Y_AXIS_TICK_LABELS_MAX_WIDTH,
          hideOverlap: true,
          showMinLabel: yAxisMin == null,
          showMaxLabel: yAxisMax == null,
        },
        splitLine: {
          show: false,
        },
        min: yAxisMin ? yAxisMin - yAxisPadding : undefined,
        max: yAxisMax ? yAxisMax + yAxisPadding : undefined,
      },
      tooltip: {
        confine: true,
        position: 'top',
        backgroundColor: '#63676C',
        borderColor: 'none',
        textStyle: {
          color: 'white',
        },
        extraCssText:
          'opacity: 0.9; width: auto; max-width: 300px; white-space: pre-wrap; text-align: center;',
      },
      series: seriesOpts,
    };

    // notMerge must be set to true to override any existing options set on the chart
    this.chart.setOption(option, true);
    this.setXAxisLabelTooltips(xAxisCategoryToTooltipText);
  }
}
