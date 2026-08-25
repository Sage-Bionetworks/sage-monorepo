import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Route, Router } from '@angular/router';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { ROUTE_PATHS, SUPPORT_EMAIL } from '@sagebionetworks/model-ad/config';
import { resolveModelOrganism } from '@sagebionetworks/model-ad/util';
import { capitalizeFirstLetter } from '@sagebionetworks/shared/util';
import { modelOrganismGuard } from './model-organism.guard';

const DEFAULT_META_DESCRIPTION =
  "Discover next-generation models of Alzheimer's Disease developed by the MODEL-AD consortium and MARMO-AD.";

const modelDetailsData = {
  title: (route: ActivatedRouteSnapshot) => {
    const organism = resolveModelOrganism(route.queryParams['modelOrganism']);
    return `${capitalizeFirstLetter(organism)} Model Details | ${route.params['name']} AD model`;
  },
  description: (route: ActivatedRouteSnapshot) => {
    const organism = resolveModelOrganism(route.queryParams['modelOrganism']);
    return `Explore information and results for the ${route.params['name']} Alzheimer's Disease ${organism} model.`;
  },
};

export const routes: Route[] = [
  {
    path: ROUTE_PATHS.HOME,
    loadChildren: () => import('@sagebionetworks/model-ad/home').then((routes) => routes.routes),
    data: {
      title: 'Model AD Explorer',
      description: DEFAULT_META_DESCRIPTION,
    },
  },
  {
    path: ROUTE_PATHS.ABOUT,
    loadChildren: () =>
      import('@sagebionetworks/explorers/shared').then((routes) => routes.wikiHeroRoute),
    data: {
      title: 'About the Model AD Explorer',
      description:
        'The Model AD Explorer is funded by the National Institute on Aging, and is developed and maintained by Sage Bionetworks.',
      heroTitle: 'About',
      wikiParams: {
        wikiId: '631750',
        ownerId: 'syn66271427',
      } as SynapseWikiParams,
    },
  },
  {
    path: ROUTE_PATHS.NEWS,
    loadChildren: () =>
      import('@sagebionetworks/explorers/shared').then((routes) => routes.wikiHeroRoute),
    data: {
      title: 'News | Model AD Explorer Releases',
      description:
        "See what's new in the Model AD Explorer, from new features to our latest data updates.",
      heroTitle: 'News',
      wikiParams: {
        wikiId: '631751',
        ownerId: 'syn66271427',
      } as SynapseWikiParams,
    },
  },
  {
    path: ROUTE_PATHS.MARMOSET_MODEL_OVERVIEW,
    loadChildren: () =>
      import('@sagebionetworks/model-ad/marmoset-model-overview-comparison-tool').then(
        (routes) => routes.routes,
      ),
    data: {
      title: "Marmoset Model Overview | Overview of marmoset models of Alzheimer's Disease",
      description:
        "Explore emerging marmoset models of Alzheimer's Disease developed by the MARMO-AD consortium.",
    },
  },
  {
    path: ROUTE_PATHS.MOUSE_MODEL_OVERVIEW,
    loadChildren: () =>
      import('@sagebionetworks/model-ad/mouse-model-overview-comparison-tool').then(
        (routes) => routes.routes,
      ),
    data: {
      title: "Mouse Model Overview | Overview of mouse models of Alzheimer's Disease",
      description: "Explore next-generation mouse models of Alzheimer's Disease.",
    },
  },
  {
    // Legacy path for the mouse model overview, when mouse was the only model organism
    // This route is kept to support legacy links, and will redirect to the new mouse model overview path
    path: 'comparison/model',
    pathMatch: 'full',
    // A string redirectTo would drop the original query params, silently stripping the filters off
    // shared comparison tool links, so build the target UrlTree instead.
    redirectTo: ({ queryParams, fragment }) =>
      inject(Router).createUrlTree([ROUTE_PATHS.MOUSE_MODEL_OVERVIEW], {
        queryParams,
        fragment: fragment ?? undefined,
      }),
  },
  {
    path: ROUTE_PATHS.DIFFERENTIAL_EXPRESSION,
    loadChildren: () =>
      import('@sagebionetworks/model-ad/differential-expression-comparison-tool').then(
        (routes) => routes.routes,
      ),
    data: {
      title: 'Differential Expression | Visual comparison tool for differential expression results',
      description:
        "Explore high-dimensional omics data for next-generation mouse models of Alzheimer's Disease.",
    },
  },
  {
    path: `${ROUTE_PATHS.GENES}/:ensemblGeneId`,
    loadChildren: () =>
      import('@sagebionetworks/model-ad/gene-details').then((routes) => routes.routes),
    data: {
      title: 'Individual RNA Expression',
      description:
        "View individual-level RNA expression results for next-generation mouse models of Alzheimer's Disease.",
    },
  },
  {
    path: `${ROUTE_PATHS.PROTEINS}/:uniqueId`,
    loadChildren: () =>
      import('@sagebionetworks/model-ad/protein-details').then((routes) => routes.routes),
    data: {
      title: 'Individual Protein Expression',
      description:
        "View individual-level protein expression results for next-generation mouse models of Alzheimer's Disease.",
    },
  },
  {
    path: ROUTE_PATHS.DISEASE_CORRELATION,
    loadChildren: () =>
      import('@sagebionetworks/model-ad/disease-correlation-comparison-tool').then(
        (routes) => routes.routes,
      ),
    data: {
      title: 'Disease Correlation | Visual comparison tool for correlation results',
      description:
        "Explore whether changes in gene expression in next-generation Alzheimer's Disease mouse models correlate with changes in gene expression in humans with the disease.",
    },
  },
  {
    path: `${ROUTE_PATHS.MODELS}/:name`,
    canActivate: [modelOrganismGuard],
    loadChildren: () =>
      import('@sagebionetworks/model-ad/model-details').then((routes) => routes.routes),
    data: modelDetailsData,
  },
  {
    path: `${ROUTE_PATHS.MODELS}/:name/:tab`,
    canActivate: [modelOrganismGuard],
    loadChildren: () =>
      import('@sagebionetworks/model-ad/model-details').then((routes) => routes.routes),
    data: modelDetailsData,
  },
  {
    path: `${ROUTE_PATHS.MODELS}/:name/:tab/:subtab`,
    canActivate: [modelOrganismGuard],
    loadChildren: () =>
      import('@sagebionetworks/model-ad/model-details').then((routes) => routes.routes),
    data: modelDetailsData,
  },
  // ensure that all models match a route, so the custom url serializer can encode special characters
  {
    path: `${ROUTE_PATHS.MODELS}/**`,
    canActivate: [modelOrganismGuard],
    loadChildren: () =>
      import('@sagebionetworks/model-ad/model-details').then((routes) => routes.routes),
  },
  {
    path: ROUTE_PATHS.TERMS_OF_SERVICE,
    loadChildren: () =>
      import('@sagebionetworks/explorers/shared').then((routes) => routes.termsOfServiceRoute),
    data: {
      title: 'Model AD Explorer | Terms of Service',
      description:
        'The Model AD Explorer is powered by Synapse, a platform for supporting scientific collaborations centered around shared biomedical data sets. Our goal is to make biomedical research more transparent, more reproducible, and more accessible to a broader audience of scientists.',
    },
  },
  {
    path: ROUTE_PATHS.NOT_FOUND,
    loadChildren: () =>
      import('@sagebionetworks/explorers/shared').then((routes) => routes.notFoundRoute),
    data: {
      title: 'Model AD Explorer | Page Not Found',
      description: DEFAULT_META_DESCRIPTION,
      supportEmail: SUPPORT_EMAIL,
    },
  },
  {
    path: ROUTE_PATHS.ERROR,
    loadChildren: () =>
      import('@sagebionetworks/explorers/shared').then((routes) => routes.errorPageRoute),
    data: {
      title: 'Model AD Explorer | Error',
      description: 'Error Page',
      supportEmail: SUPPORT_EMAIL,
    },
  },
  {
    path: 'sentry-test',
    loadComponent: () =>
      import('./sentry-test/sentry-test.component').then((m) => m.SentryTestComponent),
    data: {
      title: 'Model AD Explorer | Sentry Test',
      description: 'Diagnostic page for testing Sentry integration.',
    },
  },
  {
    path: '**',
    redirectTo: `/${ROUTE_PATHS.NOT_FOUND}`,
  },
];
