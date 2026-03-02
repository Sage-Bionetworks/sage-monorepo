/* eslint-disable @typescript-eslint/no-this-alias */
// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Component, inject, Input } from '@angular/core';
import crossfilter from 'crossfilter2';
import * as d3 from 'd3';
import * as dc from 'dc';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { MedianExpression } from '@sagebionetworks/agora/api-client';
import { HelperService } from '@sagebionetworks/agora/services';
import { HelperService as ExplorersHelperService } from '@sagebionetworks/explorers/services';
import { BaseChartComponent } from '../base-chart/base-chart.component';

// -------------------------------------------------------------------------- //
// Component
// -------------------------------------------------------------------------- //
@Component({
  selector: 'agora-median-chart',
  standalone: true,
  templateUrl: './median-chart.component.html',
  styleUrls: ['./median-chart.component.scss'],
})
export class MedianChartComponent extends BaseChartComponent {
  helperService = inject(HelperService);
  explorersHelperService = inject(ExplorersHelperService);

  _data: MedianExpression[] = [];
  get data() {
    return this._data;
  }
  @Input() set data(data) {
    this._data = data;
    this.init();
  }

  @Input() xAxisLabel = '';
  @Input() yAxisLabel = 'LOG2 CPM';

  override name = 'median-chart';
  dimension: any;
  group: any;

  override init() {
    if (!this._data?.length || !this.chartContainer.nativeElement) {
      return;
    }

    this.initData();
    this.initChart();

    this.isInitialized = true;
  }

  initData() {
    const ndx = crossfilter(this.data);
    this.dimension = ndx.dimension((d: any) => d.tissue);
    this.group = this.dimension
      .group()
      .reduceSum((d: any) => this.explorersHelperService.getSignificantFigures(d.median));
  }

  initChart() {
    const self = this;

    // Chart
    this.chart = dc
      .barChart(this.chartContainer.nativeElement)
      .dimension(this.dimension)
      .group(this.group)
      .brushOn(false);

    // X axis
    this.chart.x(d3.scaleBand()).xUnits(dc.units.ordinal).xAxis();
    // .tickSizeOuter([0]);

    // Y axis
    this.chart
      .y(d3.scaleLinear().domain([0, this.group.top(1)[0]?.value || 0]))
      .yAxisLabel(this.yAxisLabel)
      .yAxis()
      .ticks(3);
    //.tickSizeOuter([0]);

    // Colors
    this.chart.colors([this.helperService.getColor('secondary')]);

    // Spacing
    this.chart
      .margins({
        left: 70,
        right: 0,
        bottom: 30,
        top: 50,
      })
      .barPadding(0.5);

    // Misc
    this.chart.renderLabel(true).turnOnControls(false).renderTitle(false);

    // On render
    this.chart.on('renderlet', (chart: any) => {
      if (chart) {
        const yDomainLength = Math.abs(chart.y().domain()[1] - chart.y().domain()[0]);
        chart.selectAll('rect').each((el: any, i: number, tree: any) => {
          if (el && el.y <= 0) {
            tree[i].setAttribute('height', 0);
          }
        });
        chart.selectAll('rect').attr('pointer-events', 'none');
        chart.selectAll('text').each((el: any, i: number, tree: any) => {
          if (el && el['data'] && el['data'].value < 0) {
            el['data'].value = '';
            el.y = '';
            tree[i].innerHTML = '';
          }
        });
        // const svgEl = (chart.selectAll('g.axis.y').node() as SVGGraphicsElement);
        const mult = chart.effectiveHeight() / yDomainLength;
        const lefty = 0;
        const righty = 0; // use real statistics here!
        const extradata = [
          { x: chart.x().range()[0], y: chart.y()(lefty) },
          { x: chart.x().range()[1], y: chart.y()(righty) },
        ];
        const line = d3
          .line()
          .x((d: any) => d.x)
          .y(() => Math.abs(chart.y().domain()[1] - Math.log2(5)) * mult);
        const chartBody = chart.select('g.chart-body');
        let path = chartBody.selectAll('path.extra').data([extradata]);
        path = path
          .enter()
          .append('path')
          .attr('class', 'extra')
          .attr('stroke', 'red')
          .attr('id', 'extra-line')
          .merge(path);
        path.attr('d', line);

        self.addXAxisTooltips();
      }
    });

    this.chart.filter = () => '';
    this.chart.render();
  }

  override getXAxisTooltipText(text: string) {
    return this.helperService.getGCTColumnTooltipText(text);
  }
}
