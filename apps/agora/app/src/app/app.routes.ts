import { Route } from '@angular/router';

export const routes: Route[] = [
  {
    path: 'about',
    loadChildren: () => import('@sagebionetworks/agora/about').then((routes) => routes.routes),
  },
  {
    path: 'not-found',
    loadChildren: () => import('@sagebionetworks/agora/not-found').then((routes) => routes.routes),
  },
  {
    path: 'teams',
    loadChildren: () => import('@sagebionetworks/agora/teams').then((routes) => routes.teamsRoutes),
  },
  {
    path: '**',
    redirectTo: '/not-found',
  },
];
