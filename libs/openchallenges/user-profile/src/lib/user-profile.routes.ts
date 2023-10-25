import { Routes } from '@angular/router';
import { UserProfileComponent } from './user-profile.component';

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
