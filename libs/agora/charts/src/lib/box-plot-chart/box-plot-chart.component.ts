// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Component, inject, Input } from '@angular/core';
import { HelperService } from '@sagebionetworks/agora/services';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { boxPlotChartItem } from '@sagebionetworks/agora/models';
import { CategoryBoxplotSummary, CategoryPoint } from '@sagebionetworks/shared/charts';
import { BoxplotDirective } from '@sagebionetworks/shared/charts-angular';

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

  _data: boxPlotChartItem[] = [];
  points: CategoryPoint[] = [];
  summaries: CategoryBoxplotSummary[] = [];
  xAxisCategoryToTooltipText: Record<string, string> | undefined = {};
  isInitialized = false;
  pointTooltipFormatter: ((pt: CategoryPoint) => string) | undefined;

  get data(): boxPlotChartItem[] {
    return this._data;
  }
  @Input() set data(data: boxPlotChartItem[]) {
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
    this.xAxisCategoryToTooltipText = {};
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

      if (this.xAxisCategoryToTooltipText) {
        const tooltipText = this.getXAxisTooltipText(xAxisCategory);
        if (tooltipText !== '') {
          this.xAxisCategoryToTooltipText[xAxisCategory] = tooltipText;
        }
      }

      this.pointTooltipFormatter = (pt: CategoryPoint) => {
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
