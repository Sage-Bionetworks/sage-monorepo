import { Route } from '@angular/router';
import { ROUTE_PATHS } from '@sagebionetworks/qtl/config';

export const routes: Route[] = [
  {
    path: ROUTE_PATHS.HOME,
    loadChildren: () => import('@sagebionetworks/qtl/home').then((routes) => routes.routes),
    data: {
      title: 'QTL Explorer',
      description: 'home page',
    },
  },
];
