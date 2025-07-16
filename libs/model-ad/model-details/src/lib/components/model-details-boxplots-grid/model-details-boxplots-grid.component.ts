import { Component, input } from '@angular/core';
import { DecodeGreekEntityPipe } from '@sagebionetworks/explorers/util';
import { IndividualData, ModelData } from '@sagebionetworks/model-ad/api-client-angular';
import { ModelDetailsBoxplotComponent } from '../model-details-boxplot/model-details-boxplot.component';

@Component({
  selector: 'model-ad-model-details-boxplots-grid',
  imports: [ModelDetailsBoxplotComponent, DecodeGreekEntityPipe],
  templateUrl: './model-details-boxplots-grid.component.html',
  styleUrls: ['./model-details-boxplots-grid.component.scss'],
})
export class ModelDetailsBoxplotsGridComponent {
  modelDataList = input.required<ModelData[]>();
  sexes = input.required<IndividualData.SexEnum[]>();
  title = input.required<string>();
}
