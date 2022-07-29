import { Component, Inject } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
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
    private title: Title,
    private meta: Meta
  ) {
    this.appVersion = appConfig.appVersion;

    // SEO metadata
    this.title.setTitle('Home - Challenge Registry');
    this.meta.addTag({
      name: 'description',
      content: 'The homepage of the Challenge Registry',
    });
  }
}
