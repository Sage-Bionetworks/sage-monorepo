/* eslint-disable @typescript-eslint/no-this-alias */
import { Component, AfterViewInit, OnDestroy, ViewChild, ElementRef, Input } from '@angular/core';
import * as d3 from 'd3';
import * as dc from 'dc';

@Component({
  selector: 'agora-base-chart',
  standalone: true,
  templateUrl: './base-chart.component.html',
  styleUrls: ['./base-chart.component.scss'],
})
export class BaseChartComponent implements AfterViewInit, OnDestroy {
  @Input() heading = '';

  name = 'chart';
  chart: any;

  isLoading = false;
  isInitialized = false;

  resizeTimer: ReturnType<typeof setTimeout> | number = 0;

  tooltips: {
    [key: string]: d3.Selection<HTMLDivElement, unknown, HTMLElement, any>;
  } = {};

  @ViewChild('chartContainer', { static: true }) chartContainer: ElementRef = {} as ElementRef;

  ngAfterViewInit() {
    if (!this.isInitialized && !this.isLoading) {
      this.init();
    }
  }

  ngOnDestroy() {
    this.destroy();
  }

  init() {
    if (!this.chartContainer?.nativeElement) {
      return;
    }

    this.isInitialized = true;
  }

  destroy() {
    if (this.tooltips) {
      for (const name in this.tooltips) {
        this.tooltips[name].remove();
      }
    }
    if (this.chart) {
      dc.chartRegistry.deregister(this.chart);
    }
  }

  getTooltip(name: string, className = '', arrowBelow = false) {
    if (!this.tooltips[name]) {
      this.tooltips[name] = d3
        .select('body')
        .append('div')
        .attr(
          'class',
          `chart-tooltip ${
            arrowBelow ? 'arrow-below' : 'arrow-above'
          } chart-${name}-tooltip${className ? ' ' + className : ''}`,
        );
    }

    return this.tooltips[name];
  }

  showTooltip(name: string) {
    if (!this.tooltips[name]) {
      return;
    }

    this.tooltips[name]
      .transition()
      .duration(80)
      .style('opacity', 1)
      .style('visibility', 'visible');
  }

  hideTooltip(name: string) {
    if (!this.tooltips[name]) {
      return;
    }

    this.tooltips[name]
      .transition()
      .duration(80)
      .style('top', '0')
      .style('left', '0')
      .style('opacity', 0)
      .style('visibility', 'hidden');
  }

  onResize() {
    if (!this.chart) {
      return;
    }

    const self = this;
    clearTimeout(this.resizeTimer);
    this.resizeTimer = setTimeout(() => {
      self.chart
        .width(self.chartContainer.nativeElement.parentElement.offsetWidth)
        .height(self.chartContainer.nativeElement.offsetHeight);
      if (self.chart.rescale) {
        self.chart.rescale();
      }
      self.chart.redraw();
    }, 100);
  }

  getXAxisTooltipText(text: string) {
    return text;
  }

  addXAxisTooltips() {
    const self = this;
    const tooltip = this.getTooltip('x-axis', `chart-x-axis-tooltip ${this.name}-x-axis-tooltip`);

    this.chart.selectAll('g.axis.x g.tick').each(function (this: any) {
      const tick = d3.select(this);
      const tickText = tick.select('text');
      const tickLine = tick.select('line');

      const text = self.getXAxisTooltipText(tickText?.text());

      if (text) {
        tickText
          .on('mouseover', function () {
            const tickTextNode = tickText.node() as HTMLElement;
            const tickLineNode = tickLine.node() as HTMLElement;
            const tooltipNode = tooltip.node() as HTMLElement;

            if (!tooltipNode || !tickTextNode || !tickLineNode) {
              return;
            }

            const tickTextRect = tickTextNode.getBoundingClientRect() || null;
            const tickLineRect = tickLineNode.getBoundingClientRect() || null;

            tooltip
              .html(text)
              .style(
                'top',
                // Position at the bottom on the label + 15px
                `${window.pageYOffset + tickTextRect.top + tickTextRect.height + 15}px`,
              )
              .style(
                'left',
                // Left position of the tick line minus half the tooltip width to center.
                `${tickLineRect.left - tooltipNode.offsetWidth / 2}px`,
              );

            self.showTooltip('x-axis');
          })
          .on('mouseout', function () {
            self.hideTooltip('x-axis');
          });
      }
    });
  }
}
