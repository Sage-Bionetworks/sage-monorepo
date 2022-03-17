import { Component, Inject } from '@angular/core';
import { APP_CONFIG, AppConfig } from '@challenge-registry/web/config';

@Component({
  selector: 'challenge-registry-not-found',
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss'],
})
export class NotFoundComponent {
  public appVersion: string;

  constructor(@Inject(APP_CONFIG) private appConfig: AppConfig) {
    this.appVersion = appConfig.appVersion;
  }
}
