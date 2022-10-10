import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OrganizationComponent } from './organization.component';
import { OrganizationResolver } from './organization.resolver';

export const routes: Routes = [
  {
    path: '',
    component: OrganizationComponent,
    resolve: { organization: OrganizationResolver },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  declarations: [],
  providers: [],
  exports: [RouterModule],
})
export class OrganizationRoutingModule {}
