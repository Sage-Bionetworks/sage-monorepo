import { DatasetComponentOption, ECharts, EChartsOption, SeriesOption } from 'echarts';
import { CallbackDataParams } from 'echarts/types/dist/shared';
import {
  grayGridBoxplotChartTheme,
  minimalBoxplotChartTheme,
} from '../chart-theme/boxplot-chart-theme';
import { BoxplotProps, CategoryPoint } from '../models';
import { BoxplotChartTheme } from '../models/boxplot';
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

const yAxisPadding = 0.2;
const defaultPointShape = 'circle';
const defaultPointColor = '#8b8ad1';
const defaultPointOpacity = 0.8;

const defaultPointCategoryOffset = 0.3;
const defaultPointCategoryJitterMax = 0.02;

export class BoxplotChart {
  chart: ECharts | undefined;

  constructor(chartDom: HTMLDivElement | HTMLCanvasElement, boxplotProps: BoxplotProps) {
    this.chart = initChart(chartDom);
    this.setOptions(boxplotProps);
  }

  destroy() {
    this.chart?.dispose();
  }

  private getTitleOptions(
    boxplotChartTheme: BoxplotChartTheme,
    xAxisTitle?: string,
    title?: string,
  ) {
    const titles: EChartsOption['title'] = [];
    if (xAxisTitle) {
      titles.push(
        // Add x-axis title as a title rather than xAxis.name, because
        // setting via xAxis.name causes cursor to change to pointer when
        // x-axis label tooltips are used
        {
          text: xAxisTitle,
          textStyle: boxplotChartTheme.titleTextStyle,
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
        textStyle: boxplotChartTheme.titleTextStyle,
      });
    }
    return titles;
  }

  private getXAxisOptions(
    xAxisCategories: string[],
    xAxisLabelFormatter: BoxplotProps['xAxisLabelFormatter'],
    xAxisLabelTooltipFormatter: BoxplotProps['xAxisLabelTooltipFormatter'],
    boxplotChartTheme: BoxplotChartTheme,
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
          ...boxplotChartTheme.xAxisLabelTextStyle,
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
    boxplotChartTheme: BoxplotChartTheme,
  ) {
    const yAxisOptions: EChartsOption['yAxis'] = {
      type: 'value',
      name: yAxisTitle,
      nameLocation: 'middle',
      nameGap: boxplotChartTheme.yAxisTickLabelMaxWidth,
      nameTextStyle: boxplotChartTheme.yAxisTitleTextStyle,
      axisLine: {
        show: true,
      },
      axisLabel: {
        width: boxplotChartTheme.yAxisTickLabelMaxWidth,
        hideOverlap: true,
        showMinLabel: yAxisMin == null,
        showMaxLabel: yAxisMax == null,
      },
      splitLine: boxplotChartTheme.yAxisSplitLine,
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
      chartStyle,
    } = boxplotProps;

    const showLegend = boxplotProps.showLegend || false;
    const noDataStyle = boxplotProps.noDataStyle || 'textOnly';
    const boxplotChartTheme =
      chartStyle === 'grayGrid' ? grayGridBoxplotChartTheme : minimalBoxplotChartTheme;

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
      itemStyle: boxplotChartTheme.boxplotItemStyle,
      boxWidth: [7, 115],
      silent: true,
      tooltip: {
        show: false,
      },
      markArea: boxplotChartTheme.getBoxplotMarkArea?.(xAxisCategories),
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
        symbolSize: boxplotChartTheme.pointSymbolSize,
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
        itemHeight: boxplotChartTheme.pointSymbolSize,
        itemWidth: boxplotChartTheme.pointSymbolSize,
        selectedMode: false,
      },
      grid: {
        ...boxplotChartTheme.grid,
        top: title ? 60 : 20,
      },
      title: this.getTitleOptions(boxplotChartTheme, xAxisTitle, title),
      aria: {
        enabled: true,
      },
      dataset: datasetOpts,
      xAxis: this.getXAxisOptions(
        xAxisCategories,
        xAxisLabelFormatter,
        xAxisLabelTooltipFormatter,
        boxplotChartTheme,
      ),
      yAxis: this.getYAxisOptions(yAxisTitle, yAxisMin, yAxisMax, boxplotChartTheme),
      tooltip: boxplotChartTheme.tooltip,
      series: seriesOpts,
    };

    // notMerge must be set to true to override any existing options set on the chart
    this.chart.setOption(option, true);
  }
}
