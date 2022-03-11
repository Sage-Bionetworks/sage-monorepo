import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchViewerModule } from './search-viewer/search-viewer.module';
import { StatisticsViewerModule } from './statistics-viewer/statistics-viewer.module';
import { TopicsViewerModule } from './topics-viewer/topics-viewer.module';
import { ChallengeHostListModule } from './challenge-host-list/challenge-host-list.module';
import { ChallengeRegistrationModule } from './challenge-registration/challenge-registration.module';

@NgModule({
  imports: [CommonModule],
  declarations: [],
  exports: [
    SearchViewerModule,
    StatisticsViewerModule,
    TopicsViewerModule,
    ChallengeHostListModule,
    ChallengeRegistrationModule,
  ],
})
export class WebFeatureHomeModule {}
