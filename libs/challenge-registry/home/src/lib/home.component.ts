import { Component, Inject } from '@angular/core';
import { KAuthService } from '@sagebionetworks/challenge-registry/auth';
import {
  APP_CONFIG,
  AppConfig,
} from '@sagebionetworks/challenge-registry/config';

@Component({
  selector: 'challenge-registry-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  public appVersion: string;

  constructor(
    @Inject(APP_CONFIG) private appConfig: AppConfig,
    private kauthService: KAuthService
  ) {
    this.appVersion = appConfig.appVersion;
  }

  logout(): void {
    console.log('Logout');
    this.kauthService.logout();
  }
}
