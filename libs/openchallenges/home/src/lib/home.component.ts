import { Component, inject, Renderer2 } from '@angular/core';
import { ConfigService } from '@sagebionetworks/openchallenges/web/angular/config';
import { FooterComponent } from '@sagebionetworks/openchallenges/ui';
import { SeoService } from '@sagebionetworks/shared/util';
import { ChallengeHostListComponent } from './challenge-host-list/challenge-host-list.component';
import { ChallengeRegistrationComponent } from './challenge-registration/challenge-registration.component';
import { ChallengeSearchComponent } from './challenge-search/challenge-search.component';
// import { FeaturedChallengeListComponent } from './featured-challenge-list/featured-challenge-list.component';
import { SponsorListComponent } from './sponsor-list/sponsor-list.component';
import { PlatformsComponent } from './platforms/platforms.component';
import { StatisticsViewerComponent } from './statistics-viewer/statistics-viewer.component';
// import { TopicsViewerComponent } from './topics-viewer/topics-viewer.component';
import { getSeoData } from './home-seo-data';
import { RandomChallengeListComponent } from './random-challenge-list/random-challenge-list.component';

@Component({
  selector: 'openchallenges-home',
  imports: [
    ChallengeHostListComponent,
    ChallengeRegistrationComponent,
    ChallengeSearchComponent,
    RandomChallengeListComponent,
    SponsorListComponent,
    PlatformsComponent,
    StatisticsViewerComponent,
    FooterComponent,
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  private readonly configService = inject(ConfigService);
  private readonly seoService = inject(SeoService);
  private readonly renderer2 = inject(Renderer2);

  public appVersion: string;
  public dataUpdatedOn: string;
  public privacyPolicyUrl: string;
  public termsOfUseUrl: string;
  public apiDocsUrl: string;
  public showAnnouncement: boolean;

  constructor() {
    this.appVersion = this.configService.config.app.version;
    this.dataUpdatedOn = this.configService.config.data.updatedOn;
    this.privacyPolicyUrl = this.configService.config.urls.privacyPolicy;
    this.termsOfUseUrl = this.configService.config.urls.termsOfUse;
    this.apiDocsUrl = this.configService.config.api.docs.url;
    this.showAnnouncement = this.configService.config.app.announcement.show;
    this.seoService.setData(getSeoData(), this.renderer2);
  }
}
