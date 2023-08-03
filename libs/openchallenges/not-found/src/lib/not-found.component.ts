import { Component } from '@angular/core';
import { ConfigService } from '@sagebionetworks/openchallenges/config';

@Component({
  selector: 'openchallenges-not-found',
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss'],
})
export class NotFoundComponent {
  public appVersion: string;
  public dataUpdatedOn: string;

  constructor(private readonly configService: ConfigService) {
    this.appVersion = this.configService.config.appVersion;
    this.dataUpdatedOn = this.configService.config.dataUpdatedOn;
  }
}
