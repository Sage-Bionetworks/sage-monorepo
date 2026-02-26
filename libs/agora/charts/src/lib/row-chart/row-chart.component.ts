/* eslint-disable @typescript-eslint/no-empty-function */
/* eslint-disable @typescript-eslint/no-this-alias */
import { Component, ElementRef, Input, ViewChild, inject } from '@angular/core';

import * as d3 from 'd3';
import * as dc from 'dc';

import { RowChartItem } from '@sagebionetworks/agora/models';
import { HelperService } from '@sagebionetworks/agora/services';
import { HelperService as ExplorersHelperService } from '@sagebionetworks/explorers/services';
import { BaseChartComponent } from '../base-chart/base-chart.component';

// Using a d3 v4 function to get all nodes
d3.selection.prototype['nodes'] = function () {
  const nodes = new Array(this.size());
  let i = -1;
  this.each(function (this: any) {
    nodes[++i] = this;
  });
  return nodes;
};

@Component({
  selector: 'agora-row-chart',
  standalone: true,
  templateUrl: './row-chart.component.html',
  styleUrls: ['./row-chart.component.scss'],
})
export class RowChartComponent extends BaseChartComponent {
  helperService = inject(HelperService);
  explorersHelperService = inject(ExplorersHelperService);

  _data: RowChartItem[] = [];
  get data(): RowChartItem[] {
    return this._data;
  }
  @Input() set data(data: RowChartItem[]) {
    this._data = data;
    this.init();
  }

  @Input() xAxisLabel = '';
  @Input() paddingLR = 15;
  @Input() paddingUD = 0;

  override name = 'row-chart';
  group: any;
  dimension: any;

  @ViewChild('leftAxis', { static: true }) leftAxis: ElementRef = {} as ElementRef;

  max = -Infinity;
  canDisplay = false;
  canResize = false;
  colors: string[] = [this.helperService.getColor('secondary')];

  override init() {
    if (!this._data?.length || !this.chartContainer?.nativeElement) {
      return;
    }

    this.initData();
    this.initChart();

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

    this.chart = dc.rowChart(this.chartContainer.nativeElement);

    this.chart.group(this.group).dimension(this.dimension);

    //this.chart.xAxis().tickSizeOuter([0]);

    this.chart
      .gap(4)
      .title(false)
      .valueAccessor((d: any) => {
        return self.explorersHelperService.getSignificantFigures(+d.value.logfc, 3);
      })
      .keyAccessor((d: any) => {
        return d.key[0];
      })
      .label((d: any) => {
        return d.key[0];
      })
      .on('preRender', function (chart: any) {
        self.max = -Infinity;
        self.updateXDomain();
        if (self.max !== -Infinity) {
          self.max *= 1.1;
          chart.x(
            d3
              .scaleLinear()
              .range([0, self.chartContainer.nativeElement.offsetWidth - 40])
              .domain([-self.max, self.max]),
          );
          chart.xAxis().scale(chart.x());
        }
      })
      .on('preRedraw', function (chart: any) {
        self.max = -Infinity;
        self.updateXDomain();
        if (self.max !== -Infinity) {
          self.max *= 1.1;
          chart.x(
            d3
              .scaleLinear()
              .range([0, self.chartContainer.nativeElement.offsetWidth - 40])
              .domain([-self.max, self.max]),
          );
          chart.xAxis().scale(chart.x());
        }
      })
      .on('renderlet', function (chart: any) {
        // Copy all vertical texts to another div, so they don't get hidden by
        // the row chart svg after being translated
        self.moveTextToElement(chart, self.leftAxis.nativeElement, 9);
        dc.events.trigger(function () {
          self.updateChartExtras(chart);
        });
      })
      //.othersGrouper(null)
      .ordinalColors(self.colors)

      .transitionDuration(0);

    this.chart.render();
  }

  addXLabel(chart: dc.RowChart, text: string, svg?: any, width = 0, height = 0) {
    const textSelection = svg || chart.svg();
    if (textSelection !== null) {
      const label = textSelection.select('.x-axis-label');

      if (!label.node()) {
        textSelection
          .append('text')
          .attr('class', 'x-axis-label')
          .attr('text-anchor', 'middle')
          .attr('x', width / 2)
          .attr('y', height - 10)
          .text(text);
      } else {
        label.attr('x', width / 2).attr('y', height - 10);
      }

      this.adjustXLabel(chart, textSelection, width, height);
    }
  }

