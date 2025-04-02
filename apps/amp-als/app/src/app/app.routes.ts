import { Route } from '@angular/router';

export const routes: Route[] = [
  {
    path: 'not-found',
    loadChildren: () =>
      import('@sagebionetworks/amp-als/not-found').then((routes) => routes.routes),
    data: {
      title: 'Page not found',
      description: '',
    },
  },
  {
    path: '**',
    redirectTo: '/not-found',
  },
];
