import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { WebUiModule } from '@challenge-registry/web/ui';
import { OrgProfileComponent } from './org-profile.component';
import { OrgProfileHeaderModule } from './org-profile-header/org-profile-header.module';
import { OrgProfileOverviewModule } from './org-profile-overview/org-profile-overview.module';
import { OrgProfileOverviewComponent } from './org-profile-overview/org-profile-overview.component';
import { OrgProfileChallengesComponent } from './org-profile-challenges/org-profile-challenges.component';

const routes: Routes = [
  {
    path: '',
    component: OrgProfileComponent,
    children: [
      { path: '', component: OrgProfileOverviewComponent },
      { path: 'challenges', component: OrgProfileChallengesComponent },
    ],
  },
];

@NgModule({
  declarations: [OrgProfileComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MatTabsModule,
    WebUiModule,
    OrgProfileHeaderModule,
    OrgProfileOverviewModule,
  ],
  exports: [OrgProfileComponent],
})
export class OrgProfileModule {}
