import { Route } from '@angular/router';

export const routes: Route[] = [
  {
    path: 'not-found',
    loadChildren: () =>
      import('@sagebionetworks/model-ad/not-found').then(
        (routes) => routes.routes,
      ),
  },
  {
    path: '**',
    redirectTo: '/not-found',
  },
];
