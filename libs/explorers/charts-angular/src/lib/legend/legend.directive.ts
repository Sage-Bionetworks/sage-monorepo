import { Directive, ElementRef, inject, Input, OnChanges, OnDestroy, OnInit } from '@angular/core';
import {
  ChartStyle,
  LegendChart,
  LegendProps,
  PointStyle,
  whenChartFontReady,
} from '@sagebionetworks/explorers/charts';

@Directive({
  selector: '[sageLegend]',
  standalone: true,
})
export class LegendDirective implements OnChanges, OnInit, OnDestroy {
  private readonly el = inject(ElementRef);

  legend: LegendChart | undefined;
  private destroyed = false;

  @Input({ required: true }) pointStyles: PointStyle[] = [];
  @Input() chartStyle: undefined | ChartStyle;

  ngOnInit() {
    whenChartFontReady(() => {
      if (this.destroyed) return;
      this.legend = new LegendChart(this.el.nativeElement, this.getLegendProps());
    });
  }

  ngOnChanges(): void {
    this.legend?.setOptions(this.getLegendProps());
  }

  ngOnDestroy() {
    this.destroyed = true;
    this.legend?.destroy();
  }

  private getLegendProps(): LegendProps {
    return {
      pointStyles: this.pointStyles,
      chartStyle: this.chartStyle,
    };
  }
}
