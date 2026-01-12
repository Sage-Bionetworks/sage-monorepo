import { Component, computed, input } from '@angular/core';
import { LegendDirective } from '@sagebionetworks/explorers/charts-angular';
import { ModelData, Sex } from '@sagebionetworks/model-ad/api-client';
import { MODEL_DETAILS_BOXPLOT_POINT_STYLES } from '@sagebionetworks/model-ad/config';
import { BoxplotComponent } from '@sagebionetworks/model-ad/ui';

@Component({
  selector: 'model-ad-model-details-boxplots-grid',
  imports: [BoxplotComponent, LegendDirective],
  templateUrl: './model-details-boxplots-grid.component.html',
  styleUrls: ['./model-details-boxplots-grid.component.scss'],
})
export class ModelDetailsBoxplotsGridComponent {
  modelDataList = input.required<ModelData[]>();
  genotypeOrder = input<string[] | undefined>();
  sexes = input.required<Sex[]>();

  pointStyles = computed(() => {
    return MODEL_DETAILS_BOXPLOT_POINT_STYLES.filter((pointStyle) =>
      this.sexes().includes(pointStyle.label as Sex),
    );
  });
}
