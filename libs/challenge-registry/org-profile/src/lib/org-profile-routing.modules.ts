import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OrgProfileComponent } from './org-profile.component';
import { OrgProfileOverviewComponent } from './org-profile-overview/org-profile-overview.component';
import { OrgProfileChallengesComponent } from './org-profile-challenges/org-profile-challenges.component';
import { OrgProfilePeopleComponent } from './org-profile-people/org-profile-people.component';
import { OrgProfileSettingsComponent } from './org-profile-settings/org-profile-settings.component';

const routes: Routes = [
  {
    path: '',
    component: OrgProfileComponent,
    children: [
      { path: '', component: OrgProfileOverviewComponent },
      { path: 'challenges', component: OrgProfileChallengesComponent },
      { path: 'people', component: OrgProfilePeopleComponent },
      { path: 'settings', component: OrgProfileSettingsComponent },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class OrgProfileRoutingModule {}
