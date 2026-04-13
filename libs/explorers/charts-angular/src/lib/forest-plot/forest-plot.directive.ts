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
  ForestPlotChart,
  ForestPlotItem,
  ForestPlotProps,
} from '@sagebionetworks/explorers/charts';

@Directive({
  selector: '[sageForestPlot]',
  standalone: true,
})
export class ForestPlotDirective implements OnDestroy {
  private readonly el = inject(ElementRef);

  items = input.required<ForestPlotItem[]>();
  title = input<string>();
  xAxisTitle = input<string>();
  xAxisMin = input<number>();
  xAxisMax = input<number>();
  yAxisCategories = input<string[]>();
  yAxisLabelTooltipFormatter = input<(category: string) => string>();
  pointTooltipFormatter = input<(item: ForestPlotItem) => string>();
  ciLabelFormatter = input<(value: number) => string>();
  defaultLineColor = input<string>();
  defaultPointColor = input<string>();
  pointSize = input<number>();
  showCILabels = input<boolean>();
  noDataStyle = input<'textOnly' | 'grayBackground'>();

  private forestPlot: ForestPlotChart | undefined;

  private readonly props = computed<ForestPlotProps>(() => ({
    items: this.items(),
    title: this.title(),
    xAxisTitle: this.xAxisTitle(),
    xAxisMin: this.xAxisMin(),
    xAxisMax: this.xAxisMax(),
    yAxisCategories: this.yAxisCategories(),
    yAxisLabelTooltipFormatter: this.yAxisLabelTooltipFormatter(),
    pointTooltipFormatter: this.pointTooltipFormatter(),
    ciLabelFormatter: this.ciLabelFormatter(),
    defaultLineColor: this.defaultLineColor(),
    defaultPointColor: this.defaultPointColor(),
    pointSize: this.pointSize(),
    showCILabels: this.showCILabels(),
    noDataStyle: this.noDataStyle(),
  }));

  constructor() {
    afterNextRender(() => {
      this.forestPlot = new ForestPlotChart(this.el.nativeElement, this.props());
    });

    effect(() => {
      const currentProps = this.props();
      this.forestPlot?.setOptions(currentProps);
    });
  }

  ngOnDestroy() {
    this.forestPlot?.destroy();
  }
}
