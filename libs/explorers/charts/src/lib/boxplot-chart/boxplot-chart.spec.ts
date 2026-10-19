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

  describe('drawing order', () => {
    // eCharts draws a markArea at z 1 unless the markArea sets its own z
    const markAreaDefaultZ = 1;

    function getDrawingOrder() {
      const option = getOption();
      const axes = [...(option.xAxis as { z?: number }[]), option.yAxis as { z?: number }];
      const series = option.series as { type: string; z?: number; markArea?: { z?: number } }[];
      return {
        axisZs: axes.map((axis) => axis.z ?? 0),
        boxplot: series.find((s) => s.type === 'boxplot'),
        points: series.filter((s) => s.type === 'scatter'),
      };
    }

    it('draws boxes and points above the axes, so grid lines run behind them', () => {
      makeChart({ chartStyle: 'grayGrid' });
      const { axisZs, boxplot, points } = getDrawingOrder();
      const topAxisZ = Math.max(...axisZs);

      expect(boxplot?.z).toBeGreaterThan(topAxisZ);
      expect(points.length).toBeGreaterThan(0);
      points.forEach((series) => expect(series.z).toBeGreaterThan(boxplot?.z ?? Infinity));
    });

    it.each([
      ['gray grid lanes', { chartStyle: 'grayGrid' } as Partial<BoxplotProps>],
      ['custom lane colors', { laneBackgroundColors: ['#F9F9FA', '#FFFFFF'] }],
    ])('keeps %s below the axes, so they never cover the axis lines', (_, props) => {
      makeChart(props);
      const { axisZs, boxplot } = getDrawingOrder();

      expect(boxplot?.markArea).toBeDefined();
      expect(boxplot?.markArea?.z ?? markAreaDefaultZ).toBeLessThan(Math.min(...axisZs));
    });
  });
});
