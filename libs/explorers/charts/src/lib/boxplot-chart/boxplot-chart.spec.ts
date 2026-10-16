import { DatasetComponentOption, EChartsOption } from 'echarts';
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
});
