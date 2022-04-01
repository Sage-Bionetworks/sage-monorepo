import { Component, Inject } from '@angular/core';
import { APP_CONFIG, AppConfig } from '@challenge-registry/web/config';

@Component({
  selector: 'challenge-registry-org-search',
  templateUrl: './org-search.component.html',
  styleUrls: ['./org-search.component.scss'],
})
export class OrgSearchComponent {
  public appVersion: string;

  constructor(@Inject(APP_CONFIG) private appConfig: AppConfig) {
    this.appVersion = appConfig.appVersion;
  }
}
