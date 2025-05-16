import { Route } from '@angular/router';

export const routes: Route[] = [
  {
    path: '',
    loadChildren: () => import('@sagebionetworks/model-ad/home').then((routes) => routes.routes),
  },
  {
    path: 'terms-of-service',
    loadChildren: () =>
      import('@sagebionetworks/explorers/shared').then((routes) => routes.termsOfServiceRoute),
  },
  {
    path: 'not-found',
    loadChildren: () =>
      import('@sagebionetworks/explorers/shared').then((routes) => routes.notFoundRoute),
  },
  {
    path: '**',
    redirectTo: '/not-found',
  },
];
