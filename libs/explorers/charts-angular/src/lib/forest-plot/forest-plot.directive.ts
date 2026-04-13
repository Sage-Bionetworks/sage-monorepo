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
import { CallbackDataParams } from 'echarts/types/dist/shared';

@Directive({
  selector: '[sageForestPlot]',
  standalone: true,
})
export class ForestPlotDirective implements OnDestroy {
  private readonly el = inject(ElementRef);

  items = input.required<ForestPlotItem[]>();
  title = input<string | undefined>(undefined);
  xAxisTitle = input<string | undefined>(undefined);
  xAxisMin = input<number | undefined>(undefined);
  xAxisMax = input<number | undefined>(undefined);
  yAxisCategories = input<string[] | undefined>(undefined);
  yAxisLabelTooltipFormatter = input<((category: string) => string) | undefined>(undefined);
  pointTooltipFormatter = input<
    ((item: ForestPlotItem, params: CallbackDataParams) => string) | undefined
  >(undefined);
  ciLabelFormatter = input<((value: number) => string) | undefined>(undefined);
  defaultLineColor = input<string | undefined>(undefined);
  defaultPointColor = input<string | undefined>(undefined);
  pointSize = input<number | undefined>(undefined);
  showCILabels = input<boolean | undefined>(undefined);
  noDataStyle = input<'textOnly' | 'grayBackground' | undefined>(undefined);

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
