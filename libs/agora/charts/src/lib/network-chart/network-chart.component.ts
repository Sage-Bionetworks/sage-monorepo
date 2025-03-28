/* eslint-disable @typescript-eslint/no-this-alias */
/* eslint-disable @angular-eslint/no-output-on-prefix */
import {
  Component,
  ElementRef,
  EventEmitter,
  inject,
  Input,
  Output,
  ViewChild,
} from '@angular/core';
import * as d3 from 'd3';

import {
  NetworkChartData,
  NetworkChartLink,
  NetworkChartNode,
} from '@sagebionetworks/agora/models';
import { HelperService } from '@sagebionetworks/agora/services';
import { hexagonSymbol } from './symbol-hexagon';

@Component({
  selector: 'agora-network-chart',
  standalone: true,
  templateUrl: './network-chart.component.html',
  styleUrls: ['./network-chart.component.scss'],
})
export class NetworkChartComponent {
  helperService = inject(HelperService);

  _data: NetworkChartData | undefined;
  get data(): NetworkChartData | undefined {
    return this._data;
  }
  @Input() set data(data: NetworkChartData | undefined) {
    this._data = data;
    this.init();
  }

  _selectedFilter = 0;
  get selectedFilter(): number {
    return this._selectedFilter;
  }
  @Input() set selectedFilter(n: number) {
    this._selectedFilter = n;
    this.filter();
  }

  width = 800;
  height = 800;

  group: any;
  nodes: any;
  links: any;
  mainNode: any;
  texts: any;
  simulation: any;

  selectedNode: NetworkChartNode | undefined;
  zoomHandler: any;

  chart: any;
  isInitialized = false;

  resizeTimer: ReturnType<typeof setTimeout> | number = 0;

  @ViewChild('chartContainer', { static: true }) chartContainer: ElementRef = {} as ElementRef;

  @Output() onNodeClick: EventEmitter<any> = new EventEmitter();

  init() {
    if (!this._data?.nodes?.length || !this.chartContainer?.nativeElement) {
      return;
    }

    this.initChart();
    this.isInitialized = true;
  }

  initChart() {
    if (!this._data?.nodes?.length) {
      return;
    }

    this.width = this.chartContainer.nativeElement.parentElement.offsetWidth - 30;
    this.height = 400 + 700 * (this._data.nodes.length / 100);

    this.chart = d3
      .select(this.chartContainer.nativeElement)
      .append('svg')
      .attr('width', this.width)
      .attr('height', this.height);

    this.group = this.chart.append('g');

    this.initZoom();
    this.initLinks();
    this.initNodes();
    this.initSimulation();

    this.filter();
  }

  initZoom() {
    this.zoomHandler = d3
      .zoom()
      // Don't allow the zoomed area to be bigger than the viewport.
      .scaleExtent([1, 1])
      .translateExtent([
        [-200, -300],
        [this.width + 200, this.height + 300],
      ])
      .on('zoom', (e) => {
        // Zoom functions, this in this context is the svg
        this.group.attr('transform', e.transform);
      });
    this.zoomHandler(this.chart);
    this.chart.call(this.zoomHandler);
  }

  initLinks() {
    if (!this._data?.links?.length) {
      return;
    }

    this.links = this.group
      .selectAll('line')
      .data(this._data.links)
      .enter()
      .append('line')
      .attr('stroke-width', 2);

    this.updateLinkClasses();
  }

  getLinkClasses(link: NetworkChartLink) {
    const classes = ['network-chart-link'];

    if (link.class) {
      classes.push(link.class);
    }

    return classes.join(' ');
  }

  updateLinkClasses() {
    this.links.attr('class', (link: NetworkChartLink) => {
      return this.getLinkClasses(link);
    });
  }

