import { EChartsOption } from 'echarts';
import { CallbackDataParams } from 'echarts/types/dist/shared';
import { DEFAULT_COLOR } from '../constants';
import { candlestickItems } from '../mocks';
import { CandlestickItem, CandlestickProps } from '../models';
import { initChart, setNoDataOption } from '../utils';
import {
  CandlestickChart,
  DEFAULT_CHART_HEIGHT,
  DEFAULT_REFERENCE_LINE_COLOR,
  computeYBounds,
  resolveItemColor,
} from './candlestick-chart';

jest.mock('../utils', () => ({
  initChart: jest.fn(),
  setNoDataOption: jest.fn(),
}));

describe('resolveItemColor', () => {
  it('uses item color when provided', () => {
    expect(resolveItemColor('#ff0000', '#00ff00')).toBe('#ff0000');
  });

  it('falls back to prop default when item color is undefined', () => {
    expect(resolveItemColor(undefined, '#00ff00')).toBe('#00ff00');
  });

  it('falls back to DEFAULT_COLOR when both item color and prop default are undefined', () => {
    expect(resolveItemColor(undefined, undefined)).toBe(DEFAULT_COLOR);
  });

  it('treats empty string item color as not set, falling back to prop default', () => {
    expect(resolveItemColor('', '#00ff00')).toBe('#00ff00');
  });
});

describe('computeYBounds', () => {
  it('returns explicit bounds when both yAxisMin and yAxisMax are provided', () => {
    const props: CandlestickProps = {
      items: candlestickItems,
      yAxisMin: 0,
      yAxisMax: 2,
    };
    expect(computeYBounds(props)).toEqual([0, 2]);
  });

  it('ignores explicit bounds when only one is provided', () => {
    const propsMinOnly: CandlestickProps = { items: candlestickItems, yAxisMin: 0 };
    const propsMaxOnly: CandlestickProps = { items: candlestickItems, yAxisMax: 2 };
    const autoCalculated = computeYBounds({ items: candlestickItems });
    expect(computeYBounds(propsMinOnly)).toEqual(autoCalculated);
    expect(computeYBounds(propsMaxOnly)).toEqual(autoCalculated);
  });

  it('auto-calculates symmetric bounds from CI data (x1.1)', () => {
    // candlestickItems max |CI bound| is COGDX ciUpper = 1.48 -> 1.48 * 1.1 = 1.628
    const [yMin, yMax] = computeYBounds({ items: candlestickItems });
    expect(yMin).toBeCloseTo(-1.628, 3);
    expect(yMax).toBeCloseTo(1.628, 3);
  });

  it('returns symmetric bounds (yMin === -yMax)', () => {
    const [yMin, yMax] = computeYBounds({ items: candlestickItems });
    expect(yMin).toBeCloseTo(-yMax, 10);
  });

  it('returns [-1, 1] when all CI bounds are zero', () => {
    const props: CandlestickProps = {
      items: [{ xAxisCategory: 'A', value: 0, ciLower: 0, ciUpper: 0 }],
    };
    expect(computeYBounds(props)).toEqual([-1, 1]);
  });
});

function makeChart(props: Partial<CandlestickProps> = {}): CandlestickChart {
  return new CandlestickChart(document.createElement('div'), {
    items: candlestickItems,
    ...props,
  });
}

