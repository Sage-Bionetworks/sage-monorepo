import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatLegacyTabsModule as MatTabsModule } from '@angular/material/legacy-tabs';
import { MatIconModule } from '@angular/material/icon';
import { ChallengeComponent } from './challenge.component';
import { ChallengeRoutingModule } from './challenge-routing.module';
import {
  AvatarComponent,
  FooterComponent,
  UiModule,
} from '@sagebionetworks/openchallenges/ui';
import { ChallengeContributorsComponent } from './challenge-contributors/challenge-contributors.component';
import { ChallengeOrganizersComponent } from './challenge-organizers/challenge-organizers.component';
import { ChallengeOverviewComponent } from './challenge-overview/challenge-overview.component';
import { ChallengeStargazersComponent } from './challenge-stargazers/challenge-stargazers.component';
import { ChallengeStatsComponent } from './challenge-stats/challenge-stats.component';

@NgModule({
  declarations: [ChallengeComponent],
  imports: [
    CommonModule,
    MatTabsModule,
    MatIconModule,
    ChallengeOverviewComponent,
    ChallengeOrganizersComponent,
    ChallengeContributorsComponent,
    ChallengeStargazersComponent,
    ChallengeRoutingModule,
    ChallengeStatsComponent,
    // ProgressBarModule,
    UiModule,
    AvatarComponent,
    FooterComponent,
  ],
  exports: [ChallengeComponent],
})
export class ChallengeModule {}