  initNodes() {
    if (!this._data?.nodes?.length) {
      return;
    }

    this.selectedNode = this._data.nodes[0];

    this.mainNode = this.group
      .selectAll('path')
      .data([this._data.nodes[0]])
      .enter()
      .append('path')
      .attr('d', d3.symbol().size(900).type(hexagonSymbol))
      .on('click', (e: any, NetworkNode: any) => {
        this.selectedNode = NetworkNode;
        this.updateNodesClasses();
        this.onNodeClick.emit(NetworkNode);
      });

    this.nodes = this.group
      .selectAll('circle')
      .data(this._data.nodes.slice(1))
      .enter()
      .append('circle')
      .attr('r', 10)
      .on('click', (e: any, node: any) => {
        this.selectedNode = node;
        this.updateNodesClasses();
        this.onNodeClick.emit(node);
      });

    this.texts = this.group
      .selectAll('text')
      .data(this._data.nodes)
      .enter()
      .append('text')
      .text((node: any) => node?.label)
      .attr('font-size', 12);

    this.updateNodesClasses();
  }

  getNodeClasses(node: NetworkChartNode) {
    const classes = ['network-chart-node'];

    if (node.id === this._data?.nodes[0].id) {
      classes.push('main');
    }

    if (node.id === this.selectedNode?.id) {
      classes.push('selected');
    }

    if (node.class) {
      classes.push(node.class);
    }

    return classes.join(' ');
  }

  updateNodesClasses() {
    this.mainNode.attr('class', (node: NetworkChartNode) => {
      return this.getNodeClasses(node);
    });

    this.nodes.attr('class', (node: NetworkChartNode) => {
      return this.getNodeClasses(node);
    });
  }

  initSimulation() {
    if (!this._data?.nodes?.length) {
      return;
    }

    this.simulation = d3
      .forceSimulation(this._data.nodes)
      .force(
        'NetworkLink',
        d3
          .forceLink()
          .id(function (d: any) {
            return d.id;
          })
          .links(this._data.links),
      )
      .force('charge', d3.forceManyBody().strength(-450))
      .force('center', d3.forceCenter(this.width / 2, this.height / 2))
      .force(
        'collision',
        d3.forceCollide().radius(() => 35),
      )
      .alphaDecay(0.5)
      .on('end', () => {
        this.refreshPositions();
      });
  }

  refreshPositions() {
    this.links
      .attr('x1', function (d: any) {
        return d.source.x;
      })
      .attr('y1', function (d: any) {
        return d.source.y;
      })
      .attr('x2', function (d: any) {
        return d.target.x;
      })
      .attr('y2', function (d: any) {
        return d.target.y;
      });

    this.nodes
      .attr('cx', function (d: any) {
        return d.x;
      })
      .attr('cy', function (d: any) {
        return d.y;
      });

    this.mainNode.style('transform', function (d: any) {
      return 'translate(' + d.x + 'px, ' + d.y + 'px) rotate(30deg)';
    });

    this.texts
      .attr('x', function (d: any) {
        return d.x;
      })
      .attr('dx', (d: any) => {
        // A font size of 12 has 16 pixels per letter, so we pick
        // half the word and make a negative dx. The anchor is in
        // the middle so we half the result again
        return (-d.label.length * 16) / 2 / 2;
      })
      .attr('y', function (d: any) {
        return d.y + 30;
      });
  }

  filter() {
    const hiddenNodes: string[] = [];

    this.nodes.attr('display', (node: NetworkChartNode) => {
      if ((node.value || 0) < this._selectedFilter) {
        hiddenNodes.push(node.id);
        return 'none';
      }
      return 'block';
    });

    this.texts.attr('display', (node: NetworkChartNode) =>
      (node.value || 0) < this._selectedFilter ? 'none' : 'block',
    );

    this.links.attr('display', (link: NetworkChartLink) =>
      hiddenNodes.includes(link.source.id) || hiddenNodes.includes(link.target.id)
        ? 'none'
        : 'block',
    );
  }

  onResize() {
    const self = this;
    clearTimeout(this.resizeTimer);
    this.resizeTimer = setTimeout(() => {
      self.width = self.chartContainer.nativeElement.parentElement.offsetWidth - 30;
      self.height = 400 + 700 * ((self._data?.nodes?.length || 0) / 100);
      self.chart.attr('width', self.width).attr('height', self.height);
      self.group.attr('transform', 'translate(0, 0)');
    }, 100);
  }
}
