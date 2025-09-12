// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Component, inject, Input } from '@angular/core';
import { HelperService } from '@sagebionetworks/agora/services';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { BoxPlotChartItem } from '@sagebionetworks/agora/models';
import { CategoryBoxplotSummary, CategoryPoint } from '@sagebionetworks/explorers/charts';
import { BoxplotDirective } from '@sagebionetworks/explorers/charts-angular';
import { CallbackDataParams } from 'echarts/types/dist/shared';

// -------------------------------------------------------------------------- //
// Component
// -------------------------------------------------------------------------- //
@Component({
  selector: 'agora-box-plot-chart',
  imports: [BoxplotDirective],
  templateUrl: './box-plot-chart.component.html',
  styleUrls: ['./box-plot-chart.component.scss'],
})
export class BoxPlotComponent {
  helperService = inject(HelperService);

  _data: BoxPlotChartItem[] = [];
  points: CategoryPoint[] = [];
  summaries: CategoryBoxplotSummary[] = [];
  xAxisLabelTooltipFormatter: ((params: CallbackDataParams) => string) | undefined;
  isInitialized = false;
  pointTooltipFormatter: ((pt: CategoryPoint, params: CallbackDataParams) => string) | undefined;

  get data(): BoxPlotChartItem[] {
    return this._data;
  }
  @Input() set data(data: BoxPlotChartItem[]) {
    this._data = data;
    this.init();
  }

  @Input() heading = '';
  @Input() xAxisLabel = '';
  @Input() yAxisLabel = 'LOG 2 FOLD CHANGE';
  @Input() yAxisMin: number | undefined;
  @Input() yAxisMax: number | undefined;
  @Input() plotHeight = '480px';

  reset() {
    this.points = [];
    this.summaries = [];
    this.xAxisLabelTooltipFormatter = undefined;
    this.isInitialized = false;
    this.pointTooltipFormatter = undefined;
  }

  init() {
    this.reset();

    if (!this._data?.length) {
      return;
    }

    this.initData();
    this.isInitialized = true;
  }

  initData() {
    this._data.forEach((item) => {
      const xAxisCategory = item.key;

      if (item.circle) {
        const point: CategoryPoint = {
          xAxisCategory: xAxisCategory,
          value: item.circle.value,
          text: item.circle.tooltip,
        };
        this.points.push(point);
      }

      const summary: CategoryBoxplotSummary = {
        xAxisCategory: xAxisCategory,
        min: item.value[0],
        firstQuartile: item.quartiles[0],
        median: item.value[1],
        thirdQuartile: item.quartiles[2],
        max: item.value[2],
      };
      this.summaries.push(summary);

      this.xAxisLabelTooltipFormatter = (params: CallbackDataParams) => {
        const xAxisCategory = params.name;
        return this.getXAxisTooltipText(xAxisCategory);
      };

      this.pointTooltipFormatter = (pt: CategoryPoint, params: CallbackDataParams) => {
        return pt.text ?? pt.value.toString();
      };
    });

    this.points.sort((a, b) => {
      return a.xAxisCategory.localeCompare(b.xAxisCategory);
    });

    this.summaries.sort((a, b) => {
      return a.xAxisCategory.localeCompare(b.xAxisCategory);
    });
  }

  getXAxisTooltipText(text: string) {
    return this.helperService.getGCTColumnTooltipText(text);
  }
}
