import { Component, computed, input } from '@angular/core';
import { LegendDirective } from '@sagebionetworks/explorers/charts-angular';
import { Sex } from '@sagebionetworks/model-ad/api-client';
import { getPointStylesBySex } from '../../utils';
import { BoxplotComponent, BoxplotData } from '../boxplot/boxplot.component';

@Component({
  selector: 'model-ad-boxplots-grid',
  imports: [BoxplotComponent, LegendDirective],
  templateUrl: './boxplots-grid.component.html',
  styleUrls: ['./boxplots-grid.component.scss'],
})
export class BoxplotsGridComponent {
  boxplotDataList = input.required<BoxplotData[]>();
  xAxisOrder = input<string[] | undefined>();
  sexFilter = input<Sex[] | undefined>();

  pointStyles = computed(() => getPointStylesBySex(this.sexFilter()));
}
