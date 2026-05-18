import { ECharts } from 'echarts';
import { RowHoverController } from './forest-plot-row-hover';

describe('RowHoverController', () => {
  const yAxisCategories = ['ACC', 'CBE', 'DLPFC', 'TCX'];
  let mockOn: jest.Mock;
  let mockOff: jest.Mock;
  let mockSetOption: jest.Mock;
  let mockChart: ECharts;
  let controller: RowHoverController;

  beforeEach(() => {
    mockOn = jest.fn();
    mockOff = jest.fn();
    mockSetOption = jest.fn();
    mockChart = {
      on: mockOn,
      off: mockOff,
      setOption: mockSetOption,
    } as unknown as ECharts;
    controller = new RowHoverController(mockChart, yAxisCategories);
  });

  describe('getYAxisLabelFormatter', () => {
    it('returns the bare category when nothing is hovered', () => {
      const fmt = controller.getYAxisLabelFormatter();
      expect(fmt('ACC')).toBe('ACC');
      expect(fmt('TCX')).toBe('TCX');
    });
  });

  describe('attach', () => {
    it('registers an updateAxisPointer listener', () => {
      controller.attach();
      expect(mockOn).toHaveBeenCalledTimes(1);
      expect(mockOn).toHaveBeenCalledWith('updateAxisPointer', expect.any(Function));
    });

    it('detaches the previous listener when called again', () => {
      controller.attach();
      const firstListener = mockOn.mock.calls[0][1];
      controller.attach();
      expect(mockOff).toHaveBeenCalledWith('updateAxisPointer', firstListener);
      expect(mockOn).toHaveBeenCalledTimes(2);
    });
  });

  describe('detach', () => {
    it('removes the attached listener', () => {
      controller.attach();
      const listener = mockOn.mock.calls[0][1];
      controller.detach();
      expect(mockOff).toHaveBeenCalledWith('updateAxisPointer', listener);
    });

    it('is a no-op when nothing is attached', () => {
      controller.detach();
      expect(mockOff).not.toHaveBeenCalled();
    });

    it('is a no-op on a second call', () => {
      controller.attach();
      controller.detach();
      controller.detach();
      expect(mockOff).toHaveBeenCalledTimes(1);
    });
  });

  describe('updateAxisPointer listener', () => {
    let listener: (event: unknown) => void;

    beforeEach(() => {
      controller.attach();
      listener = mockOn.mock.calls[0][1];
    });

    it('translates a numeric axis-pointer value to a category by index', () => {
      // value 2 -> yAxisCategories[2] -> 'DLPFC'
      listener({ axesInfo: [{ axisDim: 'y', value: 2 }] });
      expect(mockSetOption).toHaveBeenCalledTimes(1);
      const fmt = mockSetOption.mock.calls[0][0].yAxis.axisLabel.formatter;
      expect(fmt('DLPFC')).toBe('{b|DLPFC}');
      expect(fmt('ACC')).toBe('ACC');
    });

    it('rounds fractional numeric values to the nearest index', () => {
      // 1.6 -> Math.round = 2 -> 'DLPFC'
      listener({ axesInfo: [{ axisDim: 'y', value: 1.6 }] });
      const fmt = mockSetOption.mock.calls[0][0].yAxis.axisLabel.formatter;
      expect(fmt('DLPFC')).toBe('{b|DLPFC}');
    });

    it('uses a string value directly when it matches a known category', () => {
      listener({ axesInfo: [{ axisDim: 'y', value: 'CBE' }] });
      const fmt = mockSetOption.mock.calls[0][0].yAxis.axisLabel.formatter;
      expect(fmt('CBE')).toBe('{b|CBE}');
    });

    it('clears the hover when the string value is not in yAxisCategories', () => {
      listener({ axesInfo: [{ axisDim: 'y', value: 'ACC' }] });
      mockSetOption.mockClear();
      listener({ axesInfo: [{ axisDim: 'y', value: 'UNKNOWN' }] });
      expect(mockSetOption).toHaveBeenCalledTimes(1);
      const fmt = mockSetOption.mock.calls[0][0].yAxis.axisLabel.formatter;
      expect(fmt('ACC')).toBe('ACC');
    });

    it('clears the hover when the numeric value is out of range', () => {
      listener({ axesInfo: [{ axisDim: 'y', value: 'ACC' }] });
      mockSetOption.mockClear();
      listener({ axesInfo: [{ axisDim: 'y', value: 99 }] });
      expect(mockSetOption).toHaveBeenCalledTimes(1);
      const fmt = mockSetOption.mock.calls[0][0].yAxis.axisLabel.formatter;
      expect(fmt('ACC')).toBe('ACC');
    });

    it('clears the hover when axesInfo has no y-axis entry', () => {
      listener({ axesInfo: [{ axisDim: 'y', value: 'ACC' }] });
      mockSetOption.mockClear();
      listener({ axesInfo: [{ axisDim: 'x', value: 0.5 }] });
      expect(mockSetOption).toHaveBeenCalledTimes(1);
      const fmt = mockSetOption.mock.calls[0][0].yAxis.axisLabel.formatter;
      expect(fmt('ACC')).toBe('ACC');
    });

    it('ignores events with no y entry when nothing was previously hovered', () => {
      // hoveredCategory stays null, so no re-render is needed.
      listener({ axesInfo: [{ axisDim: 'x', value: 0.5 }] });
      expect(mockSetOption).not.toHaveBeenCalled();
    });

    it('ignores events with missing axesInfo', () => {
      listener({});
      expect(mockSetOption).not.toHaveBeenCalled();
    });

    it('does not call setOption when the hovered category is unchanged', () => {
      listener({ axesInfo: [{ axisDim: 'y', value: 'ACC' }] });
      mockSetOption.mockClear();
      listener({ axesInfo: [{ axisDim: 'y', value: 'ACC' }] });
      expect(mockSetOption).not.toHaveBeenCalled();
    });
  });
});
