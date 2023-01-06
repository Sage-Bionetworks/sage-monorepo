import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ChallengeComponent } from './challenge.component';
import { ChallengeDetailsModule } from './challenge-details/challenge-details.module';
import { ChallengeOverviewModule } from './challenge-overview/challenge-overview.module';
import { ChallengeOrganizersModule } from './challenge-organizers/challenge-organizers.module';
import { ChallengeSponsorsModule } from './challenge-sponsors/challenge-sponsors.module';
import { ChallengeStargazersModule } from './challenge-stargazers/challenge-stargazers.module';
import { ChallengeStatsModule } from './challenge-stats/challenge-stats.module';

const routes: Routes = [
  {
    path: ':challengeName',
    component: ChallengeComponent,
    children: [
      { path: ':challengeName', component: ChallengeOverviewModule },
      { path: 'details', component: ChallengeDetailsModule },
      { path: 'organizers', component: ChallengeOrganizersModule },
      { path: 'sponsors', component: ChallengeSponsorsModule },
      { path: 'stargazers', component: ChallengeStargazersModule },
      { path: 'stats', component: ChallengeStatsModule },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ChallengeRoutingModule {}