  adjustXLabel(chart: dc.RowChart, sel: any, width = 0, height = 0) {
    const svgEl = (sel.node() || chart.svg()) as SVGGraphicsElement;
    if (svgEl !== null) {
      const textDims = svgEl.getBBox();

      // Dynamically adjust positioning after reading text dimension from DOM
      // The main svg gets translated by (30, 10) and the flex row has a margin
      // of 15 pixels. We subtract them from the svg size, get the middle point
      // then add back the left translate to get the correct center
      sel.attr('x', (width - 45) / 2 + 30).attr('y', height - Math.ceil(textDims.height) / 2);
    }
  }

  updateChartExtras(chart: dc.RowChart) {
    const self = this;
    let rectHeight = !chart.select('g.row rect').empty()
      ? parseInt(chart.select('g.row rect').attr('height'), 10)
      : 52;
    rectHeight = isNaN(rectHeight) ? 52 : rectHeight;
    const squareSize = 18;
    const lineWidth = 60;

    // Insert a line for each row of the chart
    self.insertLinesInRows(chart);

    // Insert the texts for each row of the chart. At first we need to add
    // empty texts so that the rowChart redraw does not move out confidence
    // texts around
    self.insertTextsInRows(chart);
    self.insertTextsInRows(chart, 'confidence-text-left');
    self.insertTextsInRows(chart, 'confidence-text-right');

    // Finally redraw the lines in each row
    self.drawLines(chart, rectHeight / 2);

    // Change the row rectangles into small circles, this happens on
    // every render or redraw
    self.rectToCircles(chart, squareSize, rectHeight);

    // Only show the 0, min and max values on the xAxis ticks
    self.updateXTicks(chart);

    // Redraw confidence text next to the lines in each row
    self.renderConfidenceTexts(chart, rectHeight / 2, lineWidth, true);
    self.renderConfidenceTexts(chart, rectHeight / 2, lineWidth);
  }

  updateXDomain() {
    //Draw the horizontal lines
    this._data.forEach((g: any) => {
      if (Math.abs(+g.ci_l) > this.max) {
        this.max = Math.abs(+g.ci_l);
      }
      if (Math.abs(+g.ci_r) > this.max) {
        this.max = Math.abs(+g.ci_r);
      }
    });
  }

  updateXTicks(chart: dc.RowChart) {
    const allTicks = chart.selectAll('g.axis g.tick');
    allTicks.each(function (this: any, i: any) {
      const el = d3.select(this);
      let value = parseFloat(el.select('text').text());
      // Handle UTF-8 characters, if text is not a number,
      // then replace all non-numeric values with the empty string
      if (isNaN(value)) {
        value = parseFloat(
          '-' +
            el
              .select('text')
              .text()
              .replace(/[^,.0-9]/g, ''),
        );
      }

      if (i > 0 && i < allTicks.size() - 1) {
        if (value) {
          el.selectAll('line').style('opacity', 0);
          el.select('text').style('opacity', 0);
        }
      } else if (value) {
        el.selectAll('line').style('opacity', 0);
        el.select('text').style('opacity', 1);
      }
    });
  }

  adjustTextToElement(el: HTMLElement) {
    d3.select(el)
      .selectAll('g.textGroup text')
      .each(function () {
        const pRigth = isNaN(parseInt(d3.select(el).style('padding-right'), 10))
          ? 15
          : parseInt(d3.select(el).style('padding-right'), 10);
        const transfString = d3.select(this).attr('transform');
        const translateString = transfString.substring(
          transfString.indexOf('(') + 1,
          transfString.indexOf(')'),
        );
        const translate =
          translateString.split(',').length > 1
            ? translateString.split(',')
            : translateString.split(' ');
        const svgWidth = isNaN(parseFloat(d3.select(el).select('svg').style('width')))
          ? 450
          : parseFloat(d3.select(el).select('svg').style('width'));
        const transfX = svgWidth - pRigth;
        const ftransfx = isNaN(transfX) ? 0.0 : transfX;
        d3.select(this).attr('transform', () => {
          return 'translate(' + ftransfx + ',' + parseFloat(translate[1]) + ')';
        });
      });
  }

