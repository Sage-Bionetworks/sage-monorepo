import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedUtilModule } from '@sagebionetworks/shared/util';
import { FooterComponent, UiModule } from '@sagebionetworks/openchallenges/ui';
import { HomeComponent } from './home.component';
import { FeaturedChallengeListModule } from './featured-challenge-list/featured-challenge-list.module';
import { SponsorListModule } from './sponsor-list/sponsor-list.module';
import { StatisticsViewerModule } from './statistics-viewer/statistics-viewer.module';
import { TopicsViewerModule } from './topics-viewer/topics-viewer.module';
import { HomeRoutingModule } from './home-routing.module';
import { ChallengeHostListComponent } from './challenge-host-list/challenge-host-list.component';
import { ChallengeRegistrationComponent } from './challenge-registration/challenge-registration.component';
import { ChallengeSearchComponent } from './challenge-search/challenge-search.component';

@NgModule({
  declarations: [HomeComponent],
  imports: [
    CommonModule,
    HomeRoutingModule,
    UiModule,
    ChallengeHostListComponent,
    ChallengeRegistrationComponent,
    ChallengeSearchComponent,
    FeaturedChallengeListModule,
    SponsorListModule,
    StatisticsViewerModule,
    TopicsViewerModule,
    SharedUtilModule,
    FooterComponent,
  ],
})
export class HomeModule {}
