import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { FooterComponent } from '@sagebionetworks/openchallenges/ui';
import { SharedUtilModule } from '@sagebionetworks/shared/util';
import { ChallengeHostListComponent } from './challenge-host-list/challenge-host-list.component';
import { ChallengeRegistrationComponent } from './challenge-registration/challenge-registration.component';
import { ChallengeSearchComponent } from './challenge-search/challenge-search.component';
import { FeaturedChallengeListComponent } from './featured-challenge-list/featured-challenge-list.component';
import { SponsorListComponent } from './sponsor-list/sponsor-list.component';
// import { StatisticsViewerComponent } from './statistics-viewer/statistics-viewer.component';
import { TopicsViewerComponent } from './topics-viewer/topics-viewer.component';

@Component({
  selector: 'openchallenges-home',
  standalone: true,
  imports: [
    CommonModule,
    ChallengeHostListComponent,
    ChallengeRegistrationComponent,
    ChallengeSearchComponent,
    FeaturedChallengeListComponent,
    SponsorListComponent,
    // StatisticsViewerComponent,
    TopicsViewerComponent,
    SharedUtilModule,
    FooterComponent,
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  public appVersion: string;
  public dataUpdatedOn: string;
  public privacyPolicyUrl: string;
  public termsOfUseUrl: string;

  constructor(private readonly configService: ConfigService) {
    this.appVersion = this.configService.config.appVersion;
    this.dataUpdatedOn = this.configService.config.dataUpdatedOn;
    this.privacyPolicyUrl = this.configService.config.privacyPolicyUrl;
    this.termsOfUseUrl = this.configService.config.termsOfUseUrl;
  }
}
