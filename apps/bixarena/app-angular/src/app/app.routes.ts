import { Route } from '@angular/router';
import { authGuard } from '@sagebionetworks/bixarena/services';

export const appRoutes: Route[] = [
  {
    path: '',
    loadChildren: () => import('@sagebionetworks/bixarena/home').then((r) => r.routes),
  },
  {
    path: 'battle',
    canActivate: [authGuard],
    loadChildren: () => import('@sagebionetworks/bixarena/battle').then((r) => r.routes),
  },
  {
    path: 'leaderboard',
    loadChildren: () => import('@sagebionetworks/bixarena/leaderboard').then((r) => r.routes),
  },
  // {
  //   path: 'arena',
  //   loadChildren: () => import('@sagebionetworks/bixarena/arena').then((r) => r.routes),
  // },
  { path: '**', redirectTo: '' },
];
