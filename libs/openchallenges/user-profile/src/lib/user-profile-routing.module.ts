import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserProfileComponent } from './user-profile.component';
// import { UserProfileResolver } from './user-profile.resolver';

export const routes: Routes = [
  // {
  //   path: '',
  //   component: UserProfileComponent,
  //   resolve: { userProfile: UserProfileResolver },
  // },
  {
    path: '',
    component: UserProfileComponent,
    children: [{ path: '**', component: UserProfileComponent }],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserProfileRoutingModule {}
