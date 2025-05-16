import { Component, inject, Input } from '@angular/core';
import { ConfigService } from '@sagebionetworks/model-ad/config';

@Component({
  selector: 'explorers-not-found',
  imports: [],
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss'],
})
export class NotFoundComponent {
  configService = inject(ConfigService);
  backgroundImagePath = 'explorers-assets/images/background.svg';
  email = '';

  constructor() {
    this.email = this.configService.config.supportEmail;
  }
}
