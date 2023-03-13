import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OrgProfileComponent } from './org-profile.component';

const routes: Routes = [
  { path: '', redirectTo: '/org', pathMatch: 'full' },
  {
    path: ':slug',
    component: OrgProfileComponent,
    children: [{ path: '**', component: OrgProfileComponent }],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class OrgProfileRoutingModule {}
