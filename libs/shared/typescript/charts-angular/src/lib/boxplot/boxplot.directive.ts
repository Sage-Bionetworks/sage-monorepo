import { Directive, ElementRef, Input, OnChanges, OnDestroy, OnInit } from '@angular/core';
import {
  BoxplotChart,
  BoxplotProps,
  CategoryBoxplotSummary,
  CategoryPoint,
} from '@sagebionetworks/shared/charts';

@Directive({
  selector: '[sageBoxplot]',
  standalone: true,
})
export class BoxplotDirective implements OnChanges, OnInit, OnDestroy {
  boxplot: BoxplotChart | undefined;

  @Input({ required: true }) points: CategoryPoint[] = [];
  @Input() summaries: CategoryBoxplotSummary[] | undefined;
  @Input() title = '';
  @Input() xAxisTitle = '';
  @Input() yAxisTitle = '';
  @Input() yAxisMin: number | undefined;
  @Input() yAxisMax: number | undefined;
  @Input() xAxisCategoryToTooltipText: Record<string, string> | undefined;
  @Input() pointTooltipFormatter: undefined | ((pt: CategoryPoint) => string);

  constructor(private el: ElementRef) {
    // add comment so constructor is not empty
  }

  ngOnInit() {
    this.boxplot = new BoxplotChart(this.el.nativeElement, this.getBoxplotProps());
  }

  ngOnChanges(): void {
    this.boxplot?.setOptions(this.getBoxplotProps());
  }

  ngOnDestroy() {
    this.boxplot?.destroy();
  }

  private getBoxplotProps(): BoxplotProps {
    return {
      points: this.points,
      summaries: this.summaries,
      title: this.title,
      xAxisTitle: this.xAxisTitle,
      yAxisTitle: this.yAxisTitle,
      yAxisMin: this.yAxisMin,
      yAxisMax: this.yAxisMax,
      xAxisCategoryToTooltipText: this.xAxisCategoryToTooltipText,
      pointTooltipFormatter: this.pointTooltipFormatter,
    };
  }
}
