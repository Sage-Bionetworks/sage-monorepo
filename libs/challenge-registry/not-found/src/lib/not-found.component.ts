import { Component } from '@angular/core';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';

@Component({
  selector: 'challenge-registry-not-found',
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss'],
})
export class NotFoundComponent {
  public appVersion: string;

  constructor(private readonly configService: ConfigService) {
    this.appVersion = this.configService.config.appVersion;
  }
}
