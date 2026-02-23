import { Component } from '@angular/core';
import { DEFAULT_HERO_BACKGROUND_IMAGE_PATH } from '@sagebionetworks/agora/config';

@Component({
  selector: 'agora-nomination-form',
  standalone: true,
  templateUrl: './nomination-form.component.html',
  styleUrls: ['./nomination-form.component.scss'],
})
export class NominationFormComponent {
  readonly heroBackgroundImagePath = DEFAULT_HERO_BACKGROUND_IMAGE_PATH;
}