  // Moves all text in textGroups to a new HTML element
  moveTextToElement(chart: dc.RowChart, el: HTMLElement, vSpacing = 0) {
    const self = this;
    const container = d3.select(el).html('');
    const svg = container.append('svg');
    const group = svg.append('g').attr('class', 'textGroup');
    const texts: any = chart.selectAll('g.row > text');
    const tooltip = this.getTooltip('x-axis', 'chart-x-axis-tooltip row-chart-x-axis-tooltip');

    texts.each(function (this: any) {
      this.style.display = 'none';
      group
        .append('text')
        .html(this.innerHTML)
        .attr('x', 0)
        .attr('y', this.getAttribute('y'))
        .attr('dy', this.getAttribute('dy'));
    });

    // Move the text to the correct position in the new svg
    const svgEl = chart.select('g.axis g.tick line.grid-line').node() as SVGGraphicsElement;

    // Need this condition when reloading in Edge
    if (svgEl) {
      const step = svgEl.getBBox().height / texts.nodes().length;

      svg.selectAll('text').each(function (d, i) {
        const currentStep = step * i;
        const transfX =
          parseFloat(svg.style('width')) - parseFloat(d3.select(el).style('padding-right'));
        const ftransfx = isNaN(transfX) ? 0 : transfX;

        const tickText = d3.select(this);

        tickText
          .attr('text-anchor', 'end')
          .attr('transform', () => {
            return 'translate(' + ftransfx + ',' + (currentStep + vSpacing) + ')';
          })
          .on('mouseover', function () {
            const tickTextNode = tickText.node() as HTMLElement;
            const tooltipNode = tooltip.node() as HTMLElement;

            if (!tooltipNode || !tickTextNode) {
              return;
            }

            tooltip.html(self.helperService.getGCTColumnTooltipText(tickText.text()));

            const tickTextRect = tickTextNode.getBoundingClientRect() || null;

            tooltip
              .style(
                'top',
                // Position at the bottom on the label + 15px
                `${window.pageYOffset + tickTextRect.top - 15}px`,
              )
              .style(
                'left',
                // Left position of the tick line minus half the tooltip width to center.
                `${tickTextRect.left + tickTextRect.width + 15}px`,
              );

            self.showTooltip('x-axis');
          })
          .on('mouseout', function () {
            self.hideTooltip('x-axis');
          });
      });
    }
  }

  insertLinesInRows(chart: dc.RowChart) {
    chart.selectAll('g.row').each(function (this: any) {
      const row = d3.select(this);
      row.select('.hline').remove();
      row.insert('g').attr('class', 'hline').insert('line');
    });
  }

  insertTextsInRows(chart: dc.RowChart, textClass?: string) {
    chart.selectAll('g.row').each(function (this: any) {
      const row = d3.select(this);
      row.select('.' + textClass).remove();
      row
        .insert('g')
        .attr('class', textClass ? textClass : 'confidence-text')
        .insert('text');
    });
  }

  // Draw the lines through the chart rows and a vertical line at
  // x = 0
  drawLines(chart: dc.RowChart, yPos: number) {
    const self = this;
    // Hide all vertical lines except one at x = 0
    chart.selectAll('g.axis g.tick').each(function (this: any) {
      const el = d3.select(this);
      if (parseFloat(el.select('text').text())) {
        el.select('.grid-line').style('opacity', 0);
      } else {
        el.select('.grid-line')
          .style('stroke', '#BCC0CA')
          .style('stroke-width', '2px')
          .style('opacity', 1);
      }
    });

    // Draw the horizontal lines
    chart
      .selectAll('g.row g.hline line')
      .attr('stroke-width', 1.5)
      .attr('stroke', () => {
        return self.colors[0];
      })
      // ES6 method shorthand for object literals
      .attr('x1', (d) => {
        const data: any = self._data.find((g: any) => {
          return d.key[0] === g.tissue;
        });

        if (data) {
          const val = chart.x()(data.ci_l);
          return isNaN(val) ? 0.0 : val;
        } else {
          return 0.0;
        }
      })
      .attr('y1', () => {
        return yPos;
      })
      .attr('x2', (d) => {
        const data: any = self._data.find((g: any) => {
          return d.key[0] === g.tissue;
        });

        if (data) {
          const val = chart.x()(data.ci_r);
          return isNaN(val) ? 0.0 : val;
        } else {
          return 0.0;
        }
      })
      .attr('y2', () => {
        return yPos;
      });
  }

