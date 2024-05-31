import { Route } from '@angular/router';

export const appRoutes: Route[] = [
  {
    path: 'not-found',
    loadChildren: () =>
      import('@sagebionetworks/model-ad/not-found').then(
        (routes) => routes.routes
      ),
  },
  {
    path: '**',
    redirectTo: '/not-found',
  },
];
