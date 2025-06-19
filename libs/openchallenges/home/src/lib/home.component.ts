
import { Component, Renderer2 } from '@angular/core';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { FooterComponent } from '@sagebionetworks/openchallenges/ui';
import { AppShellNoRenderDirective, SeoService } from '@sagebionetworks/shared/util';
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
    AppShellNoRenderDirective
],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  public appVersion: string;
  public dataUpdatedOn: string;
  public privacyPolicyUrl: string;
  public termsOfUseUrl: string;
  public apiDocsUrl: string;
  public showAnnouncement: boolean;

  constructor(
    private readonly configService: ConfigService,
    private seoService: SeoService,
    private renderer2: Renderer2,
  ) {
    this.appVersion = this.configService.config.appVersion;
    this.dataUpdatedOn = this.configService.config.dataUpdatedOn;
    this.privacyPolicyUrl = this.configService.config.privacyPolicyUrl;
    this.termsOfUseUrl = this.configService.config.termsOfUseUrl;
    this.apiDocsUrl = this.configService.config.apiDocsUrl;
    this.showAnnouncement = this.configService.config.showAnnouncement;
    this.seoService.setData(getSeoData(), this.renderer2);
  }
}
