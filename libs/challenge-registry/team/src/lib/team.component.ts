import { Component, Inject } from '@angular/core';
import {
  APP_CONFIG,
  AppConfig,
} from '@sagebionetworks/challenge-registry/config';

@Component({
  selector: 'challenge-registry-team',
  templateUrl: './team.component.html',
  styleUrls: ['./team.component.scss'],
})
export class TeamComponent {
  public appVersion: string;

  constructor(@Inject(APP_CONFIG) private appConfig: AppConfig) {
    this.appVersion = appConfig.appVersion;
  }
}
