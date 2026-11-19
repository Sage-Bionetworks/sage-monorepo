import { EChartsOption } from 'echarts';
import { CallbackDataParams } from 'echarts/types/dist/shared';
import { DEFAULT_COLOR } from '../constants';
import { forestPlotItems } from '../mocks';
import { ForestPlotItem, ForestPlotProps } from '../models';
import { GRID_TOP } from '../point-plot-defaults';
import { initChart, setNoDataOption } from '../utils';
import { getChartFontFamily } from '../utils/chart-font';
import {
  computeXBounds,
  computeXTickPositions,
  measureWidestBoldLabel,
  Y_AXIS_LABEL_STYLE_NAMES,
} from './forest-plot-axis';

type YAxisLabelRichStyle = {
  fontWeight?: number;
  fontSize?: string;
  color?: string;
  width?: number;
  align?: string;
  backgroundColor?: string;
};
type YAxisLabelOption = {
  formatter: (category: string) => string;
  rich: Record<string, YAxisLabelRichStyle>;
};
const bold = (category: string) => `{${Y_AXIS_LABEL_STYLE_NAMES.bold}|${category}}`;
const regular = (category: string) => `{${Y_AXIS_LABEL_STYLE_NAMES.regular}|${category}}`;
import { ForestPlotChart, computeInitialHeight } from './forest-plot-chart';
import { CI_LINE_SERIES_ID, DOT_SERIES_ID, resolveItemColor } from './forest-plot-series';

