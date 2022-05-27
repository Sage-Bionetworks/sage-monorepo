import { Component, Inject } from '@angular/core';
import { APP_CONFIG, AppConfig } from '@sage-bionetworks/web/config';

@Component({
  selector: 'challenge-registry-about',
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss'],
})
export class AboutComponent {
  public appVersion: string;

  constructor(@Inject(APP_CONFIG) private appConfig: AppConfig) {
    this.appVersion = appConfig.appVersion;
  }
}
