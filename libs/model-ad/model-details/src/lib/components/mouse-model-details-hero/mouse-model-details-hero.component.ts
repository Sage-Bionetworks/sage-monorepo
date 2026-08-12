import { Component, computed, input } from '@angular/core';
import { SanitizeHtmlPipe } from '@sagebionetworks/explorers/util';
import { MouseModel } from '@sagebionetworks/model-ad/api-client';
import { ModelDetailsModifiedGenesComponent } from '../model-details-modified-genes/model-details-modified-genes.component';

@Component({
  selector: 'model-ad-mouse-model-details-hero',
  imports: [SanitizeHtmlPipe, ModelDetailsModifiedGenesComponent],
  templateUrl: './mouse-model-details-hero.component.html',
  styleUrls: ['./mouse-model-details-hero.component.scss'],
})
export class MouseModelDetailsHeroComponent {
  readonly backgroundImagePath = 'explorers-assets/images/background.svg';
  readonly JAX_STRAIN_URL = 'https://www.jax.org/strain';

  readonly MATCHED_CONTROLS_URLS: Record<string, string> = {
    B6129: `${this.JAX_STRAIN_URL}/101045`,
    '5xFAD': `${this.JAX_STRAIN_URL}/008730`,
    'C57BL/6J': `${this.JAX_STRAIN_URL}/000664`,
  };

  model = input.required<MouseModel>();

  matchedControlsHeading = computed(
    () => `Matched Control${this.model().matched_controls.length > 1 ? 's' : ''}`,
  );
}
