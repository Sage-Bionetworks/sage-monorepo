import { DatasetComponentOption, EChartsOption, ScatterSeriesOption } from 'echarts';
import {
  AUTO_Y_AXIS_MARGIN,
  CAPPED_POINT_SPACING_PX,
  CAPPED_POINT_SYMBOL,
  CAPPED_POINT_SYMBOL_SIZE,
  CAPPED_POINT_SERIES_NAME_SUFFIX,
} from '../constants';
import { staticBoxplotPoints, staticBoxplotSummaries } from '../mocks';
import { BoxplotProps, CategoryPoint } from '../models';
import { CappedCategoryPoint } from '../models/boxplot';
import { initChart, setNoDataOption } from '../utils';
import { BoxplotChart } from './boxplot-chart';

// Only the chart lifecycle is stubbed; the real utils are kept because getUniqueValues is what
// derives the categories under test.
jest.mock('../utils', () => ({
  ...jest.requireActual('../utils'),
  initChart: jest.fn(),
  setNoDataOption: jest.fn(),
}));

function makeChart(props: Partial<BoxplotProps> = {}): BoxplotChart {
  return new BoxplotChart(document.createElement('div'), {
    points: staticBoxplotPoints,
    ...props,
  });
}

describe('BoxplotChart', () => {
  const mockSetOption = jest.fn();
  let pxPerCategory = 400;
  const mockChart = {
    setOption: mockSetOption,
    dispose: jest.fn(),
    convertToPixel: (_finder: unknown, value: number) => value * pxPerCategory,
  };

  beforeEach(() => {
    jest.clearAllMocks();
    pxPerCategory = 400;
    (initChart as jest.Mock).mockReturnValue(mockChart);
  });

  function getOption(): EChartsOption {
    return mockSetOption.mock.calls[0][0] as EChartsOption;
  }

  function getXAxisCategories(): string[] {
    const axes = getOption().xAxis as { id: string; data?: string[] }[];
    return axes.find((axis) => axis.id === 'category-x-axis')?.data ?? [];
  }

  function getDatasetSource(id: string): unknown {
    const datasets = getOption().dataset as DatasetComponentOption[];
    return datasets.find((dataset) => dataset.id === id)?.source;
  }

  function getPointSeriesValues(): number[] {
    return (getDatasetSource('points') as CategoryPoint[]).map((pt) => pt.value);
  }

  function getSeries(name: string): ScatterSeriesOption | undefined {
    return (getOption().series as ScatterSeriesOption[]).find((series) => series.name === name);
  }

  function getCappedSeriesNames(): string[] {
    return (getOption().series as ScatterSeriesOption[])
      .map((series) => String(series.name))
      .filter((name) => name.endsWith(CAPPED_POINT_SERIES_NAME_SUFFIX));
  }

  describe('setOptions -- no data', () => {
    it('calls setNoDataOption when there are neither points nor summaries', () => {
      makeChart({ points: [] });
      expect(setNoDataOption).toHaveBeenCalledWith(mockChart, 'textOnly');
      expect(mockSetOption).not.toHaveBeenCalled();
    });
  });

  describe('xAxisCategories', () => {
    it('derives categories from points when xAxisCategories is undefined', () => {
      makeChart();
      expect(getXAxisCategories()).toEqual(['CAT1', 'CAT2', 'CAT3', 'CAT4']);
    });

    it('derives categories from summaries when there are no points', () => {
      makeChart({ points: [], summaries: staticBoxplotSummaries });
      expect(getXAxisCategories()).toEqual(
        staticBoxplotSummaries.map((summary) => summary.xAxisCategory),
      );
    });

    it('uses explicit xAxisCategories when provided', () => {
      makeChart({ xAxisCategories: ['CAT4', 'CAT1'] });
      expect(getXAxisCategories()).toEqual(['CAT4', 'CAT1']);
    });

    it('derives categories from points when xAxisCategories is empty', () => {
      makeChart({ xAxisCategories: [] });
      expect(getXAxisCategories()).toEqual(['CAT1', 'CAT2', 'CAT3', 'CAT4']);
    });
  });

  describe('drawing order', () => {
    function getDrawingOrder() {
      const option = getOption();
      const axes = [...(option.xAxis as { z?: number }[]), option.yAxis as { z?: number }];
      const series = option.series as { type: string; z?: number; markArea?: unknown }[];
      return {
        axisZs: axes.map((axis) => axis.z ?? 0),
        laneZ: (option['markArea'] as { z?: number } | undefined)?.z,
        boxplot: series.find((s) => s.type === 'boxplot'),
        points: series.filter((s) => s.type === 'scatter'),
      };
    }

    it('draws boxes and points above the axes, so grid lines run behind them', () => {
      makeChart({ chartStyle: 'grayGrid' });
      const { axisZs, boxplot, points } = getDrawingOrder();
      const topAxisZ = Math.max(...axisZs);

      expect(boxplot).toBeDefined();
      expect(boxplot?.z).toBeGreaterThan(topAxisZ);
      expect(points.length).toBeGreaterThan(0);
      points.forEach((series) => expect(series.z).toBeGreaterThan(boxplot?.z ?? Infinity));
    });

    it('draws capped markers above the points they stand in for', () => {
      makeChart({
        points: [
          { xAxisCategory: 'CAT1', pointCategory: 'Female', value: 4 },
          { xAxisCategory: 'CAT1', pointCategory: 'Female', value: 20 },
        ],
        yAxisMax: 10,
        capPointsAboveYAxisMax: true,
      });
      const series = getOption().series as { name?: string; z?: number }[];
      const pointSeries = series.find((s) => s.name === 'Female');
      const cappedSeries = series.find(
        (s) => s.name === `Female${CAPPED_POINT_SERIES_NAME_SUFFIX}`,
      );

      expect(pointSeries).toBeDefined();
      expect(cappedSeries).toBeDefined();
      expect(cappedSeries?.z).toBeGreaterThan(pointSeries?.z ?? Infinity);
    });

    it.each([
      ['gray grid lanes', { chartStyle: 'grayGrid' } as Partial<BoxplotProps>],
      ['custom lane colors', { laneBackgroundColors: ['#F9F9FA', '#FFFFFF'] }],
    ])('keeps %s below the axes, so they never cover the axis lines', (_, props) => {
      makeChart(props);
      const { axisZs, laneZ, boxplot } = getDrawingOrder();

      expect(boxplot?.markArea).toBeDefined();
      expect(laneZ).toBeDefined();
      expect(laneZ).toBeLessThan(Math.min(...axisZs));
    });
  });

  describe('y-axis range', () => {
    function getYAxis() {
      return getOption().yAxis as {
        min?: number;
        max?: number;
        boundaryGap?: [string, string];
        axisLabel?: { showMinLabel?: boolean; showMaxLabel?: boolean };
      };
    }

    it('ends the y-axis exactly at yAxisMin and yAxisMax', () => {
      makeChart({ yAxisMin: -1, yAxisMax: 0.002 });
      const yAxis = getYAxis();

      expect(yAxis.min).toBe(-1);
      expect(yAxis.max).toBe(0.002);
    });

    it.each([
      ['yAxisMin', { yAxisMin: 0, yAxisMax: 10 }, 'min'],
      ['yAxisMax', { yAxisMin: -10, yAxisMax: 0 }, 'max'],
    ] as const)('keeps a %s of zero', (_, props, end) => {
      makeChart(props);
      expect(getYAxis()[end]).toBe(0);
    });

    function pointsWithValues(values: number[]): CategoryPoint[] {
      return values.map((value) => ({ xAxisCategory: 'CAT1', value }));
    }

    it.each([
      ['all positive, so the bottom stays at zero', [0, 5, 950], ['0%', AUTO_Y_AXIS_MARGIN]],
      ['all negative, so the top stays at zero', [-950, -5, 0], [AUTO_Y_AXIS_MARGIN, '0%']],
      ['on both sides of zero', [-0.6, 0.1, 0.6], [AUTO_Y_AXIS_MARGIN, AUTO_Y_AXIS_MARGIN]],
    ])('fits the y-axis to data that is %s', (_, values, expectedMargin) => {
      makeChart({ points: pointsWithValues(values) });
      const yAxis = getYAxis();

      expect(yAxis.min).toBeUndefined();
      expect(yAxis.max).toBeUndefined();
      expect(yAxis.boundaryGap).toEqual(expectedMargin);
    });

    it('counts summaries that extend past zero when choosing the margin', () => {
      makeChart({
        points: pointsWithValues([0.1]),
        summaries: [
          {
            xAxisCategory: 'CAT1',
            min: -0.5,
            firstQuartile: -0.1,
            median: 0,
            thirdQuartile: 0.1,
            max: 0.5,
          },
        ],
      });
      expect(getYAxis().boundaryGap).toEqual([AUTO_Y_AXIS_MARGIN, AUTO_Y_AXIS_MARGIN]);
    });

    it('keeps a zero-based axis at zero when only yAxisMax is set', () => {
      makeChart({ points: pointsWithValues([0, 4, 20]), yAxisMax: 10 });
      const yAxis = getYAxis();

      expect(yAxis.min).toBeUndefined();
      expect(yAxis.boundaryGap?.[0]).toBe('0%');
    });

    it('does not hide the end labels when bounds are set', () => {
      makeChart({ yAxisMin: 0, yAxisMax: 10 });
      const { axisLabel } = getYAxis();

      expect(axisLabel?.showMinLabel).not.toBe(false);
      expect(axisLabel?.showMaxLabel).not.toBe(false);
    });
  });

  describe('points above yAxisMax', () => {
    const yAxisMax = 10;
    const pointsAroundMax: CategoryPoint[] = [
      { xAxisCategory: 'CAT1', pointCategory: 'Female', value: 4 },
      { xAxisCategory: 'CAT1', pointCategory: 'Female', value: yAxisMax },
      { xAxisCategory: 'CAT1', pointCategory: 'Female', value: yAxisMax + 0.1 },
      { xAxisCategory: 'CAT1', pointCategory: 'Male', value: yAxisMax + 5 },
    ];

    it('removes points above yAxisMax from the point series and keeps a point equal to it', () => {
      makeChart({ points: pointsAroundMax, yAxisMax });
      expect(getPointSeriesValues()).toEqual([4, yAxisMax]);
    });

    it('keeps every point when yAxisMax is undefined', () => {
      makeChart({ points: pointsAroundMax });
      expect(getPointSeriesValues()).toEqual([4, yAxisMax, yAxisMax + 0.1, yAxisMax + 5]);
    });

    it('still computes the box from every point, including those above yAxisMax', () => {
      makeChart({ points: pointsAroundMax, yAxisMax });
      const boxInput = (
        getDatasetSource('points-formatted-for-boxplot-transform') as number[][]
      ).flat();
      expect(boxInput).toEqual([4, yAxisMax, yAxisMax + 0.1, yAxisMax + 5]);
    });
  });

  describe('capPointsAboveYAxisMax', () => {
    const yAxisMax = 10;
    const pointCategoryColors = { Female: '#D72247', Male: '#245299' };
    const points: CategoryPoint[] = [
      { xAxisCategory: 'CAT1', pointCategory: 'Female', value: 4 },
      { xAxisCategory: 'CAT1', pointCategory: 'Female', value: yAxisMax + 2 },
      { xAxisCategory: 'CAT1', pointCategory: 'Female', value: yAxisMax + 1 },
      { xAxisCategory: 'CAT1', pointCategory: 'Female', value: yAxisMax + 3 },
      { xAxisCategory: 'CAT2', pointCategory: 'Female', value: yAxisMax + 4 },
      { xAxisCategory: 'CAT1', pointCategory: 'Male', value: 5 },
      { xAxisCategory: 'CAT1', pointCategory: 'Male', value: yAxisMax + 5 },
    ];

    function makeCappedChart(props: Partial<BoxplotProps> = {}) {
      makeChart({
        points,
        yAxisMax,
        capPointsAboveYAxisMax: true,
        pointCategoryColors,
        ...props,
      });
    }

    function getCappedRows(pointCategory: string): CappedCategoryPoint[] {
      return getDatasetSource(`capped-${pointCategory}`) as CappedCategoryPoint[];
    }

    it('adds no capped markers when the flag is off', () => {
      makeCappedChart({ capPointsAboveYAxisMax: false });
      expect(getCappedSeriesNames()).toEqual([]);
    });

    it('adds no capped markers when yAxisMax is undefined', () => {
      makeCappedChart({ yAxisMax: undefined });
      expect(getCappedSeriesNames()).toEqual([]);
    });

    it('draws each point above yAxisMax at yAxisMax, keeping its true value', () => {
      makeCappedChart();
      expect(getCappedSeriesNames()).toEqual([
        `Female${CAPPED_POINT_SERIES_NAME_SUFFIX}`,
        `Male${CAPPED_POINT_SERIES_NAME_SUFFIX}`,
      ]);
      expect(getSeries(`Female${CAPPED_POINT_SERIES_NAME_SUFFIX}`)?.encode).toEqual({
        x: 'xAxisValue',
        y: 'cappedValue',
      });
      expect(getCappedRows('Female').map((pt) => [pt.value, pt.cappedValue])).toEqual([
        [yAxisMax + 1, yAxisMax],
        [yAxisMax + 2, yAxisMax],
        [yAxisMax + 3, yAxisMax],
        [yAxisMax + 4, yAxisMax],
      ]);
      expect(getCappedRows('Male').map((pt) => pt.value)).toEqual([yAxisMax + 5]);
    });

    it('spreads the markers of one group evenly in pixels, smallest value first', () => {
      makeCappedChart();
      const symbolOffset = getSeries(`Female${CAPPED_POINT_SERIES_NAME_SUFFIX}`)?.symbolOffset as (
        pt: CappedCategoryPoint,
      ) => number[];
      const cat1Rows = getCappedRows('Female').filter((pt) => pt.xAxisCategory === 'CAT1');
      const cat2Rows = getCappedRows('Female').filter((pt) => pt.xAxisCategory === 'CAT2');

      expect(cat1Rows.map((pt) => pt.value)).toEqual([yAxisMax + 1, yAxisMax + 2, yAxisMax + 3]);
      expect(cat1Rows.map(symbolOffset)).toEqual([
        [-CAPPED_POINT_SPACING_PX, 0],
        [0, 0],
        [CAPPED_POINT_SPACING_PX, 0],
      ]);
      expect(cat2Rows.map(symbolOffset)).toEqual([[0, 0]]);
    });

    it("squeezes a group on narrow charts so it stays within its own point category's column", () => {
      pxPerCategory = 90;
      makeCappedChart();
      const symbolOffset = getSeries(`Female${CAPPED_POINT_SERIES_NAME_SUFFIX}`)?.symbolOffset as (
        pt: CappedCategoryPoint,
      ) => number[];
      const femaleRows = getCappedRows('Female').filter((pt) => pt.xAxisCategory === 'CAT1');
      const maleRow = getCappedRows('Male')[0];
      const [left, middle, right] = femaleRows.map((pt) => symbolOffset(pt)[0]);

      const columnGapPx = (maleRow.xAxisValue - femaleRows[0].xAxisValue) * pxPerCategory;

      expect(right).toBeLessThan(CAPPED_POINT_SPACING_PX);
      expect([left, middle]).toEqual([-right, 0]);
      expect(right + CAPPED_POINT_SYMBOL_SIZE / 2).toBeLessThanOrEqual(columnGapPx / 2 + 1e-9);
    });

    it("places a group's markers at its point category offset without jitter", () => {
      makeCappedChart();
      const cat1Rows = getCappedRows('Female').filter((pt) => pt.xAxisCategory === 'CAT1');
      cat1Rows.forEach((pt) => expect(pt.xAxisValue).toBeCloseTo(0.85));
      expect(getCappedRows('Male')[0].xAxisValue).toBeCloseTo(1.15);
    });

    it('uses the capped symbol and size in the point category colour and opacity', () => {
      makeCappedChart({ pointOpacity: 0.5 });
      const series = getSeries(`Female${CAPPED_POINT_SERIES_NAME_SUFFIX}`);
      expect(series?.symbol).toBe(CAPPED_POINT_SYMBOL);
      expect(series?.symbolSize).toBe(CAPPED_POINT_SYMBOL_SIZE);
      expect(series?.symbolKeepAspect).toBe(true);
      expect(series?.itemStyle).toEqual(getSeries('Female')?.itemStyle);
      expect(series?.itemStyle).toEqual({ color: pointCategoryColors.Female, opacity: 0.5 });
    });

    it('caps points without point categories as a single group', () => {
      makeCappedChart({
        points: [
          { xAxisCategory: 'CAT1', value: 4 },
          { xAxisCategory: 'CAT1', value: yAxisMax + 1 },
        ],
      });
      expect(getCappedSeriesNames()).toEqual([`points${CAPPED_POINT_SERIES_NAME_SUFFIX}`]);
      expect(getCappedRows('points').map((pt) => pt.value)).toEqual([yAxisMax + 1]);
    });

    it('passes the true value to the point tooltip formatter', () => {
      const pointTooltipFormatter = jest.fn().mockReturnValue('tooltip');
      makeCappedChart({ pointTooltipFormatter });
      const formatter = (
        getSeries(`Male${CAPPED_POINT_SERIES_NAME_SUFFIX}`)?.tooltip as {
          formatter: (params: { data: unknown }) => string;
        }
      ).formatter;

      expect(formatter({ data: getCappedRows('Male')[0] })).toBe('tooltip');
      expect(pointTooltipFormatter).toHaveBeenCalledWith(
        expect.objectContaining({ value: yAxisMax + 5 }),
        expect.anything(),
      );
    });
  });
});
