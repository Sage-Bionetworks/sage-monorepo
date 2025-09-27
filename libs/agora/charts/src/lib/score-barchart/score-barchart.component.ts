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

import { OverallScoresDistribution } from '@sagebionetworks/agora/api-client';
import { ScoreData } from '@sagebionetworks/agora/models';
import { HelperService } from '@sagebionetworks/agora/services';

@Component({
  selector: 'agora-score-barchart',
  imports: [],
  templateUrl: './score-barchart.component.html',
  styleUrls: ['./score-barchart.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ScoreBarChartComponent implements OnChanges, AfterViewInit, OnDestroy {
  helperService = inject(HelperService);

  _score: number | null = null;
  get score(): number | null {
    return this._score;
  }
  @Input() set score(score: number | null) {
    this._score = score;
  }

  @Input() shouldResize = true;
  @Input() barColor = '#8B8AD1';
  @Input() data: OverallScoresDistribution | undefined;
  @Input() xAxisLabel = 'Gene score';
  @Input() yAxisLabel = 'Number of genes';

  @ViewChild('scoreBarChartContainer') scoreBarChartContainer: ElementRef<HTMLElement> =
    {} as ElementRef;
  @ViewChild('chart') chartRef: ElementRef<SVGElement> = {} as ElementRef;
  @ViewChild('tooltip', { static: true }) tooltip: ElementRef<HTMLElement> = {} as ElementRef;

  initialized = false;
  private MIN_CHART_WIDTH = 350;
  private CHART_HEIGHT = 350;
  private chart!: d3.Selection<any, unknown, null, undefined>;
  private chartMargin = { top: 20, right: 20, bottom: 40, left: 60 };
  private chartXScale!: d3.ScaleBand<string>;
  private chartNegativeBars!: d3.Selection<SVGRectElement, ScoreData, SVGGElement, unknown>;
  private chartScoreBars!: d3.Selection<SVGRectElement, ScoreData, SVGGElement, unknown>;
  private chartBarLabels!: d3.Selection<SVGTextElement, ScoreData, SVGGElement, unknown>;
  private chartXAxisDrawn!: d3.Selection<SVGGElement, unknown, null, undefined>;
  private chartXAxisLabel!: d3.Selection<SVGTextElement, unknown, null, undefined>;

  private resizeTimer: ReturnType<typeof setTimeout> | number = 0;

  chartData: ScoreData[] = [];
  scoreIndex = -1;

  @HostListener('window:resize', ['$event.target'])
  onResize() {
    if (this.shouldResize && this.initialized) {
      const divSize = this.scoreBarChartContainer.nativeElement.getBoundingClientRect().width;
      clearTimeout(this.resizeTimer);
      this.resizeTimer = setTimeout(() => {
        this.resizeChart(divSize);
      }, 100);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (
      (changes['data'] && !changes['data'].firstChange) ||
      (changes['score'] && !changes['score'].firstChange) ||
      (changes['barColor'] && !changes['barColor'].firstChange)
    ) {
      if (this.score === null) {
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
    if (this.score === null) this.hideChart();
    else this.createChart();
  }

  ngOnDestroy(): void {
    this.destroyChart();
  }

  setScoreIndex(bins: number[][]) {
    bins.forEach((item, index: number) => {
      if (this._score === null) return;
      if (this._score >= item[0] && this._score < item[1]) {
        this.scoreIndex = index;
      }
      if (index === bins.length - 1) {
        // check border case where score is equal to the last bin upper bound
        if (this._score === item[1]) this.scoreIndex = index;
      }
    });
  }

  initData() {
    if (!this.data) return;

    this.chartData = [];

    this.setScoreIndex(this.data.bins);

    this.data.distribution.forEach((item, index: number) => {
      this.chartData.push({
        distribution: item,
        bins: this.data?.bins[index],
      } as ScoreData);
    });
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

  getChartBoundingWidth(): number {
    return (
      d3.select(this.chartRef.nativeElement).node()?.getBoundingClientRect().width ||
      this.MIN_CHART_WIDTH
    );
  }

  createChart() {
    this.initData();
    if (this.chartData) {
      const width = this.getChartBoundingWidth();
      const height = this.CHART_HEIGHT;
      const innerWidth = width - this.chartMargin.left - this.chartMargin.right;
      const innerHeight = height - this.chartMargin.top - this.chartMargin.bottom;

      const svg = (this.chart = d3
        .select(this.chartRef.nativeElement)
        .attr('width', width)
        .attr('height', height)
        .append('g')
        .attr('transform', `translate(${this.chartMargin.left}, ${this.chartMargin.top})`));

      this.chartXScale = d3
        .scaleBand()
        .domain(this.chartData.map((d) => d.bins[0].toString()))
        .range([0, innerWidth])
        .padding(0.2);

      const yScale = d3
        .scaleLinear()
        .domain([0, d3.max(this.chartData, (d) => d.distribution) as number])
        .range([innerHeight, 0]);

      // NEGATIVE SPACE ABOVE BARS
      this.chartNegativeBars = svg
        .selectAll('.negative-bars')
        .data(this.chartData)
        .enter()
        .append('rect')
        .attr('class', 'negative-bars')
        .attr('x', (d) => this.chartXScale(d.bins[0].toString()) as number)
        .attr('y', 0)
        .attr('width', this.chartXScale.bandwidth())
        .attr('height', (d) => yScale(d.distribution))
        .attr('fill', 'transparent')
        .on('mouseenter', (event, d) => {
          const index = svg.selectAll('.negative-bars').nodes().indexOf(event.target);
          const tooltipText = this.getToolTipText(
            d.bins[0] as number,
            d.bins[1] as number,
            d.distribution,
          );
          const tooltipCoordinates = this.getTooltipCoordinates(
            this.chartMargin.left,
            this.chartMargin.top,
            this.chartXScale(d.bins[0].toString()) as number,
            this.chartXScale.bandwidth(),
            yScale(d.distribution),
          );
          const bar = d3.select(this.chartScoreBars.nodes()[index]);
          this.handleMouseEnter(bar, index, tooltipText, tooltipCoordinates);
        })
        .on('mouseleave', (event) => {
          const index = svg.selectAll('.negative-bars').nodes().indexOf(event.target);
          const bar = d3.select(this.chartScoreBars.nodes()[index]);
          this.handleMouseLeave(bar, index);
        });

      // BARS
      this.chartScoreBars = svg
        .selectAll('.scorebars')
        .data(this.chartData)
        .enter()
        .append('rect')
        .attr('class', 'scorebars')
        .attr('x', (d) => this.chartXScale(d.bins[0].toString()) as number)
        .attr('y', (d) => yScale(d.distribution))
        .attr('width', this.chartXScale.bandwidth())
        .attr('height', (d) => innerHeight - yScale(d.distribution))
        .attr('fill', this.barColor)
        .style('fill-opacity', (_, index) => (this.scoreIndex === index ? '100%' : '50%'))
        .on('mouseenter', (event, d) => {
          const index = svg.selectAll('.scorebars').nodes().indexOf(event.target);
          const tooltipText = this.getToolTipText(
            d.bins[0] as number,
            d.bins[1] as number,
            d.distribution,
          );
          const tooltipCoordinates = this.getTooltipCoordinates(
            this.chartMargin.left,
            this.chartMargin.top,
            this.chartXScale(d.bins[0].toString()) as number,
            this.chartXScale.bandwidth(),
            yScale(d.distribution),
          );
          const bar = d3.select(this.chartScoreBars.nodes()[index]);
          this.handleMouseEnter(bar, index, tooltipText, tooltipCoordinates);
        })
        .on('mouseleave', (event) => {
          const index = svg.selectAll('.scorebars').nodes().indexOf(event.target);
          const bar = d3.select(this.chartScoreBars.nodes()[index]);
          this.handleMouseLeave(bar, index);
        });

      // SCORE LABELS
      this.chartBarLabels = svg
        .selectAll('.bar-labels')
        .data(this.chartData)
        .enter()
        .append('text')
        .attr('class', 'bar-labels')
        .attr(
          'x',
          (d) =>
            (this.chartXScale(d.bins[0].toString()) as number) + this.chartXScale.bandwidth() / 2,
        )
        .attr('y', (d) => yScale(d.distribution) - 5)
        .attr('fill', this.barColor)
        .attr('text-anchor', 'middle')
        .attr('font-size', '12px')
        .style('font-weight', (_, index) => (this.scoreIndex === index ? 'bold' : 'normal'))
        .text((_, index) => {
          // only show the score on the corresponding bar
          if (this.scoreIndex == index)
            return this.helperService.roundNumber(this.score as number, 2);
          return '';
        })
        .on('mouseenter', (_, d) => {
          const index = this.scoreIndex;
          const tooltipText = this.getToolTipText(
            d.bins[0] as number,
            d.bins[1] as number,
            d.distribution,
          );
          const tooltipCoordinates = this.getTooltipCoordinates(
            this.chartMargin.left,
            this.chartMargin.top,
            this.chartXScale(d.bins[0].toString()) as number,
            this.chartXScale.bandwidth(),
            yScale(d.distribution),
          );
          const bar = d3.select(this.chartScoreBars.nodes()[index]);
          this.handleMouseEnter(bar, index, tooltipText, tooltipCoordinates);
        })
        .on('mouseleave', () => {
          const index = this.scoreIndex;
          const bar = d3.select(this.chartScoreBars.nodes()[index]);
          this.handleMouseLeave(bar, index);
        });

      // X-AXIS
      const xAxis = d3.axisBottom(this.chartXScale);
      this.chartXAxisDrawn = svg
        .append('g')
        .attr('class', 'x-axis')
        .attr('transform', `translate(0, ${innerHeight})`)
        .call(xAxis);

      // Y-AXIS
      const yAxis = d3.axisLeft(yScale);
      svg.append('g').attr('class', 'y-axis').call(yAxis);

      // X-AXIS LABEL
      this.chartXAxisLabel = svg
        .append('text')
        .attr('class', 'x-axis-label')
        .attr('x', innerWidth / 2)
        .attr('y', innerHeight + this.chartMargin.bottom)
        .attr('text-anchor', 'middle')
        .text('GENE SCORE');

      // Y-AXIS LABEL
      svg
        .append('text')
        .attr('class', 'y-axis-label')
        .attr('x', -innerHeight / 2)
        .attr('y', -this.chartMargin.left)
        .attr('dy', '1em')
        .attr('text-anchor', 'middle')
        .attr('transform', 'rotate(-90)')
        .text('NUMBER OF GENES');

      this.initialized = true;
    }
  }

  handleMouseEnter(
    bar: d3.Selection<SVGRectElement, unknown, null, undefined>,
    index: number,
    tooltipText: string,
    tooltipCoordinates: { X: number; Y: number },
  ) {
    this.highlightBar(bar, index);
    this.showTooltip(tooltipText, tooltipCoordinates.X, tooltipCoordinates.Y, index);
  }

  handleMouseLeave(bar: d3.Selection<SVGRectElement, unknown, null, undefined>, index: number) {
    this.unhighlightBar(bar, index);
    this.hideTooltip();
  }

  highlightBar(bar: d3.Selection<SVGRectElement, unknown, null, undefined>, index: number) {
    if (index === this.scoreIndex) {
      // when user mouses over score bar, change the opacity so it is clear they have moused over
      bar.style('fill-opacity', '80%');
    } else {
      bar.style('fill-opacity', '100%');
    }
  }

  unhighlightBar(bar: d3.Selection<SVGRectElement, unknown, null, undefined>, index: number) {
    if (index === this.scoreIndex) {
      // score bar should be 100% on mouseout
      bar.style('fill-opacity', '100%');
    } else {
      // non-score bars should be 50%
      bar.style('fill-opacity', '50%');
    }
  }

  getTooltipCoordinates(
    leftMargin: number,
    topMargin: number,
    xBarPosition: number,
    xBarWidth: number,
    yBarPosition: number,
  ) {
    // x-coordinate would be the left margin + x-barPosition + half of the bar width
    const x = leftMargin + xBarPosition + xBarWidth / 2;
    // y-coordinate would be the y-barPosition + top margin
    const y = topMargin + yBarPosition;
    return { X: x, Y: y };
  }

  getToolTipText(scoreRangeStart: number, scoreRangeEnd: number, geneCount: number) {
    const leftBoundCharacter = this.scoreIndex == 0 ? '[' : '(';
    return `Score Range: ${leftBoundCharacter}${scoreRangeStart}, ${scoreRangeEnd}]
    <br>
    Gene Count: ${geneCount}`;
  }

  showTooltip(text: string, x: number, y: number, index: number) {
    const tooltipElement = this.tooltip.nativeElement;
    tooltipElement.innerHTML = text;
    tooltipElement.style.left = `${x}px`;
    if (index === this.scoreIndex) y -= 14; // account for height of score
    tooltipElement.style.top = `${y}px`;
    tooltipElement.style.display = 'block';
  }

  hideTooltip() {
    const tooltipElement = this.tooltip.nativeElement;
    if (tooltipElement.style.display === 'block') tooltipElement.style.display = 'none';
  }

  destroyChart() {
    if (this.initialized) this.chart.remove();
  }

  resizeChart = (divSize: number): void => {
    // calculate new width
    const width = Math.max(divSize, this.MIN_CHART_WIDTH);
    const innerWidth = width - this.chartMargin.left - this.chartMargin.right;

    // update chart size
    this.chart.attr('width', width);

    // update chartXScale
    this.chartXScale.range([0, innerWidth]);

    // update negative bars
    this.chartNegativeBars
      .transition()
      .attr('x', (d) => this.chartXScale(d.bins[0].toString()) as number);

    // update score bars
    this.chartScoreBars
      .transition()
      .attr('x', (d) => this.chartXScale(d.bins[0].toString()) as number)
      .attr('width', this.chartXScale.bandwidth());

    // update score bar labels
    this.chartBarLabels
      .transition()
      .attr(
        'x',
        (d) =>
          (this.chartXScale(d.bins[0].toString()) as number) + this.chartXScale.bandwidth() / 2,
      )
      .attr('width', this.chartXScale.bandwidth());

    // update drawn x-axis
    const xAxis = d3.axisBottom(this.chartXScale);
    this.chartXAxisDrawn.transition().call(xAxis);

    // update x-axis label
    this.chartXAxisLabel.transition().attr('x', innerWidth / 2);
  };
}
