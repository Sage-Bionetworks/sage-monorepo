import { Routes } from '@angular/router';
import { ChallengeComponent } from './challenge.component';

export const routes: Routes = [
  {
    path: '',
    component: ChallengeComponent,
    children: [{ path: '**', component: ChallengeComponent }],
  },
];
