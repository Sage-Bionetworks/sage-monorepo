import { Routes } from '@angular/router';

export const appRoutes: Routes = [
  { path: '', redirectTo: 'not-found', pathMatch: 'full' },
  {
    path: 'not-found',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/not-found-tailwind').then((routes) => routes.routes),
  },
  {
    path: '**',
    redirectTo: '/not-found',
  },
];
