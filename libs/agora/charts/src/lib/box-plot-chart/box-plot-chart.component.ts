/* eslint-disable @typescript-eslint/no-empty-function */
/* eslint-disable @typescript-eslint/no-this-alias */
// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Component, inject, Input } from '@angular/core';
import * as d3 from 'd3';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
// import { agoraBoxPlot } from './box-plot';
import { boxPlotChartItem } from '@sagebionetworks/agora/models';
import { HelperService } from '@sagebionetworks/agora/services';
import { BaseChartComponent } from '../base-chart/base-chart.component';
import { BoxplotDirective } from '@sagebionetworks/shared/charts-angular';

// -------------------------------------------------------------------------- //
// Component
// -------------------------------------------------------------------------- //
@Component({
  selector: 'agora-box-plot-chart',
  imports: [BoxplotDirective],
  providers: [HelperService],
  templateUrl: './box-plot-chart.component.html',
  styleUrls: ['./box-plot-chart.component.scss'],
})
export class BoxPlotComponent extends BaseChartComponent {
  helperService = inject(HelperService);

  _data: boxPlotChartItem[] = [];
  get data(): boxPlotChartItem[] {
    return this._data;
  }
  @Input() set data(data: boxPlotChartItem[]) {
    this._data = data;
    this.init();
  }

  @Input() xAxisLabel = '';
  @Input() yAxisLabel = 'LOG 2 FOLD CHANGE';
  @Input() yAxisMin: number | undefined;
  @Input() yAxisMax: number | undefined;
  @Input() yAxisPadding = 0.2;
  @Input() rcRadius = 9;
  @Input() rcColor = this.helperService.getColor('secondary');

  override name = 'box-plot-chart';
  dimension: any;
  group: any;
  min = 0;
  max = 0;

  override init() {
    if (!this._data?.length || !this.chartContainer?.nativeElement) {
      return;
    }

    this.initData();

    if (!this.chart) {
      this.initChart();
    } else {
      this.hideCircles();
      this.chart.redraw();
    }

    this.isInitialized = true;
  }

  initData() {
    const self = this;

    this.group = {
      all: () => {
        return self._data;
      },
      order: () => {},
      top: () => {},
    };

    this.dimension = {
      filter: () => {},
      filterAll: () => {},
    };
  }

  initChart() {
    const self = this;

    // this.chart = agoraBoxPlot(this.chartContainer.nativeElement, null, {
    //   yAxisMin: this.yAxisMin ? this.yAxisMin - this.yAxisPadding : undefined,
    //   yAxisMax: this.yAxisMax ? this.yAxisMax + this.yAxisPadding : undefined,
    // });

    this.chart.group(this.group).dimension(this.dimension);

    this.chart.elasticX(true).xAxis().tickSizeOuter([0]);

    this.chart
      .elasticY(true)
      .yAxisLabel(this.yAxisLabel, 20)
      .yRangePadding(this.rcRadius * 1.5)
      .yAxis()
      .ticks(8);
    //.tickSizeOuter([0]);

    this.chart
      .renderTitle(false)
      .showOutliers(0)
      .dataWidthPortion(0.1)
      .dataOpacity(0)
      .colors('transparent')
      .tickFormat(() => '');

    this.chart.margins({
      left: 90,
      right: 0,
      bottom: 50,
      top: 10,
    });

    this.chart.on('renderlet', function () {
      self.renderCircles();
      self.addXAxisTooltips();
    });

    this.chart.filter = () => '';
    this.chart.render();
  }

  renderCircles() {
    const self = this;
    const tooltip = this.getTooltip('internal', 'chart-value-tooltip box-plot-chart-value-tooltip');

    const height = this.chartContainer.nativeElement.offsetHeight;
    const lineCenter = this.chart.selectAll('line.center');
    const yDomainLength = Math.abs(this.chart.yAxisMax() - this.chart.yAxisMin());
    const mult = (height - 60) / yDomainLength;

    this.chart.selectAll('circle').remove();

    this.chart.selectAll('g.box').each(function (this: HTMLElement, el: any, i: number) {
      if (!self.data[i]['circle']) {
        return;
      }

      const data = self.data[i]['circle'];
      const cy = Math.abs(self.chart.y().domain()[1] - data['value']) * mult;
      const circle = d3.select(this).insert('circle', ':last-child');

      circle
        .attr('fill', self.rcColor)
        .attr('r', self.rcRadius)
        .attr('cx', lineCenter.attr('x1'))
        .attr('cy', isNaN(cy) ? 0.0 : cy)
        .style('stroke-width', 0)
        .style('opacity', 0)
        .style('transition', 'all .3s');

      circle
        .on('mouseover', function () {
          if (!data['tooltip']) {
            return;
          }

          const offset = self.helperService.getOffset(this);

          tooltip
            .html(data['tooltip'])
            .style('left', (offset?.left || 0) + 'px')
            .style('top', (offset?.top || 0) + 'px');

          self.showTooltip('internal');
        })
        .on('mouseout', function () {
          self.hideTooltip('internal');
        });
    });

    setTimeout(() => {
      self.showCircles();
    }, 1);
  }

  hideCircles() {
    this.chart.selectAll('circle').style('opacity', 0);
  }

  showCircles() {
    this.chart.selectAll('circle').style('opacity', 1);
  }

  override getXAxisTooltipText(text: string) {
    return this.helperService.getGCTColumnTooltipText(text);
  }
}
