import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatLegacyTabsModule as MatTabsModule } from '@angular/material/legacy-tabs';
import { MatIconModule } from '@angular/material/icon';
import { ChallengeComponent } from './challenge.component';
import { ChallengeOverviewModule } from './challenge-overview/challenge-overview.module';
import { ChallengeOrganizersModule } from './challenge-organizers/challenge-organizers.module';
import { ChallengeSponsorsModule } from './challenge-sponsors/challenge-sponsors.module';
import { ChallengeStargazersModule } from './challenge-stargazers/challenge-stargazers.module';
import { ChallengeRoutingModule } from './challenge-routing.module';
import { ChallengeStatsModule } from './challenge-stats/challenge-stats.module';
import { ProgressBarModule } from 'primeng/progressbar';
import { FooterComponent, UiModule } from '@sagebionetworks/openchallenges/ui';

@NgModule({
  declarations: [ChallengeComponent],
  imports: [
    CommonModule,
    MatTabsModule,
    MatIconModule,
    ChallengeOverviewModule,
    ChallengeOrganizersModule,
    ChallengeSponsorsModule,
    ChallengeStargazersModule,
    ChallengeRoutingModule,
    ChallengeStatsModule,
    ProgressBarModule,
    UiModule,
    FooterComponent,
  ],
  exports: [ChallengeComponent],
})
export class ChallengeModule {}
