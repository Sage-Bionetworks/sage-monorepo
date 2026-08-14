import { Component, input } from '@angular/core';
import { ResourceCardData } from '@sagebionetworks/explorers/models';
import { ResourceCardsComponent } from '@sagebionetworks/explorers/ui';

@Component({
  selector: 'model-ad-model-details-resources',
  imports: [ResourceCardsComponent],
  templateUrl: './model-details-resources.component.html',
  styleUrls: ['./model-details-resources.component.scss'],
})
export class ModelDetailsResourcesComponent {
  modelSpecificResourceCards = input<ResourceCardData[]>([]);
  additionalResourceCards = input<ResourceCardData[]>([]);
}
