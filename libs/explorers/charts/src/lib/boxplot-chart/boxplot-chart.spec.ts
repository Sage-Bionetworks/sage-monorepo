import { EChartsOption } from 'echarts';
import { staticBoxplotPoints, staticBoxplotSummaries } from '../mocks';
import { BoxplotProps } from '../models';
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

  // the boxplot stacks a value axis and a category axis; only the latter carries the labels
  function getXAxisCategories(): string[] {
    const axes = getOption().xAxis as { id: string; data?: string[] }[];
    return axes.find((axis) => axis.id === 'category-x-axis')?.data ?? [];
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
});
