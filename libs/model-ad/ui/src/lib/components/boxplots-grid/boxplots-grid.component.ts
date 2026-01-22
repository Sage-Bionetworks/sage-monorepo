import { Component, computed, input } from '@angular/core';
import { LegendDirective } from '@sagebionetworks/explorers/charts-angular';
import { Sex } from '@sagebionetworks/model-ad/api-client';
import { BOXPLOT_POINT_STYLES } from '@sagebionetworks/model-ad/config';
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

  pointStyles = computed(() => {
    const sexes = this.sexFilter();
    if (sexes) {
      return BOXPLOT_POINT_STYLES.filter((pointStyle) => sexes.includes(pointStyle.label as Sex));
    }
    return BOXPLOT_POINT_STYLES;
  });
}
