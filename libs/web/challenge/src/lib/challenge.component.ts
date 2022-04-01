import { Component, Inject } from '@angular/core';
import { APP_CONFIG, AppConfig } from '@challenge-registry/web/config';

@Component({
  selector: 'challenge-registry-challenges',
  templateUrl: './challenge.component.html',
  styleUrls: ['./challenge.component.scss'],
})
export class ChallengeComponent {
  public appVersion: string;

  constructor(@Inject(APP_CONFIG) private appConfig: AppConfig) {
    this.appVersion = appConfig.appVersion;
  }
}
