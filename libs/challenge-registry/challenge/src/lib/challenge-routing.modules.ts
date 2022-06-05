import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ChallengeComponent } from './challenge.component';
import { ChallengeOverviewComponent } from './challenge-overview/challenge-overview.component';
import { ChallengeSettingsComponent } from './challenge-settings/challenge-settings.component';
import { ChallengeStargazersComponent } from './challenge-stargazers/challenge-stargazers.component';
import { ChallengeSponsorsComponent } from './challenge-sponsors/challenge-sponsors.component';

const routes: Routes = [
  {
    path: '',
    component: ChallengeComponent,
    children: [
      { path: '', component: ChallengeOverviewComponent },
      { path: 'sponsors', component: ChallengeSponsorsComponent },
      { path: 'stargazers', component: ChallengeStargazersComponent },
      { path: 'settings', component: ChallengeSettingsComponent },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ChallengeRoutingModule {}
