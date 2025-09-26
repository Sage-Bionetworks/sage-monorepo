/* eslint-disable @typescript-eslint/no-this-alias */
import {
  AfterViewInit,
  Component,
  ElementRef,
  HostListener,
  inject,
  Input,
  OnChanges,
  OnDestroy,
  SimpleChanges,
  ViewChild,
  ViewEncapsulation,
} from '@angular/core';
import * as d3 from 'd3';

import { MedianExpression } from '@sagebionetworks/agora/api-client';
import { HelperService } from '@sagebionetworks/agora/services';

@Component({
  selector: 'agora-median-barchart',
  standalone: true,
  templateUrl: './median-barchart.component.html',
  styleUrls: ['./median-barchart.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class MedianBarChartComponent implements OnChanges, AfterViewInit, OnDestroy {
  helperService = inject(HelperService);

  private chartInitialized = false;
  private tooltipInitialized = false;
  private _data: MedianExpression[] = [];
  private chart!: d3.Selection<any, unknown, null, undefined>;
  private tooltip!: d3.Selection<any, unknown, null, undefined>;
  private MEANINGFUL_EXPRESSION_THRESHOLD = Math.log2(5);
  private maxValueY = -1;

  private MIN_CHART_WIDTH = 500;
  private CHART_HEIGHT = 350;
  private chartMargin = { top: 20, right: 20, bottom: 65, left: 65 };
  private chartXScale!: d3.ScaleBand<string>;
  private chartXAxisDrawn!: d3.Selection<SVGGElement, unknown, null, undefined>;
  private chartXAxisLabel!: d3.Selection<SVGTextElement, unknown, null, undefined>;
  private chartBars!: d3.Selection<SVGRectElement, MedianExpression, any, unknown>;
  private chartScoreLabels!: d3.Selection<SVGTextElement, MedianExpression, any, unknown>;
  private chartThresholdLine!: d3.Selection<SVGLineElement, unknown, null, undefined>;
  private shouldShowThresholdLine = false;

  private resizeTimer: ReturnType<typeof setTimeout> | number = 0;

  get data() {
    return this._data;
  }
  @Input() set data(data: MedianExpression[]) {
    this._data = data
      .filter((el) => el.median && el.median > 0)
      .sort((a, b) => a.tissue.localeCompare(b.tissue));
    this.maxValueY = d3.max(this._data, (d) => d.median) || 0;
    this.shouldShowThresholdLine = this.MEANINGFUL_EXPRESSION_THRESHOLD <= this.maxValueY;
  }

  @Input() shouldResize = true;
  @Input() xAxisLabel = '';
  @Input() yAxisLabel = 'LOG2 CPM';

  @ViewChild('medianBarChartContainer') medianBarChartContainer: ElementRef<HTMLElement> =
    {} as ElementRef;
  @ViewChild('chart') chartRef: ElementRef<SVGElement> = {} as ElementRef;
  @ViewChild('tooltip') tooltipRef: ElementRef<HTMLElement> = {} as ElementRef;

  @HostListener('window:resize', ['$event.target'])
  onResize() {
    if (this.shouldResize && this.chartInitialized) {
      const self = this;
      const divSize = this.medianBarChartContainer.nativeElement.getBoundingClientRect().width;
      clearTimeout(this.resizeTimer);
      this.resizeTimer = setTimeout(() => {
        self.resizeChart(divSize);
      }, 100);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (
      (changes['_data'] && !changes['_data'].firstChange) ||
      (changes['xAxisLabel'] && !changes['xAxisLabel'].firstChange) ||
      (changes['yAxisLabel'] && !changes['yAxisLabel'].firstChange)
    ) {
      if (this._data.length === 0) {
        this.clearChart();
        this.hideChart();
      } else {
        this.clearChart();
        this.showChart();
        this.createChart();
      }
    }
  }

  ngAfterViewInit(): void {
    if (this._data.length === 0) this.hideChart();
    else this.createChart();
  }

  ngOnDestroy(): void {
    this.destroyChart();
  }

  clearChart() {
    const svg = d3.select(this.chartRef.nativeElement);
    svg.selectAll('*').remove();
  }

  hideChart() {
    const svg = d3.select(this.chartRef.nativeElement);
    svg.style('display', 'none');
  }

  showChart() {
    const svg = d3.select(this.chartRef.nativeElement);
    svg.style('display', 'block');
  }

  destroyChart() {
    if (this.chartInitialized) this.chart.remove();
    if (this.tooltipInitialized) this.tooltip.remove();
  }

  showTooltip(text: string, x: number, y: number): void {
    this.tooltip = d3
      .select(this.tooltipRef.nativeElement)
      .style('left', `${x}px`)
      .style('top', `${y}px`)
      .style('display', 'block')
      .html(text);
    this.tooltipInitialized = true;
  }

  hideTooltip() {
    if (this.tooltipInitialized) {
      this.tooltip.style('display', 'none');
    }
  }

  getBarCenterX(tissue: string, xScale: d3.ScaleBand<string>): number {
    return (xScale(tissue) || 0) + xScale.bandwidth() / 2;
  }

  // get the current width allotted to this chart or default
  getChartBoundingWidth(): number {
    return (
      d3.select(this.chartRef.nativeElement).node()?.getBoundingClientRect().width ||
      this.MIN_CHART_WIDTH
    );
  }

  createChart() {
    if (this._data.length > 0) {
      const barColor = this.helperService.getColor('secondary');
      const width = this.getChartBoundingWidth();
      const height = this.CHART_HEIGHT;
      const innerWidth = width - this.chartMargin.left - this.chartMargin.right;
      const innerHeight = height - this.chartMargin.top - this.chartMargin.bottom;

      this.chart = d3
        .select(this.chartRef.nativeElement)
        .attr('width', width)
        .attr('height', height)
        .append('g')
        .attr('transform', `translate(${this.chartMargin.left}, ${this.chartMargin.top})`);

      // SCALES
      this.chartXScale = d3
        .scaleBand()
        .domain(this._data.map((d) => d.tissue))
        .range([0, innerWidth])
        .padding(0.2);

      const yScale = d3.scaleLinear().domain([0, this.maxValueY]).nice().range([innerHeight, 0]);

      // BARS
      this.chartBars = this.chart
        .selectAll('.medianbars')
        .data(this._data)
        .enter()
        .append('rect')
        .attr('class', 'medianbars')
        .attr('x', (d) => this.chartXScale(d.tissue) as number)
        .attr('y', (d) => yScale(d.median || 0))
        .attr('width', this.chartXScale.bandwidth())
        .attr('height', (d) => innerHeight - yScale(d.median || 0))
        .attr('fill', barColor);

      // SCORE LABELS
      this.chartScoreLabels = this.chart
        .selectAll('.bar-labels')
        .data(this._data)
        .enter()
        .append('text')
        .attr('class', 'bar-labels')
        .attr('x', (d) => this.getBarCenterX(d.tissue, this.chartXScale))
        .attr('y', (d) => yScale(d.median || 0) - 5)
        .text((d) => this.helperService.roundNumber(d.median || 0, 2));

      // X-AXIS
      const xAxis = d3.axisBottom(this.chartXScale);
      this.chartXAxisDrawn = this.chart
        .append('g')
        .attr('class', 'x-axis')
        .attr('transform', `translate(0, ${innerHeight})`)
        .call(xAxis.tickSizeOuter(0));
      this.chartXAxisDrawn
        .selectAll('.tick')
        .on('mouseenter', (_, tissue) => {
          const tooltipText = this.helperService.getGCTColumnTooltipText(tissue as string);
          this.showTooltip(
            tooltipText,
            this.getBarCenterX(tissue as string, this.chartXScale) + this.chartMargin.left,
            height - this.chartMargin.top,
          );
        })
        .on('mouseleave', () => {
          this.hideTooltip();
        });

      // Y-AXIS
      const yAxis = d3.axisLeft(yScale);
      this.chart.append('g').attr('class', 'y-axis').call(yAxis);

      // X-AXIS LABEL
      this.chartXAxisLabel = this.chart
        .append('text')
        .attr('class', 'x-axis-label')
        .attr('x', innerWidth / 2)
        .attr('y', innerHeight + this.chartMargin.bottom)
        .attr('text-anchor', 'middle')
        .text(this.xAxisLabel);

      // Y-AXIS LABEL
      this.chart
        .append('text')
        .attr('class', 'y-axis-label')
        .attr('x', -innerHeight / 2)
        .attr('y', -this.chartMargin.left)
        .attr('dy', '1em')
        .attr('text-anchor', 'middle')
        .attr('transform', 'rotate(-90)')
        .text(this.yAxisLabel);

      // THRESHOLD LINE
      if (this.shouldShowThresholdLine) {
        this.chartThresholdLine = this.chart
          .append('line')
          .attr('class', 'meaningful-expression-threshold-line')
          .attr('x1', 0)
          .attr('x2', innerWidth)
          .attr('y1', yScale(this.MEANINGFUL_EXPRESSION_THRESHOLD))
          .attr('y2', yScale(this.MEANINGFUL_EXPRESSION_THRESHOLD))
          .attr('stroke', 'red');
      }

      this.chartInitialized = true;
    }
  }

  resizeChart = (divSize: number): void => {
    // calculate new width
    const width = Math.max(divSize, this.MIN_CHART_WIDTH);
    const innerWidth = width - this.chartMargin.left - this.chartMargin.right;

    // update chart size
    this.chart.attr('width', width);

    // update chartXScale
    this.chartXScale.range([0, innerWidth]);

    // update bars
    this.chartBars
      .transition()
      .attr('x', (d) => this.chartXScale(d.tissue) as number)
      .attr('width', this.chartXScale.bandwidth());

    // update score labels
    this.chartScoreLabels
      .transition()
      .attr('x', (d) => this.getBarCenterX(d.tissue, this.chartXScale));

    // update drawn x-axis
    const xAxis = d3.axisBottom(this.chartXScale);
    this.chartXAxisDrawn.transition().call(xAxis.tickSizeOuter(0));

    // update x-axis label
    this.chartXAxisLabel.transition().attr('x', innerWidth / 2);

    // update threshold line
    if (this.shouldShowThresholdLine) {
      this.chartThresholdLine.transition().attr('x2', innerWidth);
    }
  };
}
