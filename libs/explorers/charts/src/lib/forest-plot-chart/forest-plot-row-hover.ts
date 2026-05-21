import { ECharts } from 'echarts';
import { UpdateAxisPointerEvent } from '../types';

const UPDATE_AXIS_POINTER = 'updateAxisPointer';

// Owns the "bolded label on the hovered row" interaction. ECharts' y-axis axisPointer
// paints the highlight band on its own; this controller listens for the corresponding
// updateAxisPointer event and rewrites the y-axis label formatter so the hovered
// category renders via the rich-text `b` style.
//
// Lifecycle: construct -> getYAxisLabelFormatter() -> attach() after setOption ->
// detach() before the next setOption or chart dispose.
export class RowHoverController {
  private hoveredCategory: string | null = null;
  private listener: ((event: unknown) => void) | null = null;

  constructor(
    private readonly chart: ECharts,
    private readonly yAxisCategories: string[],
  ) {}

  getYAxisLabelFormatter(): (cat: string) => string {
    const hovered = this.hoveredCategory;
    return (cat: string) => (cat === hovered ? `{b|${cat}}` : cat);
  }

  attach() {
    this.detach();
    this.listener = (rawEvent: unknown) => {
      const event = rawEvent as UpdateAxisPointerEvent;
      const yInfo = event.axesInfo?.find((a) => a.axisDim === 'y');
      const raw = yInfo?.value;
      let next: string | null = null;
      if (typeof raw === 'number') {
        next = this.yAxisCategories[Math.round(raw)] ?? null;
      } else if (typeof raw === 'string') {
        next = this.yAxisCategories.includes(raw) ? raw : null;
      }
      if (next === this.hoveredCategory) return;
      this.hoveredCategory = next;
      this.chart.setOption({
        yAxis: { axisLabel: { formatter: this.getYAxisLabelFormatter() } },
      });
    };
    this.chart.on(UPDATE_AXIS_POINTER, this.listener);
  }

  detach() {
    if (this.listener) {
      this.chart.off(UPDATE_AXIS_POINTER, this.listener);
      this.listener = null;
    }
  }
}
