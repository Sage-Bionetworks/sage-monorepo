import {
  Component,
  ElementRef,
  EventEmitter,
  inject,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild,
  ViewEncapsulation,
} from '@angular/core';
import { BioDomain } from '@sagebionetworks/agora/api-client';
import { HelperService } from '@sagebionetworks/agora/services';
import * as d3 from 'd3';

@Component({
  selector: 'agora-biodomains-chart',
  standalone: true,
  templateUrl: './biodomains-chart.component.html',
  styleUrls: ['./biodomains-chart.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class BiodomainsChartComponent implements OnChanges, OnInit, OnDestroy {
  helperService = inject(HelperService);

  @Input() data: BioDomain[] | undefined;
  @Input() geneName = '';

  @Output() selectedBioDomainIndex = new EventEmitter<number | undefined>();

  @ViewChild('chart', { static: true }) chartRef: ElementRef<SVGElement> = {} as ElementRef;
  @ViewChild('tooltip', { static: true }) tooltip: ElementRef<HTMLElement> = {} as ElementRef;

  highlightColor = '#5081A7';

  selectedBioDomain = '';
  selectedIndex = 0;

  initialized = false;
  private chart!: d3.Selection<any, unknown, null, undefined>;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data'] && !changes['data'].firstChange) {
      this.hideChart();
      this.createChart();
    }
  }

  ngOnInit(): void {
    this.createChart();
  }

  ngOnDestroy(): void {
    this.destroyChart();
  }

  hideChart() {
    const svg = d3.select(this.chartRef.nativeElement);
    svg.selectAll('*').remove();
    svg.style('display', 'none');
  }

  initChart() {
    this.selectedIndex = 0;
    this.selectedBioDomain = '';
  }

  createChart() {
    const width = 500;
    const height = 560;

    this.initChart();

    this.selectedBioDomainIndex.emit(this.selectedIndex);

    if (!this.data) {
      // clear the existing chart as the data may have changed due to a new overlaypanel being displayed
      this.hideChart();
    } else {
      const svg = (this.chart = d3.select(this.chartRef.nativeElement));

      if (this.selectedIndex >= 0) this.selectedBioDomain = this.data[this.selectedIndex].biodomain;

      svg.attr('width', width).attr('height', height);

      svg.style('display', 'block');

      const labelWidth = 200;

      const barColor = '#8B8AD1';

      const xScale = d3
        .scaleLinear()
        .domain([0, d3.max(this.data, (d) => d.pct_linking_terms) as number])
        .range([0, 200]);

      const yScale = d3
        .scaleBand()
        .domain(this.data.map((d) => d.biodomain))
        .range([0, height])
        .paddingInner(0.4);

      // NEGATIVE SPACE NEXT TO BARS
      svg
        .selectAll('.negative-bars')
        .data(this.data)
        .enter()
        .append('rect')
        .attr('class', 'negative-bars')
        .attr('x', (d) => labelWidth + xScale(d.pct_linking_terms))
        .attr('y', (d) => yScale(d.biodomain) || 0)
        .attr('width', (d) => width - labelWidth - xScale(d.pct_linking_terms))
        .attr('height', yScale.bandwidth())
        .attr('fill', 'white')
        .on('click', (event) => {
          const index = svg
            .selectAll('.negative-bars')
            .nodes()
            .indexOf(event.target as HTMLElement);
          this.handleClick(bars, labels, barValues, index);
        })
        .on('mouseenter', (event) => {
          const index = svg
            .selectAll('.negative-bars')
            .nodes()
            .indexOf(event.target as HTMLElement);
          this.handleMouseEnter(bars, labels, barValues, index);
        })
        .on('mouseleave', (event) => {
          const index = svg
            .selectAll('.negative-bars')
            .nodes()
            .indexOf(event.target as HTMLElement);
          this.handleMouseLeave(bars, labels, barValues, index);
        });

      // BARS
      const bars = svg
        .selectAll('.bars')
        .data(this.data)
        .enter()
        .append('rect')
        .attr('class', 'bars')
        .attr('x', labelWidth)
        .attr('y', (d) => yScale(d.biodomain) || 0)
        .attr('width', (d) => xScale(d.pct_linking_terms))
        .attr('height', yScale.bandwidth())
        .attr('fill', barColor)
        .style('fill-opacity', (d) => (this.selectedBioDomain === d.biodomain ? '100%' : '50%'))
        .on('click', (event: MouseEvent) => {
          const index = svg
            .selectAll('.bars')
            .nodes()
            .indexOf(event.target as HTMLElement);
          this.handleClick(bars, labels, barValues, index);
        })
        .on('mouseover', (event) => {
          const index = svg
            .selectAll('.bars')
            .nodes()
            .indexOf(event.target as HTMLElement);
          this.handleMouseEnter(bars, labels, barValues, index);
        })
        .on('mouseleave', (event) => {
          const index = svg
            .selectAll('.bars')
            .nodes()
            .indexOf(event.target as HTMLElement);
          this.handleMouseLeave(bars, labels, barValues, index);
        });

      // BAR LABELS
      const labels = svg
        .selectAll('.bar-labels')
        .data(this.data)
        .enter()
        .append('text')
        .attr('class', 'bar-labels')
        .attr('x', labelWidth - 10)
        .attr('y', (d) => (yScale(d.biodomain) || 0) + yScale.bandwidth() / 2)
        .attr('dy', '0.35em')
        .attr('text-anchor', 'end')
        .style('font-size', '12px')
        .style('font-weight', (d) => (this.selectedBioDomain === d.biodomain ? 'bold' : 'normal'))
        .text((d) => d.biodomain)
        .on('click', (event) => {
          const index = svg
            .selectAll('.bar-labels')
            .nodes()
            .indexOf(event.target as HTMLElement);
          this.handleClick(bars, labels, barValues, index);
        })
        .on('mouseover', (event) => {
          const index = svg
            .selectAll('.bar-labels')
            .nodes()
            .indexOf(event.target as HTMLElement);
          this.handleMouseEnter(bars, labels, barValues, index);
        })
        .on('mousemove', (event: MouseEvent, data: BioDomain) => {
          const tooltipText = this.getToolTipText(data.pct_linking_terms);
          const tooltipCoordinates = this.getTooltipCoordinates(
            event.offsetX,
            yScale.bandwidth(),
            yScale(data.biodomain) || 0,
          );
          this.showTooltip(tooltipText, tooltipCoordinates.X, tooltipCoordinates.Y);
        })
        .on('mouseleave', (event) => {
          const index = svg
            .selectAll('.bar-labels')
            .nodes()
            .indexOf(event.target as HTMLElement);
          this.handleMouseLeave(bars, labels, barValues, index);
          this.hideTooltip(event);
        });

      // BAR VALUE
      const barValues = svg
        .selectAll('.bar-values')
        .data(this.data)
        .enter()
        .append('text')
        .attr('class', 'bar-values')
        .attr('x', (d) => labelWidth + xScale(d.pct_linking_terms) + 4)
        .attr('y', (d) => (yScale(d.biodomain) || 0) + yScale.bandwidth() / 2)
        .attr('dy', '0.35em')
        .attr('text-anchor', 'start')
        .style('font-size', '12px')
        .text((data) => {
          let percentage = this.helperService.roundNumber(data.pct_linking_terms, 1);
          if (percentage === '0.0') percentage = '0';
          return `${percentage}%`;
        })
        .style('display', (d) => (this.selectedBioDomain === d.biodomain ? 'block' : 'none'));

      this.initialized = true;
    }
  }

  handleClick(
    bars: d3.Selection<SVGRectElement, BioDomain, SVGElement, unknown>,
    labels: d3.Selection<SVGTextElement, BioDomain, SVGElement, unknown>,
    barValues: d3.Selection<SVGTextElement, BioDomain, SVGElement, unknown>,
    index: number,
  ) {
    this.selectedIndex = index;
    // emit change to index to populate GO terms
    this.selectedBioDomainIndex.emit(index);

    // reset all elements
    bars.style('fill-opacity', '50%');
    labels.style('font-weight', 'normal');
    labels.style('fill', 'black');
    barValues.style('display', 'none');

    const bar = d3.select(bars.nodes()[index]);
    bar.style('fill-opacity', '100%');

    const label = d3.select(labels.nodes()[index]);
    label.style('font-weight', 'bold');

    const barValue = d3.select(barValues.nodes()[index]);
    barValue.style('display', 'block');
  }

  handleMouseEnter(
    bars: d3.Selection<SVGRectElement, BioDomain, SVGElement, unknown>,
    labels: d3.Selection<SVGTextElement, BioDomain, SVGElement, undefined>,
    barValues: d3.Selection<SVGTextElement, BioDomain, SVGElement, undefined>,
    index: number,
  ) {
    const bar = d3.select(bars.nodes()[index]);
    this.highlightBar(bar, index);

    const label = d3.select(labels.nodes()[index]);
    this.highlightLabel(label, index);

    const barValue = d3.select(barValues.nodes()[index]);
    this.showBarValue(barValue, index);
  }

  handleMouseLeave(
    bars: d3.Selection<SVGRectElement, BioDomain, SVGElement, unknown>,
    labels: d3.Selection<SVGTextElement, BioDomain, SVGElement, undefined>,
    barValues: d3.Selection<SVGTextElement, BioDomain, SVGElement, undefined>,
    index: number,
  ) {
    const bar = d3.select(bars.nodes()[index]);
    this.unhighlightBar(bar, index);

    const label = d3.select(labels.nodes()[index]);
    this.unhighlightLabel(label, index);

    const barValue = d3.select(barValues.nodes()[index]);
    this.hideBarValue(barValue, index);
  }

  showBarValue(barValue: d3.Selection<SVGTextElement, unknown, null, undefined>, index: number) {
    if (index !== this.selectedIndex) {
      barValue.style('display', 'block');
    }
  }

  hideBarValue(barValue: d3.Selection<SVGTextElement, unknown, null, undefined>, index: number) {
    if (index !== this.selectedIndex) {
      barValue.style('display', 'none');
    }
  }

  highlightBar(bar: d3.Selection<SVGRectElement, unknown, null, undefined>, index: number) {
    // only highlight if it isn't the selected bar
    if (index !== this.selectedIndex) {
      bar.style('fill-opacity', '100%');
    }
  }

  unhighlightBar(bar: d3.Selection<SVGRectElement, unknown, null, undefined>, index: number) {
    if (index !== this.selectedIndex) {
      bar.style('fill-opacity', '50%');
    }
  }

  highlightLabel(label: d3.Selection<SVGTextElement, unknown, null, undefined>, index: number) {
    // only bold if it isn't the selected bar
    if (index !== this.selectedIndex) {
      label.style('font-weight', 'bold');
      label.style('fill', this.highlightColor);
    }
  }

  unhighlightLabel(label: d3.Selection<SVGTextElement, unknown, null, undefined>, index: number) {
    if (index !== this.selectedIndex) {
      label.style('font-weight', 'normal');
      label.style('fill', 'black');
    }
  }

  getTooltipCoordinates(xBarPosition: number, yBarWidth: number, yBarPosition: number) {
    // x-coordinate would be the left margin + x-barPosition
    const x = xBarPosition;
    // y-coordinate would be the y-barPosition + top margin + half of the bar width
    const y = yBarPosition + yBarWidth / 2;
    return { X: x, Y: y };
  }

  getToolTipText(linkingTerms: number) {
    if (linkingTerms === 0) return `No GO Terms link this biological domain to ${this.geneName}`;
    return `Click to explore to GO Terms that link this biological domain to ${this.geneName}`;
  }

  showTooltip(text: string, x: number, y: number) {
    const tooltipElement = this.tooltip.nativeElement;
    tooltipElement.innerHTML = text;
    tooltipElement.style.left = `${x}px`;
    tooltipElement.style.top = `${y}px`;
    tooltipElement.style.display = 'block';
  }

  hideTooltip(event: MouseEvent) {
    const tooltipElement = this.tooltip.nativeElement;
    // check whether mouse is over the tooltip otherwise there will be flicker
    if (tooltipElement && tooltipElement.contains(event.relatedTarget as Node)) {
      return;
    }

    if (tooltipElement.style.display === 'block') tooltipElement.style.display = 'none';
  }

  destroyChart() {
    if (this.initialized) this.chart.remove();
  }
}
