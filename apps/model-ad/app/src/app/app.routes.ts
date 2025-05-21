import { Route } from '@angular/router';
import { SUPPORT_EMAIL } from '@sagebionetworks/model-ad/util';

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
    data: {
      supportEmail: SUPPORT_EMAIL,
    },
  },
  {
    path: '**',
    redirectTo: '/not-found',
  },
];
