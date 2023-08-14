import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatLegacyTabsModule as MatTabsModule } from '@angular/material/legacy-tabs';
import { MatIconModule } from '@angular/material/icon';
import { ChallengeComponent } from './challenge.component';
import { ChallengeOverviewModule } from './challenge-overview/challenge-overview.module';
import { ChallengeStargazersModule } from './challenge-stargazers/challenge-stargazers.module';
import { ChallengeRoutingModule } from './challenge-routing.module';
import { ChallengeStatsModule } from './challenge-stats/challenge-stats.module';
// import { ProgressBarModule } from 'primeng/progressbar';
import {
  AvatarComponent,
  FooterComponent,
  UiModule,
} from '@sagebionetworks/openchallenges/ui';
import { ChallengeContributorsComponent } from './challenge-contributors/challenge-contributors.component';
import { ChallengeOrganizersComponent } from './challenge-organizers/challenge-organizers.component';

@NgModule({
  declarations: [ChallengeComponent],
  imports: [
    CommonModule,
    MatTabsModule,
    MatIconModule,
    ChallengeOverviewModule,
    ChallengeOrganizersComponent,
    ChallengeContributorsComponent,
    ChallengeStargazersModule,
    ChallengeRoutingModule,
    ChallengeStatsModule,
    // ProgressBarModule,
    UiModule,
    AvatarComponent,
    FooterComponent,
  ],
  exports: [ChallengeComponent],
})
export class ChallengeModule {}