jest.mock('../utils', () => ({
  initChart: jest.fn(),
  setNoDataOption: jest.fn(),
  buildTooltip: jest.fn((base) => base),
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

describe('computeXTickPositions', () => {
  const round2 = (arr: number[]) => arr.map((v) => Math.round(v * 100) / 100);

  it('anchors ticks to multiples of interval, not to xMin', () => {
    // The user-visible bug this guards against: with xMin -0.1 and interval 0.2,
    // anchoring to xMin would yield -0.1, 0.1, 0.3, 0.5, 0.7. We want the round values.
    expect(round2(computeXTickPositions(-0.1, 0.7, 0.2))).toEqual([0, 0.2, 0.4, 0.6]);
  });

  it('includes ticks at the bounds when they land on a multiple', () => {
    expect(round2(computeXTickPositions(0, 1, 0.2))).toEqual([0, 0.2, 0.4, 0.6, 0.8, 1]);
  });

  it('handles negative ranges', () => {
    expect(round2(computeXTickPositions(-0.6, 0.6, 0.2))).toEqual([
      -0.6, -0.4, -0.2, 0, 0.2, 0.4, 0.6,
    ]);
  });

  it('returns an empty array for non-positive intervals', () => {
    expect(computeXTickPositions(0, 1, 0)).toEqual([]);
    expect(computeXTickPositions(0, 1, -0.1)).toEqual([]);
  });

  it('rounds away floating-point drift so ticks match nominal values', () => {
    // Without rounding, 3 * 0.2 evaluates to 0.6000000000000001, which is > xMax = 0.6
    // and can cause ECharts to drop the max tick or render it just past the axis line.
    const positions = computeXTickPositions(0, 0.6, 0.2);
    expect(positions).toEqual([0, 0.2, 0.4, 0.6]);
    expect(positions[positions.length - 1]).toBe(0.6);
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

describe('measureWidestBoldLabel', () => {
  const labelStyle = { fontSize: '12px' };

  function measure(text: string, fontWeight: number): number {
    const context = document.createElement('canvas').getContext('2d') as CanvasRenderingContext2D;
    context.font = `${fontWeight} 12px ${getChartFontFamily()}`;
    return context.measureText(text).width;
  }

  it('measures the widest category, set in bold', () => {
    const categories = ['IN', 'VASCULAR', 'OPC'];
    expect(measureWidestBoldLabel(categories, labelStyle)).toBe(
      Math.ceil(measure('VASCULAR', 700)),
    );
  });

  it('is at least as wide as the widest category in its regular weight', () => {
    const width = measureWidestBoldLabel(['VASCULAR'], labelStyle) ?? 0;
    expect(width).toBeGreaterThanOrEqual(measure('VASCULAR', 400));
  });
});

describe('ForestPlotChart', () => {
  const mockSetOption = jest.fn();
  const mockDispose = jest.fn();
  const mockOn = jest.fn();
  const mockOff = jest.fn();
  const mockDispatchAction = jest.fn();
  const mockChart = {
    setOption: mockSetOption,
    dispose: mockDispose,
    on: mockOn,
    off: mockOff,
    dispatchAction: mockDispatchAction,
  };

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

    it('overrides only the grid sides given in gridMargins', () => {
      const gridMargins = { right: 10, bottom: 24 };
      makeChart({ xAxisTitle: 'Effect Size', gridMargins });
      const grid = getOption().grid as { top: number; right: number; bottom: number };
      expect(grid).toMatchObject(gridMargins);
      expect(grid.top).toBe(GRID_TOP.withoutTitle);
    });

    it('uses X_AXIS_TITLE_GAP as grid bottom when x-axis title is provided', () => {
      makeChart({ xAxisTitle: 'Log FC' });
      expect((getOption().grid as { bottom: number }).bottom).toBe(50);
    });

    it('keeps the CI lines silent unless they show their point tooltip', () => {
      makeChart();
      const ciLines = (getOption().series as { id?: string; silent?: boolean }[]).find(
        (series) => series.id === CI_LINE_SERIES_ID,
      );
      expect(ciLines?.silent).toBe(true);
      expect(mockOn).not.toHaveBeenCalled();
    });

    it('shows the point tooltip from its CI line when showCILineTooltip is set', () => {
      makeChart({ showCILineTooltip: true });
      const series = getOption().series as { id?: string; silent?: boolean }[];
      expect(series.find((s) => s.id === CI_LINE_SERIES_ID)?.silent).toBe(false);
      expect(mockOn).toHaveBeenCalledWith(
        'mousemove',
        { seriesId: CI_LINE_SERIES_ID },
        expect.any(Function),
      );
    });

    it('points the CI line tooltip at the point series', () => {
      makeChart({ showCILineTooltip: true });
      const series = getOption().series as { id?: string }[];
      const onMouseMove = mockOn.mock.calls.find(([name]) => name === 'mousemove')?.[2];
      onMouseMove({ dataIndex: 0 });
      expect(mockDispatchAction).toHaveBeenCalledWith({
        type: 'showTip',
        seriesIndex: series.findIndex((s) => s.id === DOT_SERIES_ID),
        dataIndex: 0,
      });
    });

    it('renders 3 series by default (zero line, CI line, dots)', () => {
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

    it('passes the raw category to yAxisLabelTooltipFormatter when the label is bold', () => {
      const selectedCategory = forestPlotItems[1].yAxisCategory;
      const yAxisLabelTooltipFormatter = jest.fn((category: string) => `Tissue ${category}`);
      makeChart({ selectedYAxisCategory: selectedCategory, yAxisLabelTooltipFormatter });
      const yAxis = getOption().yAxis as {
        tooltip: { formatter: (params: CallbackDataParams) => string };
      };
      const boldLabelParams = {
        name: bold(selectedCategory),
        value: selectedCategory,
      } as CallbackDataParams;
      expect(yAxis.tooltip.formatter(boldLabelParams)).toBe(`Tissue ${selectedCategory}`);
      expect(yAxisLabelTooltipFormatter).toHaveBeenCalledWith(selectedCategory);
    });

    it('uses default dot tooltip when no pointTooltipFormatter', () => {
      makeChart();
      const series = getOption().series as {
        type: string;
        tooltip?: { formatter: (p: CallbackDataParams) => string };
      }[];
      const dotSeries = series.find((s) => s.type === 'scatter') as {
        tooltip: { formatter: (p: CallbackDataParams) => string };
      };
      const result = dotSeries.tooltip.formatter({ dataIndex: 0 } as CallbackDataParams);
      expect(result).toBe(`Value: ${forestPlotItems[0].value}`);
    });

    it('uses custom pointTooltipFormatter when provided', () => {
      const formatter = (item: ForestPlotItem) => `custom: ${item.yAxisCategory}`;
      makeChart({ pointTooltipFormatter: formatter });
      const series = getOption().series as {
        type: string;
        tooltip?: { formatter: (p: CallbackDataParams) => string };
      }[];
      const dotSeries = series.find((s) => s.type === 'scatter') as {
        tooltip: { formatter: (p: CallbackDataParams) => string };
      };
      const result = dotSeries.tooltip.formatter({ dataIndex: 0 } as CallbackDataParams);
      expect(result).toBe(`custom: ${forestPlotItems[0].yAxisCategory}`);
    });

    it('handles array params from axis-pointer-triggered tooltips', () => {
      // When top-level axisPointer with triggerOn: 'mousemove' is active, ECharts
      // can invoke series tooltip formatters with an array of params instead of one.
      makeChart();
      const series = getOption().series as {
        type: string;
        tooltip?: { formatter: (p: CallbackDataParams | CallbackDataParams[]) => string };
      }[];
      const dotSeries = series.find((s) => s.type === 'scatter') as {
        tooltip: { formatter: (p: CallbackDataParams | CallbackDataParams[]) => string };
      };
      const result = dotSeries.tooltip.formatter([{ dataIndex: 1 } as CallbackDataParams]);
      expect(result).toBe(`Value: ${forestPlotItems[1].value}`);
    });
  });

  describe('setOptions -- QTL styling', () => {
    it('omits zero line series when showZeroLine is false', () => {
      makeChart({ showZeroLine: false });
      expect((getOption().series as unknown[]).length).toBe(2);
    });

    it('lets ECharts pick x-axis ticks via splitNumber when no xAxisInterval is provided', () => {
      // Default mode: customValues is not set; ECharts uses its own "nice number"
      // algorithm to pick round tick values from the configured splitNumber.
      makeChart();
      const xAxis = getOption().xAxis as {
        splitNumber?: number;
        axisTick?: { customValues?: number[] };
        axisLabel: { customValues?: number[] };
      };
      expect(xAxis.splitNumber).toBe(10);
      expect(xAxis.axisTick).toBeUndefined();
      expect(xAxis.axisLabel.customValues).toBeUndefined();
    });

    it('anchors x-axis ticks to round multiples of xAxisInterval', () => {
      makeChart({ xAxisMin: -0.1, xAxisMax: 0.7, xAxisInterval: 0.2 });
      const xAxis = getOption().xAxis as {
        axisLabel: { customValues: number[] };
        axisTick: { customValues: number[] };
      };
      // 0.2 anchored within [-0.1, 0.7] yields [0, 0.2, 0.4, 0.6], not the
      // xMin-anchored [-0.1, 0.1, 0.3, 0.5, 0.7]
      const rounded = xAxis.axisLabel.customValues.map((v) => Math.round(v * 100) / 100);
      expect(rounded).toEqual([0, 0.2, 0.4, 0.6]);
      expect(xAxis.axisTick.customValues).toEqual(xAxis.axisLabel.customValues);
    });

    it('falls back to ECharts splitNumber when xAxisInterval is non-positive', () => {
      // 0 (or negative) would otherwise produce an empty customValues array via
      // computeXTickPositions, leaving the chart with no x-axis labels at all. Falling
      // back to splitNumber matches the no-interval default so the axis stays labelled.
      makeChart({ xAxisInterval: 0 });
      const xAxis = getOption().xAxis as {
        splitNumber?: number;
        axisTick?: { customValues?: number[] };
        axisLabel: { customValues?: number[] };
      };
      expect(xAxis.splitNumber).toBe(10);
      expect(xAxis.axisTick).toBeUndefined();
      expect(xAxis.axisLabel.customValues).toBeUndefined();
    });

    it('hides min/max x-axis labels when bounds are auto-computed', () => {
      // Auto-bounds (±maxAbs * 1.1) sit just past the data; suppress edge labels so
      // they don't clip against the axis-line end if ECharts places a tick there.
      makeChart();
      const xAxis = getOption().xAxis as {
        axisLabel: { showMinLabel: boolean; showMaxLabel: boolean };
      };
      expect(xAxis.axisLabel.showMinLabel).toBe(false);
      expect(xAxis.axisLabel.showMaxLabel).toBe(false);
    });

    it('shows every x-axis label when xAxisMin and xAxisMax are both explicit', () => {
      makeChart({ xAxisMin: -0.1, xAxisMax: 0.7, xAxisInterval: 0.2 });
      const xAxis = getOption().xAxis as {
        axisLabel: { showMinLabel?: boolean; showMaxLabel?: boolean };
      };
      expect(xAxis.axisLabel.showMinLabel).toBeUndefined();
      expect(xAxis.axisLabel.showMaxLabel).toBeUndefined();
    });

    it('overrides x-axis line style when xAxisLineStyle is provided', () => {
      makeChart({ xAxisLineStyle: { width: 1, color: '#22252A' } });
      const xAxis = getOption().xAxis as {
        axisLine: { lineStyle: { width: number; color: string } };
      };
      expect(xAxis.axisLine.lineStyle.width).toBe(1);
      expect(xAxis.axisLine.lineStyle.color).toBe('#22252A');
    });

    it('passes the axis line cap through so the two axis lines can meet in a square corner', () => {
      const frameLineStyle = { width: 1, color: '#22252A', cap: 'butt' as const };
      makeChart({ xAxisLineStyle: frameLineStyle, yAxisLineStyle: frameLineStyle });
      const xAxis = getOption().xAxis as { axisLine: { lineStyle: { cap?: string } } };
      const yAxis = getOption().yAxis as { axisLine: { lineStyle: { cap?: string } } };
      expect(xAxis.axisLine.lineStyle.cap).toBe('butt');
      expect(yAxis.axisLine.lineStyle.cap).toBe('butt');
    });

    it('sets the distance from each axis line to its tick labels', () => {
      const xAxisLabelMargin = 14;
      const yAxisLabelMargin = 9;
      makeChart({
        xAxisTickLabelStyle: { margin: xAxisLabelMargin },
        yAxisTickLabelStyle: { margin: yAxisLabelMargin },
      });
      const xAxis = getOption().xAxis as { axisLabel: { margin: number } };
      const yAxis = getOption().yAxis as { axisLabel: { margin: number } };
      expect(xAxis.axisLabel.margin).toBe(xAxisLabelMargin);
      expect(yAxis.axisLabel.margin).toBe(yAxisLabelMargin);
    });

    it('hides the y-axis line by default', () => {
      makeChart();
      const yAxis = getOption().yAxis as { axisLine: { show: boolean } };
      expect(yAxis.axisLine.show).toBe(false);
    });

    it('shows and styles the y-axis line when yAxisLineStyle is provided', () => {
      makeChart({ yAxisLineStyle: { width: 1, color: '#22252A' } });
      const yAxis = getOption().yAxis as {
        axisLine: { show: boolean; onZero: boolean; lineStyle: { width: number; color: string } };
      };
      expect(yAxis.axisLine.show).toBe(true);
      // Anchored at the left edge, not at x=0
      expect(yAxis.axisLine.onZero).toBe(false);
      expect(yAxis.axisLine.lineStyle).toEqual({ width: 1, color: '#22252A' });
    });

    it('applies x-axis tick label style without affecting the y-axis', () => {
      makeChart({
        xAxisTickLabelStyle: { fontSize: '12px', fontWeight: 400, color: '#4A5056' },
      });
      const xAxis = getOption().xAxis as { axisLabel: { fontSize: string; color: string } };
      const yAxis = getOption().yAxis as { axisLabel: { color: string } };
      expect(xAxis.axisLabel.fontSize).toBe('12px');
      expect(xAxis.axisLabel.color).toBe('#4A5056');
      expect(yAxis.axisLabel.color).toBe('#000');
    });

    it('applies y-axis tick label style without affecting the x-axis', () => {
      makeChart({
        yAxisTickLabelStyle: { fontSize: '16px', fontWeight: 400, color: '#353A3F' },
      });
      const xAxis = getOption().xAxis as { axisLabel: { color: string } };
      const yAxis = getOption().yAxis as { axisLabel: { fontSize: string; color: string } };
      expect(yAxis.axisLabel.fontSize).toBe('16px');
      expect(yAxis.axisLabel.color).toBe('#353A3F');
      expect(xAxis.axisLabel.color).toBe('#000');
    });

    it('uses a single x-axis by default', () => {
      makeChart();
      expect(Array.isArray(getOption().xAxis)).toBe(false);
    });

    it('renders bottom and top x-axes when showXAxisLabelsOnTop is true', () => {
      makeChart({ showXAxisLabelsOnTop: true });
      const xAxes = getOption().xAxis as {
        min: number;
        max: number;
        position?: string;
        axisLine: { show: boolean };
        axisTick: { show: boolean };
      }[];
      expect(Array.isArray(xAxes)).toBe(true);
      expect(xAxes.length).toBe(2);
      expect(xAxes[1].position).toBe('top');
      expect(xAxes[0].min).toBe(xAxes[1].min);
      expect(xAxes[0].max).toBe(xAxes[1].max);
      // Top axis carries labels only -- no line or ticks
      expect(xAxes[1].axisLine.show).toBe(false);
      expect(xAxes[1].axisTick.show).toBe(false);
    });

    it('always hides axis split lines (grid lines come from gridLineSeries)', () => {
      makeChart({
        xAxisGridLineStyle: { width: 1, color: '#D0D4D9', type: 'dotted' },
        yAxisGridLineStyle: { width: 1, color: '#F1F2F4', type: 'solid' },
      });
      const xAxis = getOption().xAxis as { splitLine: { show: boolean } };
      const yAxis = getOption().yAxis as { splitLine: { show: boolean } };
      expect(xAxis.splitLine.show).toBe(false);
      expect(yAxis.splitLine.show).toBe(false);
    });

    it('omits gridLineSeries when no grid line styles are provided', () => {
      makeChart();
      const series = getOption().series as { type: string; data?: unknown[] }[];
      // Default series: zero line + CI line + dots; no grid line series
      expect(series.length).toBe(3);
    });

    it('inserts a grid line custom series when a grid line style is provided', () => {
      makeChart({ yAxisGridLineStyle: { width: 1, color: '#F1F2F4', type: 'solid' } });
      const series = getOption().series as { type: string }[];
      // zero line + grid line + CI line + dots
      expect(series.length).toBe(4);
    });

    it('configures a y-axis line axisPointer when rowHoverHighlightStyle is provided', () => {
      makeChart({
        rowHoverHighlightStyle: { backgroundColor: 'rgba(158, 158, 158, 0.15)', thickness: 13 },
      });
      const yAxis = getOption().yAxis as {
        axisPointer: {
          show: boolean;
          type: string;
          lineStyle: { width: number; color: string };
        };
      };
      expect(yAxis.axisPointer.show).toBe(true);
      expect(yAxis.axisPointer.type).toBe('line');
      expect(yAxis.axisPointer.lineStyle.width).toBe(13);
      expect(yAxis.axisPointer.lineStyle.color).toBe('rgba(158, 158, 158, 0.15)');
    });

    it('enables a top-level axisPointer with mousemove trigger for row hover', () => {
      makeChart({
        rowHoverHighlightStyle: { backgroundColor: 'rgba(0,0,0,0.1)', thickness: 10 },
      });
      const axisPointer = getOption().axisPointer as { show: boolean; triggerOn: string };
      expect(axisPointer.show).toBe(true);
      expect(axisPointer.triggerOn).toBe('mousemove');
    });

    it('does not configure axisPointer when rowHoverHighlightStyle is omitted', () => {
      makeChart();
      const yAxis = getOption().yAxis as { axisPointer: { show: boolean } };
      expect(yAxis.axisPointer.show).toBe(false);
      expect(getOption().axisPointer).toBeUndefined();
    });

    it('registers an updateAxisPointer listener when rowHoverHighlightStyle is provided', () => {
      makeChart({
        rowHoverHighlightStyle: { backgroundColor: 'rgba(0,0,0,0.1)', thickness: 13 },
      });
      expect(mockOn).toHaveBeenCalledWith('updateAxisPointer', expect.any(Function));
    });

    it('does not register an updateAxisPointer listener by default', () => {
      makeChart();
      expect(mockOn).not.toHaveBeenCalled();
    });

    it('configures rich text and an initial formatter on y-axis label when row hover is enabled', () => {
      makeChart({
        rowHoverHighlightStyle: { backgroundColor: 'rgba(0,0,0,0.1)', thickness: 13 },
        yAxisTickLabelStyle: { fontSize: '18px', color: '#4A5056' },
      });
      const { axisLabel } = getOption().yAxis as { axisLabel: YAxisLabelOption };
      // No category is hovered initially, so the formatter renders the regular style
      expect(axisLabel.formatter('ACC')).toBe(regular('ACC'));
      expect(axisLabel.rich[Y_AXIS_LABEL_STYLE_NAMES.bold].fontWeight).toBe(700);
      // Both styles inherit typography from yAxisTickLabelStyle
      for (const styleName of Object.values(Y_AXIS_LABEL_STYLE_NAMES)) {
        expect(axisLabel.rich[styleName]).toMatchObject({ fontSize: '18px', color: '#4A5056' });
      }
    });

    it('bolds the selected y-axis category without row hover enabled', () => {
      const selectedCategory = forestPlotItems[1].yAxisCategory;
      makeChart({ selectedYAxisCategory: selectedCategory });
      const { axisLabel } = getOption().yAxis as { axisLabel: YAxisLabelOption };
      expect(axisLabel.formatter(selectedCategory)).toBe(bold(selectedCategory));
      expect(axisLabel.formatter(forestPlotItems[0].yAxisCategory)).toBe(
        regular(forestPlotItems[0].yAxisCategory),
      );
      expect(axisLabel.rich[Y_AXIS_LABEL_STYLE_NAMES.bold].fontWeight).toBe(700);
      expect(mockOn).not.toHaveBeenCalled();
    });

    it('keeps every y-axis label the width of the widest bold label, so hovering does not shift the plot', () => {
      const yAxisTickLabelStyle = { fontSize: '12px' };
      makeChart({
        rowHoverHighlightStyle: { backgroundColor: 'rgba(0,0,0,0.1)', thickness: 13 },
        yAxisTickLabelStyle,
      });
      const { axisLabel } = getOption().yAxis as { axisLabel: YAxisLabelOption };
      const categories = forestPlotItems.map((item) => item.yAxisCategory);
      const widestBoldLabel = measureWidestBoldLabel(categories, yAxisTickLabelStyle);
      // ECharts sizes the plot by each label's box; a background makes that box the fixed width.
      for (const styleName of Object.values(Y_AXIS_LABEL_STYLE_NAMES)) {
        expect(axisLabel.rich[styleName]).toMatchObject({
          width: widestBoldLabel,
          align: 'right',
          backgroundColor: 'transparent',
        });
      }
    });

    it('leaves y-axis labels unformatted without a selected category or row hover', () => {
      makeChart();
      const yAxis = getOption().yAxis as { axisLabel: { formatter?: unknown; rich?: unknown } };
      expect(yAxis.axisLabel.formatter).toBeUndefined();
      expect(yAxis.axisLabel.rich).toBeUndefined();
      expect((yAxis.axisLabel as { width?: number }).width).toBeUndefined();
    });
  });
});
