import { ECharts } from 'echarts';
import { CILineHoverController } from './forest-plot-ci-line-hover';
import { CI_LINE_SERIES_ID } from './forest-plot-series';

describe('CILineHoverController', () => {
  const dotSeriesIndex = 3;
  let mockOn: jest.Mock;
  let mockOff: jest.Mock;
  let mockDispatchAction: jest.Mock;
  let controller: CILineHoverController;

  function listenerFor(eventName: string): (event?: unknown) => void {
    const call = mockOn.mock.calls.find(([name]) => name === eventName);
    if (!call) throw new Error(`No ${eventName} listener registered`);
    return call[2];
  }

  beforeEach(() => {
    mockOn = jest.fn();
    mockOff = jest.fn();
    mockDispatchAction = jest.fn();
    const chart = { on: mockOn, off: mockOff, dispatchAction: mockDispatchAction };
    controller = new CILineHoverController(chart as unknown as ECharts);
  });

  it('listens for the pointer moving over and leaving the CI lines only', () => {
    controller.attach(dotSeriesIndex);
    expect(mockOn).toHaveBeenCalledWith(
      'mousemove',
      { seriesId: CI_LINE_SERIES_ID },
      expect.any(Function),
    );
    expect(mockOn).toHaveBeenCalledWith(
      'mouseout',
      { seriesId: CI_LINE_SERIES_ID },
      expect.any(Function),
    );
  });

  it("shows the hovered row's point tooltip when the pointer moves over its CI line", () => {
    controller.attach(dotSeriesIndex);
    const hoveredRow = 2;
    listenerFor('mousemove')({ dataIndex: hoveredRow });
    expect(mockDispatchAction).toHaveBeenCalledWith({
      type: 'showTip',
      seriesIndex: dotSeriesIndex,
      dataIndex: hoveredRow,
    });
  });

  it('hides the tooltip when the pointer leaves the CI line', () => {
    controller.attach(dotSeriesIndex);
    listenerFor('mouseout')();
    expect(mockDispatchAction).toHaveBeenCalledWith({ type: 'hideTip' });
  });

  it('removes its listeners on detach, including before re-attaching', () => {
    controller.attach(dotSeriesIndex);
    const onMouseMove = listenerFor('mousemove');
    const onMouseOut = listenerFor('mouseout');
    controller.attach(dotSeriesIndex);
    expect(mockOff).toHaveBeenCalledWith('mousemove', onMouseMove);
    expect(mockOff).toHaveBeenCalledWith('mouseout', onMouseOut);
  });

  it('does nothing on detach when not attached', () => {
    controller.detach();
    expect(mockOff).not.toHaveBeenCalled();
  });
});
