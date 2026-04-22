import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { Drug } from '@sagebionetworks/agora/api-client';
import { DEFAULT_HERO_BACKGROUND_IMAGE_PATH } from '@sagebionetworks/agora/config';

@Component({
  selector: 'agora-drug-details-hero',
  imports: [CommonModule],
  templateUrl: './drug-details-hero.component.html',
  styleUrls: ['./drug-details-hero.component.scss'],
})
export class DrugDetailsHeroComponent {
  readonly backgroundImagePath = DEFAULT_HERO_BACKGROUND_IMAGE_PATH;

  drug = input.required<Drug>();
}
