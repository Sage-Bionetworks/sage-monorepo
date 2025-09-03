import { Component, computed, input } from '@angular/core';
import { IndividualData, ModelData } from '@sagebionetworks/model-ad/api-client-angular';
import { MODEL_DETAILS_BOXPLOT_POINT_STYLES } from '@sagebionetworks/model-ad/config';
import { LegendDirective } from '@sagebionetworks/shared/charts-angular';
import { ModelDetailsBoxplotComponent } from '../model-details-boxplot/model-details-boxplot.component';

@Component({
  selector: 'model-ad-model-details-boxplots-grid',
  imports: [ModelDetailsBoxplotComponent, LegendDirective],
  templateUrl: './model-details-boxplots-grid.component.html',
  styleUrls: ['./model-details-boxplots-grid.component.scss'],
})
export class ModelDetailsBoxplotsGridComponent {
  modelDataList = input.required<ModelData[]>();
  genotypeOrder = input<string[] | undefined>();
  sexes = input.required<IndividualData.SexEnum[]>();

  pointStyles = computed(() => {
    return MODEL_DETAILS_BOXPLOT_POINT_STYLES.filter((pointStyle) =>
      this.sexes().includes(pointStyle.label as IndividualData.SexEnum),
    );
  });
}
