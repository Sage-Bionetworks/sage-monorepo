import { Routes } from '@angular/router';
import { OrgProfileComponent } from './org-profile.component';

export const routes: Routes = [
  {
    path: '',
    component: OrgProfileComponent,
    children: [{ path: '**', component: OrgProfileComponent }],
  },
];
