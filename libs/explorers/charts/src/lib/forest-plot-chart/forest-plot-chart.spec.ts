import { EChartsOption } from 'echarts';
import { CallbackDataParams } from 'echarts/types/dist/shared';
import { DEFAULT_COLOR } from '../constants';
import { forestPlotItems } from '../mocks';
import { ForestPlotItem, ForestPlotProps } from '../models';
import { initChart, setNoDataOption } from '../utils';
import {
  ForestPlotChart,
  computeInitialHeight,
  computeXBounds,
  resolveItemColor,
} from './forest-plot-chart';

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

describe('computeInitialHeight', () => {
  it('calculates height from item count when above the 490px floor', () => {
    expect(computeInitialHeight(10)).toBe('500px');
    expect(computeInitialHeight(11)).toBe('544px');
  });

  it('enforces minimum height of 490px when computed height is below floor', () => {
    expect(computeInitialHeight(0)).toBe('490px');
    expect(computeInitialHeight(9)).toBe('490px');
  });
});

describe('computeXBounds', () => {
  it('returns explicit bounds when both xAxisMin and xAxisMax are provided', () => {
    const props: ForestPlotProps = {
      items: forestPlotItems,
      xAxisMin: -0.5,
      xAxisMax: 0.5,
    };
    expect(computeXBounds(props)).toEqual([-0.5, 0.5]);
  });

  it('ignores explicit bounds when only one is provided', () => {
    const propsMinOnly: ForestPlotProps = { items: forestPlotItems, xAxisMin: -0.5 };
    const propsMaxOnly: ForestPlotProps = { items: forestPlotItems, xAxisMax: 0.5 };
    const autoCalculated = computeXBounds({ items: forestPlotItems });
    // Neither case meets the "both defined" condition — auto-calculated instead
    expect(computeXBounds(propsMinOnly)).toEqual(autoCalculated);
    expect(computeXBounds(propsMaxOnly)).toEqual(autoCalculated);
  });

  it('auto-calculates symmetric bounds from CI data (x1.1)', () => {
    // forestPlotItems max |CI bound| is DLPFC ciLeft = -0.178 → 0.178 × 1.1 = 0.1958
    const [xMin, xMax] = computeXBounds({ items: forestPlotItems });
    expect(xMin).toBeCloseTo(-0.1958, 3);
    expect(xMax).toBeCloseTo(0.1958, 3);
  });

  it('returns symmetric bounds (xMin === -xMax)', () => {
    const [xMin, xMax] = computeXBounds({ items: forestPlotItems });
    expect(xMin).toBeCloseTo(-xMax, 10);
  });

  it('handles single item', () => {
    const props: ForestPlotProps = {
      items: [{ yAxisCategory: 'ACC', value: 0, ciLeft: -0.1, ciRight: 0.2 }],
    };
    const [xMin, xMax] = computeXBounds(props);
    expect(xMin).toBeCloseTo(-0.22, 3);
    expect(xMax).toBeCloseTo(0.22, 3);
  });

  it('returns [-1, 1] when all CI bounds are zero', () => {
    const props: ForestPlotProps = {
      items: [{ yAxisCategory: 'ACC', value: 0, ciLeft: 0, ciRight: 0 }],
    };
    expect(computeXBounds(props)).toEqual([-1, 1]);
  });
});

function makeChart(props: Partial<ForestPlotProps> = {}): ForestPlotChart {
  return new ForestPlotChart(document.createElement('div'), {
    items: forestPlotItems,
    ...props,
  });
}

describe('ForestPlotChart', () => {
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

  describe('constructor — initial height', () => {
    it('uses items length when yAxisCategories is not provided', () => {
      makeChart();
      expect(initChart).toHaveBeenCalledWith(
        expect.anything(),
        computeInitialHeight(forestPlotItems.length),
      );
    });

    it('uses yAxisCategories length when provided', () => {
      const yAxisCategories = ['A', 'B', 'C', 'D', 'E'];
      makeChart({ yAxisCategories });
      expect(initChart).toHaveBeenCalledWith(expect.anything(), computeInitialHeight(5));
    });
  });

  describe('destroy', () => {
    it('disposes the chart', () => {
      makeChart().destroy();
      expect(mockDispose).toHaveBeenCalled();
    });
  });

  describe('setOptions — no items', () => {
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

  describe('setOptions — with items', () => {
    it('calls chart.setOption with notMerge=true', () => {
      makeChart();
      expect(mockSetOption).toHaveBeenCalledWith(expect.anything(), true);
    });

    it('derives y-axis categories from items in order', () => {
      makeChart();
      const yAxis = getOption().yAxis as { data: string[] };
      expect(yAxis.data).toEqual(['ACC', 'CBE', 'DLPFC', 'FP', 'IFG', 'PHG', 'STG', 'TCX']);
    });

    it('uses explicit yAxisCategories when provided', () => {
      makeChart({ yAxisCategories: ['Z', 'Y', 'X'] });
      const yAxis = getOption().yAxis as { data: string[] };
      expect(yAxis.data).toEqual(['Z', 'Y', 'X']);
    });

    it('uses smaller grid top when no chart title', () => {
      makeChart();
      expect((getOption().grid as { top: number }).top).toBe(20);
    });

    it('uses larger grid top when chart title is provided', () => {
      makeChart({ title: 'My Chart' });
      expect((getOption().grid as { top: number }).top).toBe(60);
    });

    it('uses default grid bottom when no x-axis title', () => {
      makeChart();
      expect((getOption().grid as { bottom: number }).bottom).toBe(40);
    });

    it('uses X_AXIS_TITLE_GAP as grid bottom when x-axis title is provided', () => {
      makeChart({ xAxisTitle: 'Log FC' });
      expect((getOption().grid as { bottom: number }).bottom).toBe(50);
    });

    it('renders 3 series (zero line, CI line, dots)', () => {
      makeChart();
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

    it('disables y-axis tooltips when no yAxisLabelTooltipFormatter', () => {
      makeChart();
      const yAxis = getOption().yAxis as { triggerEvent: boolean; tooltip: { show: boolean } };
      expect(yAxis.triggerEvent).toBe(false);
      expect(yAxis.tooltip.show).toBe(false);
    });

    it('enables y-axis tooltips when yAxisLabelTooltipFormatter is provided', () => {
      makeChart({ yAxisLabelTooltipFormatter: (cat) => cat });
      const yAxis = getOption().yAxis as { triggerEvent: boolean; tooltip: { show: boolean } };
      expect(yAxis.triggerEvent).toBe(true);
      expect(yAxis.tooltip.show).toBe(true);
    });

    it('uses default dot tooltip when no pointTooltipFormatter', () => {
      makeChart();
      const series = getOption().series as {
        tooltip: { formatter: (p: CallbackDataParams) => string };
      }[];
      const dotSeries = series[2];
      const result = dotSeries.tooltip.formatter({ dataIndex: 0 } as CallbackDataParams);
      expect(result).toBe(`Value: ${forestPlotItems[0].value}`);
    });

    it('uses custom pointTooltipFormatter when provided', () => {
      const formatter = (item: ForestPlotItem) => `custom: ${item.yAxisCategory}`;
      makeChart({ pointTooltipFormatter: formatter });
      const series = getOption().series as {
        tooltip: { formatter: (p: CallbackDataParams) => string };
      }[];
      const dotSeries = series[2];
      const result = dotSeries.tooltip.formatter({ dataIndex: 0 } as CallbackDataParams);
      expect(result).toBe(`custom: ${forestPlotItems[0].yAxisCategory}`);
    });
  });
});
