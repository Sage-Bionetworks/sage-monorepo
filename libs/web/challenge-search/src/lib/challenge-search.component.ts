import { Component, Inject } from '@angular/core';
import { APP_CONFIG, AppConfig } from '@sage-bionetworks/web/config';

@Component({
  selector: 'challenge-registry-challenge-search',
  templateUrl: './challenge-search.component.html',
  styleUrls: ['./challenge-search.component.scss'],
})
export class ChallengeSearchComponent {
  public appVersion: string;

  constructor(@Inject(APP_CONFIG) private appConfig: AppConfig) {
    this.appVersion = appConfig.appVersion;
  }
}
