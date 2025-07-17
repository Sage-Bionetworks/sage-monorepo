import { Routes } from '@angular/router';

export const appRoutes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  {
    path: 'home',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/home').then((routes) => routes.routes),
  },
  {
    path: 'about',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/about').then((routes) => routes.routes),
  },
  {
    path: 'challenge',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/challenge-search').then((routes) => routes.routes),
  },
  {
    path: 'org',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/org-search').then((routes) => routes.routes),
  },
  {
    path: 'team',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/team').then((routes) => routes.routes),
  },
  {
    path: 'not-found',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/not-found').then((routes) => routes.routes),
  },
  {
    path: 'org/:orgLogin',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/org-profile').then((routes) => routes.routes),
  },
  {
    path: 'challenge/:challengeId',
    loadChildren: () =>
      import('@sagebionetworks/openchallenges/challenge').then((routes) => routes.routes),
  },
  {
    path: '**',
    redirectTo: '/not-found',
  },
];
