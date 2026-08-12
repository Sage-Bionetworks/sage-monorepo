import { Component, input } from '@angular/core';
import { MarmosetModel } from '@sagebionetworks/model-ad/api-client';
import { ModelDetailsModifiedGenesComponent } from '../model-details-modified-genes/model-details-modified-genes.component';

@Component({
  selector: 'model-ad-marmoset-model-details-hero',
  imports: [ModelDetailsModifiedGenesComponent],
  templateUrl: './marmoset-model-details-hero.component.html',
  styleUrls: ['./marmoset-model-details-hero.component.scss'],
})
export class MarmosetModelDetailsHeroComponent {
  readonly backgroundImagePath = 'explorers-assets/images/background.svg';

  model = input.required<MarmosetModel>();
}
