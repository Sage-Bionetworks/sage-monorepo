import { DatasetComponentOption, ECharts, EChartsOption, SeriesOption } from 'echarts';
import { CallbackDataParams } from 'echarts/types/dist/shared';
import { DEFAULT_POINT_SIZE, GRAY_BACKGROUND_COLOR } from '../constants';
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

const titleTextStyle = {
  fontWeight: 700,
  fontSize: '18px',
  color: 'black',
};

const boxplotStyle = {
  borderColor: '#bcc0ca',
  borderWidth: 2,
  color: 'transparent',
};

const yAxisPadding = 0.2;
const defaultPointShape = 'circle';
const defaultPointColor = '#8b8ad1';
const defaultPointOpacity = 0.8;

const defaultPointCategoryOffset = 0.3;
const defaultPointCategoryJitterMax = 0.02;

const Y_AXIS_TICK_LABELS_MAX_WIDTH = 80;
const SPACE_FOR_Y_AXIS_NAME = 15;

export class BoxplotChart {
  chart: ECharts | undefined;

  constructor(chartDom: HTMLDivElement | HTMLCanvasElement, boxplotProps: BoxplotProps) {
    this.chart = initChart(chartDom);
    this.setOptions(boxplotProps);
  }

  destroy() {
    this.chart?.dispose();
  }

  private getTitleOptions(xAxisTitle?: string, title?: string) {
    const titles: EChartsOption['title'] = [];
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
    return titles;
  }

  private getXAxisOptions(
    xAxisCategories: string[],
    xAxisLabelFormatter: BoxplotProps['xAxisLabelFormatter'],
    xAxisLabelTooltipFormatter: BoxplotProps['xAxisLabelTooltipFormatter'],
    chartStyle: string,
  ) {
    // Use two xAxes:
    //  - value: used to jitter points with multiple pointCategories, where
    //           xAxisCategory is mapped to 1-based index values for both
    //           boxplots and points. Axis is not displayed.
    //  - category: used to display the xAxisCategory and allows xAxis tooltips
    //           to be displayed, since the event will contain the xAxis label
    //           value, not the displayed text.
    const xAxisOptions: EChartsOption['xAxis'] = [
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
          fontWeight: chartStyle === 'grayGrid' ? 'normal' : 'bold',
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
        tooltip: {
          ...(xAxisLabelTooltipFormatter && {
            formatter: (params: CallbackDataParams) => {
              return xAxisLabelTooltipFormatter(params);
            },
            extraCssText: 'border: unset; opacity: 0.9; background-color: #63676c',
          }),
          show: Boolean(xAxisLabelTooltipFormatter),
        },
        triggerEvent: Boolean(xAxisLabelTooltipFormatter),
        position: 'bottom',
      },
    ];
    return xAxisOptions;
  }

  private getYAxisOptions(
    yAxisTitle: BoxplotProps['yAxisTitle'],
    yAxisMin: BoxplotProps['yAxisMin'],
    yAxisMax: BoxplotProps['yAxisMax'],
    chartStyle: BoxplotProps['chartStyle'],
  ) {
    const grayGridTextStyle = {
      fontWeight: 400,
      fontSize: '14px',
      color: 'black',
    };

    const yAxisOptions: EChartsOption['yAxis'] = {
      type: 'value',
      name: yAxisTitle,
      nameLocation: 'middle',
      nameGap: Y_AXIS_TICK_LABELS_MAX_WIDTH,
      nameTextStyle: chartStyle === 'grayGrid' ? grayGridTextStyle : titleTextStyle,
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
        show: chartStyle === 'grayGrid',
      },
      min: yAxisMin ? yAxisMin - yAxisPadding : undefined,
      max: yAxisMax ? yAxisMax + yAxisPadding : undefined,
    };
    return yAxisOptions;
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
      xAxisLabelTooltipFormatter,
      pointTooltipFormatter,
      pointCategoryColors,
      pointCategoryShapes,
      pointOpacity,
    } = boxplotProps;

    const showLegend = boxplotProps.showLegend || false;
    const noDataStyle = boxplotProps.noDataStyle || 'textOnly';
    const chartStyle = boxplotProps.chartStyle || 'minimal';

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
      defaultPointCategoryOffset,
      defaultPointCategoryJitterMax,
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
      markArea:
        chartStyle === 'grayGrid'
          ? {
              itemStyle: {
                color: GRAY_BACKGROUND_COLOR,
              },
              data: xAxisCategories.map((pc, idx) => {
                const spacing = 0.4;
                const pcIndex = idx + 1;
                return [{ xAxis: pcIndex - spacing }, { xAxis: pcIndex + spacing }];
              }),
            }
          : undefined,
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
        symbolSize: DEFAULT_POINT_SIZE,
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

    const option: EChartsOption = {
      legend: {
        show: showLegend,
        data: pointDatasetIds,
        orient: 'horizontal',
        left: 'left',
        top: 'bottom',
        itemHeight: DEFAULT_POINT_SIZE,
        itemWidth: DEFAULT_POINT_SIZE,
        selectedMode: false,
      },
      grid: {
        top: title ? 60 : 20,
        left: Y_AXIS_TICK_LABELS_MAX_WIDTH + SPACE_FOR_Y_AXIS_NAME,
        right: 20,
        containLabel: false,
      },
      title: this.getTitleOptions(xAxisTitle, title),
      aria: {
        enabled: true,
      },
      dataset: datasetOpts,
      xAxis: this.getXAxisOptions(
        xAxisCategories,
        xAxisLabelFormatter,
        xAxisLabelTooltipFormatter,
        chartStyle,
      ),
      yAxis: this.getYAxisOptions(yAxisTitle, yAxisMin, yAxisMax, chartStyle),
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
  }
}
