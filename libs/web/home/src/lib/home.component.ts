import { Component, Inject } from '@angular/core';
import { APP_CONFIG, AppConfig } from '@sagebionetworks/web/config';

@Component({
  selector: 'challenge-registry-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  public appVersion: string;

  constructor(@Inject(APP_CONFIG) private appConfig: AppConfig) {
    this.appVersion = appConfig.appVersion;
  }
}
