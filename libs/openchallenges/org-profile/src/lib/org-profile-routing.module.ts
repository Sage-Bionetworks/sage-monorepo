import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OrgProfileComponent } from './org-profile.component';
import { OrgProfileOverviewComponent } from './org-profile-overview/org-profile-overview.component';

const routes: Routes = [
  {
    path: ':orgLogin',
    component: OrgProfileComponent,
    children: [{ path: ':orgLogin', component: OrgProfileOverviewComponent }],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class OrgProfileRoutingModule {}
