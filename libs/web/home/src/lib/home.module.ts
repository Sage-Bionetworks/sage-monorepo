import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { WebUiModule } from '@challenge-registry/web/ui';
import { HomeComponent } from './home.component';
import { ChallengeSearchModule } from './challenge-search/challenge-search.module';
import { ChallengeHostListModule } from './challenge-host-list/challenge-host-list.module';
import { ChallengeRegistrationModule } from './challenge-registration/challenge-registration.module';
import { FeaturedChallengeListModule } from './featured-challenge-list/featured-challenge-list.module';
import { SponsorListModule } from './sponsor-list/sponsor-list.module';
import { StatisticsViewerModule } from './statistics-viewer/statistics-viewer.module';
import { TopicsViewerModule } from './topics-viewer/topics-viewer.module';

const routes: Routes = [{ path: '', component: HomeComponent }];

@NgModule({
  declarations: [HomeComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    WebUiModule,
    ChallengeHostListModule,
    ChallengeRegistrationModule,
    ChallengeSearchModule,
    FeaturedChallengeListModule,
    SponsorListModule,
    StatisticsViewerModule,
    TopicsViewerModule,
  ],
  exports: [HomeComponent],
})
export class HomeModule {}
