import { Directive, ElementRef, inject, Input, OnChanges, OnDestroy, OnInit } from '@angular/core';
import {
  BoxplotChart,
  BoxplotProps,
  CategoryBoxplotSummary,
  CategoryPoint,
} from '@sagebionetworks/shared/charts';
import { CallbackDataParams } from 'echarts/types/dist/shared';

@Directive({
  selector: '[sageBoxplot]',
  standalone: true,
})
export class BoxplotDirective implements OnChanges, OnInit, OnDestroy {
  private readonly el = inject(ElementRef);

  boxplot: BoxplotChart | undefined;

  @Input({ required: true }) points: CategoryPoint[] = [];
  @Input() summaries: CategoryBoxplotSummary[] | undefined;
  @Input() title = '';
  @Input() xAxisTitle: string | undefined;
  @Input() xAxisLabelFormatter: undefined | ((value: string) => string);
  @Input() xAxisCategories: undefined | string[];
  @Input() yAxisTitle = '';
  @Input() yAxisMin: number | undefined;
  @Input() yAxisMax: number | undefined;
  @Input() xAxisLabelTooltipFormatter: ((params: CallbackDataParams) => string) | undefined;
  @Input() pointTooltipFormatter:
    | undefined
    | ((pt: CategoryPoint, params: CallbackDataParams) => string);
  @Input() pointCategoryColors: undefined | Record<string, string>;
  @Input() pointCategoryShapes: undefined | Record<string, string>;
  @Input() showLegend: undefined | boolean;
  @Input() pointOpacity: undefined | number;
  @Input() noDataStyle: undefined | 'textOnly' | 'grayBackground';

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
      xAxisLabelFormatter: this.xAxisLabelFormatter,
      xAxisCategories: this.xAxisCategories,
      yAxisTitle: this.yAxisTitle,
      yAxisMin: this.yAxisMin,
      yAxisMax: this.yAxisMax,
      xAxisLabelTooltipFormatter: this.xAxisLabelTooltipFormatter,
      pointTooltipFormatter: this.pointTooltipFormatter,
      pointCategoryColors: this.pointCategoryColors,
      pointCategoryShapes: this.pointCategoryShapes,
      showLegend: this.showLegend,
      pointOpacity: this.pointOpacity,
      noDataStyle: this.noDataStyle,
    };
  }
}
