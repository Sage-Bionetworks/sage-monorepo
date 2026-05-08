import {
  afterNextRender,
  computed,
  Directive,
  effect,
  ElementRef,
  inject,
  input,
  OnDestroy,
} from '@angular/core';
import {
  CandlestickChart,
  CandlestickItem,
  CandlestickProps,
} from '@sagebionetworks/explorers/charts';

@Directive({
  selector: '[sageCandlestick]',
  standalone: true,
})
export class CandlestickDirective implements OnDestroy {
  private readonly el = inject(ElementRef);

  items = input.required<CandlestickItem[]>();
  title = input<string>();
  xAxisTitle = input<string>();
  yAxisTitle = input<string>();
  yAxisMin = input<number>();
  yAxisMax = input<number>();
  xAxisCategories = input<string[]>();
  xAxisLabelTooltipFormatter = input<(category: string) => string>();
  pointTooltipFormatter = input<(item: CandlestickItem) => string>();
  defaultLineColor = input<string>();
  defaultPointColor = input<string>();
  pointSize = input<number>();
  referenceLineValue = input<number>();
  referenceLineColor = input<string>();
  noDataStyle = input<'textOnly' | 'grayBackground'>();

  private candlestick: CandlestickChart | undefined;

  private readonly props = computed<CandlestickProps>(() => ({
    items: this.items(),
    title: this.title(),
    xAxisTitle: this.xAxisTitle(),
    yAxisTitle: this.yAxisTitle(),
    yAxisMin: this.yAxisMin(),
    yAxisMax: this.yAxisMax(),
    xAxisCategories: this.xAxisCategories(),
    xAxisLabelTooltipFormatter: this.xAxisLabelTooltipFormatter(),
    pointTooltipFormatter: this.pointTooltipFormatter(),
    defaultLineColor: this.defaultLineColor(),
    defaultPointColor: this.defaultPointColor(),
    pointSize: this.pointSize(),
    referenceLineValue: this.referenceLineValue(),
    referenceLineColor: this.referenceLineColor(),
    noDataStyle: this.noDataStyle(),
  }));

  constructor() {
    afterNextRender(() => {
      this.candlestick = new CandlestickChart(this.el.nativeElement, this.props());
    });

    effect(() => {
      const currentProps = this.props();
      this.candlestick?.setOptions(currentProps);
    });
  }

  ngOnDestroy() {
    this.candlestick?.destroy();
  }
}
