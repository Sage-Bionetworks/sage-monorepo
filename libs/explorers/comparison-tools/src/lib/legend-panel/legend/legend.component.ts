import { Component, input } from '@angular/core';

@Component({
  selector: 'explorers-legend',
  imports: [],
  templateUrl: './legend.component.html',
  styleUrls: ['./legend.component.scss'],
})
export class LegendComponent {
  colorChartLowerLabel = input('');
  colorChartUpperLabel = input('');
  colorChartText = input('');
  sizeChartLowerLabel = input('');
  sizeChartUpperLabel = input('');
  sizeChartText = input('');
}
