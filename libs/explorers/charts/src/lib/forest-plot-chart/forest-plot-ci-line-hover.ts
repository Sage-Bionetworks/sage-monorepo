import { ECharts } from 'echarts';
import { CI_LINE_SERIES_ID } from './forest-plot-series';

// mousemove rather than mouseover: ECharts doesn't emit mouseover for the line's transparent
// hover area, only for the visible line.
const MOUSEMOVE = 'mousemove';
const MOUSEOUT = 'mouseout';

type SeriesMouseEvent = { dataIndex: number };

// Shows a row's point tooltip while the pointer is on that row's CI line, so the line and its
// point share one tooltip, pinned to the point.
//
// Lifecycle: construct -> attach(dotSeriesIndex) after setOption -> detach() before the next
// setOption or chart dispose.
export class CILineHoverController {
  private onMouseMove: ((event: unknown) => void) | null = null;
  private onMouseOut: (() => void) | null = null;

  constructor(private readonly chart: ECharts) {}

  attach(dotSeriesIndex: number) {
    this.detach();
    this.onMouseMove = (event: unknown) => {
      const { dataIndex } = event as SeriesMouseEvent;
      this.chart.dispatchAction({ type: 'showTip', seriesIndex: dotSeriesIndex, dataIndex });
    };
    this.onMouseOut = () => this.chart.dispatchAction({ type: 'hideTip' });
    this.chart.on(MOUSEMOVE, { seriesId: CI_LINE_SERIES_ID }, this.onMouseMove);
    this.chart.on(MOUSEOUT, { seriesId: CI_LINE_SERIES_ID }, this.onMouseOut);
  }

  detach() {
    if (this.onMouseMove) this.chart.off(MOUSEMOVE, this.onMouseMove);
    if (this.onMouseOut) this.chart.off(MOUSEOUT, this.onMouseOut);
    this.onMouseMove = null;
    this.onMouseOut = null;
  }
}