describe('CandlestickChart', () => {
  const mockSetOption = jest.fn();
  const mockDispose = jest.fn();
  const mockChart = { setOption: mockSetOption, dispose: mockDispose };

  beforeEach(() => {
    jest.clearAllMocks();
    (initChart as jest.Mock).mockReturnValue(mockChart);
  });

  function getOption(): EChartsOption {
    return mockSetOption.mock.calls[0][0] as EChartsOption;
  }

  describe('constructor', () => {
    it('initializes chart with default fixed height', () => {
      makeChart();
      expect(initChart).toHaveBeenCalledWith(expect.anything(), DEFAULT_CHART_HEIGHT);
    });
  });

  describe('destroy', () => {
    it('disposes the chart', () => {
      makeChart().destroy();
      expect(mockDispose).toHaveBeenCalled();
    });
  });

  describe('setOptions -- no items', () => {
    it('calls setNoDataOption with textOnly by default', () => {
      makeChart({ items: [] });
      expect(setNoDataOption).toHaveBeenCalledWith(mockChart, 'textOnly');
    });

    it('calls setNoDataOption with custom noDataStyle', () => {
      makeChart({ items: [], noDataStyle: 'grayBackground' });
      expect(setNoDataOption).toHaveBeenCalledWith(mockChart, 'grayBackground');
    });

    it('does not call chart.setOption', () => {
      makeChart({ items: [] });
      expect(mockSetOption).not.toHaveBeenCalled();
    });
  });

  describe('setOptions -- with items', () => {
    it('calls chart.setOption with notMerge=true', () => {
      makeChart();
      expect(mockSetOption).toHaveBeenCalledWith(expect.anything(), true);
    });

    it('derives x-axis categories from items in order', () => {
      makeChart();
      const xAxis = getOption().xAxis as { data: string[] };
      expect(xAxis.data).toEqual(['BRAAK', 'CERAD', 'COGDX']);
    });

    it('uses explicit xAxisCategories when provided', () => {
      makeChart({ xAxisCategories: ['Z', 'Y', 'X'] });
      const xAxis = getOption().xAxis as { data: string[] };
      expect(xAxis.data).toEqual(['Z', 'Y', 'X']);
    });

    it('uses smaller grid top when no chart title', () => {
      makeChart();
      expect((getOption().grid as { top: number }).top).toBe(20);
    });

    it('uses larger grid top when chart title is provided', () => {
      makeChart({ title: 'My Chart' });
      expect((getOption().grid as { top: number }).top).toBe(60);
    });

    it('renders 2 series when no reference line is configured (CI line, dots)', () => {
      makeChart();
      expect((getOption().series as unknown[]).length).toBe(2);
    });

    it('renders 3 series when a reference line is configured (reference line, CI line, dots)', () => {
      makeChart({ referenceLineValue: 1 });
      expect((getOption().series as unknown[]).length).toBe(3);
    });

    it('includes chart title text when title is provided', () => {
      makeChart({ title: 'My Chart' });
      const titles = getOption().title as { text: string }[];
      expect(titles[0].text).toBe('My Chart');
    });

    it('uses empty title array when no title is provided', () => {
      makeChart();
      expect(getOption().title).toEqual([]);
    });

    it('disables x-axis tooltips when no xAxisLabelTooltipFormatter', () => {
      makeChart();
      const xAxis = getOption().xAxis as { triggerEvent: boolean; tooltip: { show: boolean } };
      expect(xAxis.triggerEvent).toBe(false);
      expect(xAxis.tooltip.show).toBe(false);
    });

    it('enables x-axis tooltips when xAxisLabelTooltipFormatter is provided', () => {
      makeChart({ xAxisLabelTooltipFormatter: (cat) => cat });
      const xAxis = getOption().xAxis as { triggerEvent: boolean; tooltip: { show: boolean } };
      expect(xAxis.triggerEvent).toBe(true);
      expect(xAxis.tooltip.show).toBe(true);
    });

    it('uses default dot tooltip when no pointTooltipFormatter', () => {
      makeChart();
      const series = getOption().series as {
        tooltip: { formatter: (p: CallbackDataParams) => string };
      }[];
      const dotSeries = series[1];
      const result = dotSeries.tooltip.formatter({ dataIndex: 0 } as CallbackDataParams);
      expect(result).toBe(`Value: ${candlestickItems[0].value}`);
    });

    it('uses custom pointTooltipFormatter when provided', () => {
      const formatter = (item: CandlestickItem) => `custom: ${item.xAxisCategory}`;
      makeChart({ pointTooltipFormatter: formatter });
      const series = getOption().series as {
        tooltip: { formatter: (p: CallbackDataParams) => string };
      }[];
      const dotSeries = series[1];
      const result = dotSeries.tooltip.formatter({ dataIndex: 0 } as CallbackDataParams);
      expect(result).toBe(`custom: ${candlestickItems[0].xAxisCategory}`);
    });
  });
});