  // Renders the confidence interval values next to the horizontal lines
  renderConfidenceTexts(chart: dc.RowChart, yPos: number, lineWidth: number, isNeg?: boolean) {
    const self = this;

    // Draw the confidence texts
    const posQueryString = isNeg ? '-left' : '-right';
    const queryString = 'g.row g.confidence-text' + posQueryString + ' text';
    chart.group(self.group);
    chart
      .selectAll(queryString)
      // ES6 method shorthand for object literals
      .attr('x', (d) => {
        const data: any = self._data.find((g: any) => {
          return d.key[0] === g.tissue;
        });

        let scaledX = 0;
        // Two significant digits
        let ciValue = 0.0;
        // Move back 0.5 pixel for the dot and 5 for each number
        let dotPixels = 0;
        const mPixels = 5;

        if (data && data.ci_l && data.ci_r) {
          dotPixels = data.ci_l.toPrecision(2).indexOf('.') !== -1 ? 0.5 : 0.0;
          ciValue = isNeg ? data.ci_l : data.ci_r;
          scaledX = chart.x()(ciValue);
        } else {
          dotPixels = 0.5;
          ciValue = d.value.logfc;
          scaledX = chart.x()(d.value.logfc) - lineWidth / 2;
        }

        let val = 0.0;
        if (ciValue) {
          val = isNeg
            ? scaledX - (ciValue.toPrecision(2).length * mPixels + dotPixels)
            : scaledX + (ciValue.toPrecision(2).length * mPixels + dotPixels);
        }
        return isNaN(val) ? 0.0 : val;
      })
      .attr('y', () => {
        return yPos + 5;
      })
      .attr('text-anchor', 'middle')
      .text((d) => {
        const data: any = self._data.find((g: any) => {
          return d.key[0] === g.tissue;
        });

        let ciValue = '0.0';
        if (data) {
          ciValue = isNeg ? data.ci_l.toPrecision(2) : data.ci_r.toPrecision(2);
        }

        return ciValue;
      });
  }

  // Compares the current value from a group to the gene expected value
  compareAttributeValue(cValue: number, gValue: number): boolean {
    return this.explorersHelperService.getSignificantFigures(cValue) === gValue;
  }

  // Changes the chart row rects into squares of the square size
  rectToCircles(chart: dc.RowChart, squareSize: number, rectHeight: number) {
    const self = this;

    chart.selectAll('g.row rect').each(function (this: any) {
      const circle = d3.select(this);
      const tooltip = self.getTooltip('internal', 'chart-value-tooltip row-chart-value-tooltip');

      circle
        .attr('transform', function (d: any) {
          const val = isNaN(chart.x()(+d.value.logfc) - squareSize / 2)
            ? 0.0
            : chart.x()(+d.value.logfc) - squareSize / 2;

          return (
            'translate(' +
            // X translate
            val +
            ',' +
            // Y translate
            (rectHeight / 2 - squareSize / 2) +
            ')'
          );
        })
        .attr('width', squareSize)
        .attr('height', squareSize)
        .attr('rx', squareSize / 2)
        .attr('ry', squareSize / 2);

      circle
        .on('mouseover', function (event: any, d: any) {
          const offset = self.explorersHelperService.getOffset(this);
          const text = `Log Fold Change: ${self.explorersHelperService.getSignificantFigures(
            +d.value.logfc,
            3,
          )}`;

          tooltip
            .style('left', (offset?.left || 0) + 'px')
            .style('top', (offset?.top + 15 || 0) + 'px')
            .html(text);

          self.showTooltip('internal');
        })
        .on('mouseout', function () {
          self.hideTooltip('internal');
        })
        .on('click', () => {});
    });
  }

  displayChart(): any {
    return { opacity: 1 };
  }

  override onResize() {
    if (!this.chart) {
      return;
    }

    const self = this;
    clearTimeout(this.resizeTimer);
    this.resizeTimer = setTimeout(() => {
      self.chart
        .width(self.chartContainer.nativeElement.parentElement.offsetWidth - 100)
        .height(self.chartContainer.nativeElement.offsetHeight);
      if (self.chart.rescale) {
        self.chart.rescale();
      }
      self.chart.redraw();
    }, 100);
  }
}
