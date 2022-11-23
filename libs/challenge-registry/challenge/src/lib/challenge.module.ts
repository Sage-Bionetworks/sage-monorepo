import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { ChallengeComponent } from './challenge.component';
import { ChallengeDetailsModule } from './challenge-details/challenge-details.module';
import { ChallengeOverviewModule } from './challenge-overview/challenge-overview.module';
import { ChallengeOrganizersModule } from './challenge-organizers/challenge-organizers.module';
import { ChallengeSponsorsModule } from './challenge-sponsors/challenge-sponsors.module';
import { ChallengeStargazersModule } from './challenge-stargazers/challenge-stargazers.module';
import { ChallengeRoutingModule } from './challenge-routing.module';
import { ChallengeStatsModule } from './challenge-stats/challenge-stats.module';
import { ProgressBarModule } from 'primeng/progressbar';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';

@NgModule({
  declarations: [ChallengeComponent],
  imports: [
    CommonModule,
    MatTabsModule,
    MatIconModule,
    ChallengeDetailsModule,
    ChallengeOverviewModule,
    ChallengeOrganizersModule,
    ChallengeSponsorsModule,
    ChallengeStargazersModule,
    ChallengeRoutingModule,
    ChallengeStatsModule,
    ProgressBarModule,
    UiModule,
  ],
  exports: [ChallengeComponent],
})
export class ChallengeModule {}
