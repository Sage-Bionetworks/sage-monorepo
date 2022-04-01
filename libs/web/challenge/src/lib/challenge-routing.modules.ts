import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ChallengeOverviewComponent } from './challenge-overview/challenge-overview.component';
import { ChallengeStargazersComponent } from './challenge-stargazers/challenge-stargazers.component';
import { ChallengeComponent } from './challenge.component';

const routes: Routes = [
  {
    path: '',
    component: ChallengeComponent,
    children: [
      { path: '', component: ChallengeOverviewComponent },
      { path: 'stargazers', component: ChallengeStargazersComponent },
      // { path: 'challenges', component: OrgProfileChallengesComponent },
      // { path: 'people', component: OrgProfilePeopleComponent },
      // { path: 'settings', component: OrgProfileSettingsComponent },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ChallengeRoutingModule {}
