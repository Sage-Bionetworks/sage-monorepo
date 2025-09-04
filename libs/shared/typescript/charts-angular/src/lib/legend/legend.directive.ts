import { Directive, ElementRef, inject, Input, OnChanges, OnDestroy, OnInit } from '@angular/core';
import { LegendChart, LegendProps, PointStyle } from '@sagebionetworks/shared/charts';

@Directive({
  selector: '[sageLegend]',
  standalone: true,
})
export class LegendDirective implements OnChanges, OnInit, OnDestroy {
  private readonly el = inject(ElementRef);

  legend: LegendChart | undefined;

  @Input({ required: true }) pointStyles: PointStyle[] = [];

  ngOnInit() {
    this.legend = new LegendChart(this.el.nativeElement, this.getLegendProps());
  }

  ngOnChanges(): void {
    this.legend?.setOptions(this.getLegendProps());
  }

  ngOnDestroy() {
    this.legend?.destroy();
  }

  private getLegendProps(): LegendProps {
    return {
      pointStyles: this.pointStyles,
    };
  }
}
