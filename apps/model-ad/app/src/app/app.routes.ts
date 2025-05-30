import { Route } from '@angular/router';
import { SUPPORT_EMAIL } from '@sagebionetworks/model-ad/util';

export const routes: Route[] = [
  {
    path: '',
    loadChildren: () => import('@sagebionetworks/model-ad/home').then((routes) => routes.routes),
  },
  {
    path: 'model-overview',
    loadChildren: () =>
      import('@sagebionetworks/model-ad/model-overview-comparison-tool').then(
        (routes) => routes.routes,
      ),
  },
  {
    path: 'gene-expression',
    loadChildren: () =>
      import('@sagebionetworks/model-ad/gene-expression-comparison-tool').then(
        (routes) => routes.routes,
      ),
  },
  {
    path: 'disease-correlation',
    loadChildren: () =>
      import('@sagebionetworks/model-ad/disease-correlation-comparison-tool').then(
        (routes) => routes.routes,
      ),
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
