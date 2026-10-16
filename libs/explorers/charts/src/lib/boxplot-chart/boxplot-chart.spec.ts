import { DatasetComponentOption, EChartsOption, ScatterSeriesOption } from 'echarts';
import {
  CAPPED_POINT_SPACING_PX,
  CAPPED_POINT_SYMBOL,
  CAPPED_POINT_SYMBOL_SIZE,
} from '../constants';
import { staticBoxplotPoints, staticBoxplotSummaries } from '../mocks';
import { BoxplotProps, CategoryPoint } from '../models';
import { initChart, setNoDataOption } from '../utils';
import { BoxplotChart } from './boxplot-chart';

// Only the chart lifecycle is stubbed; the real utils are kept because getUniqueValues is what
// derives the categories under test.
jest.mock('../utils', () => ({
  ...jest.requireActual('../utils'),
  initChart: jest.fn(),
  setNoDataOption: jest.fn(),
}));

type CappedRow = CategoryPoint & {
  xAxisValue: number;
  cappedValue: number;
  cappedOffsetPx: number;
};

function makeChart(props: Partial<BoxplotProps> = {}): BoxplotChart {
  return new BoxplotChart(document.createElement('div'), {
    points: staticBoxplotPoints,
    ...props,
  });
}

describe('BoxplotChart', () => {
  const mockSetOption = jest.fn();
  const mockChart = { setOption: mockSetOption, dispose: jest.fn() };

  beforeEach(() => {
    jest.clearAllMocks();
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
      .filter((name) => name.endsWith('above y-axis max'));
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

  describe('points above yAxisMax', () => {
    const yAxisMax = 10;
    // yAxisMax + 0.1 sits inside the y-axis padding, so the axis alone would still draw it
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

    function getCappedRows(pointCategory: string): CappedRow[] {
      return getDatasetSource(`capped-${pointCategory}`) as CappedRow[];
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
      expect(getCappedSeriesNames()).toEqual(['Female above y-axis max', 'Male above y-axis max']);
      expect(getSeries('Female above y-axis max')?.encode).toEqual({
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
      const symbolOffset = getSeries('Female above y-axis max')?.symbolOffset as (
        pt: CappedRow,
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

    it("places a group's markers at its point category offset without jitter", () => {
      makeCappedChart();
      const cat1Rows = getCappedRows('Female').filter((pt) => pt.xAxisCategory === 'CAT1');
      cat1Rows.forEach((pt) => expect(pt.xAxisValue).toBeCloseTo(0.85));
      expect(getCappedRows('Male')[0].xAxisValue).toBeCloseTo(1.15);
    });

    it('uses the capped symbol and size in the point category colour', () => {
      makeCappedChart();
      const series = getSeries('Female above y-axis max');
      expect(series?.symbol).toBe(CAPPED_POINT_SYMBOL);
      expect(series?.symbolSize).toBe(CAPPED_POINT_SYMBOL_SIZE);
      expect(series?.itemStyle).toEqual({ color: pointCategoryColors.Female });
    });

    it('caps points without point categories as a single group', () => {
      makeCappedChart({
        points: [
          { xAxisCategory: 'CAT1', value: 4 },
          { xAxisCategory: 'CAT1', value: yAxisMax + 1 },
        ],
      });
      expect(getCappedSeriesNames()).toEqual(['points above y-axis max']);
      expect(getCappedRows('points').map((pt) => pt.value)).toEqual([yAxisMax + 1]);
    });

    it('passes the true value to the point tooltip formatter', () => {
      const pointTooltipFormatter = jest.fn().mockReturnValue('tooltip');
      makeCappedChart({ pointTooltipFormatter });
      const formatter = (
        getSeries('Male above y-axis max')?.tooltip as {
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
