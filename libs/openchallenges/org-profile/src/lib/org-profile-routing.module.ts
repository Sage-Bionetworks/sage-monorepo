import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OrgProfileComponent } from './org-profile.component';
import { OrgProfileOverviewComponent } from './org-profile-overview/org-profile-overview.component';
import { OrgProfileChallengesComponent } from './org-profile-challenges/org-profile-challenges.component';
import { OrgProfileMembersComponent } from './org-profile-members/org-profile-members.component';
import { OrgProfileStatsComponent } from './org-profile-stats/org-profile-stats.component';

const routes: Routes = [
  {
    path: ':orgLogin',
    component: OrgProfileComponent,
    children: [
      { path: ':orgLogin', component: OrgProfileOverviewComponent },
      { path: 'challenges', component: OrgProfileChallengesComponent },
      { path: 'members', component: OrgProfileMembersComponent },
      { path: 'stats', component: OrgProfileStatsComponent },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class OrgProfileRoutingModule {}
