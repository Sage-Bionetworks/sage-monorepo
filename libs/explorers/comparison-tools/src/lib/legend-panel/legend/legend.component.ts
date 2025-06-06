import { Component, Input } from '@angular/core';

@Component({
  selector: 'explorers-legend',
  imports: [],
  templateUrl: './legend.component.html',
  styleUrls: ['./legend.component.scss'],
})
export class LegendComponent {
  @Input({ required: true }) colorChartLowerLabel = '';
  @Input({ required: true }) colorChartUpperLabel = '';
  @Input({ required: true }) colorChartText = '';
  @Input({ required: true }) sizeChartLowerLabel = '';
  @Input({ required: true }) sizeChartUpperLabel = '';
  @Input({ required: true }) sizeChartText = '';
}
