import { ActivatedRouteSnapshot, Route } from '@angular/router';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { ROUTE_PATHS, SUPPORT_EMAIL } from '@sagebionetworks/model-ad/config';

export const routes: Route[] = [
  {
    path: ROUTE_PATHS.HOME,
    loadChildren: () => import('@sagebionetworks/model-ad/home').then((routes) => routes.routes),
    data: {
      title: 'Model AD Explorer',
      description:
        "Discover next-generation mouse models of Alzheimer's Disease from the MODEL-AD Consortium.",
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
    path: ROUTE_PATHS.MODEL_OVERVIEW,
    loadChildren: () =>
      import('@sagebionetworks/model-ad/model-overview-comparison-tool').then(
        (routes) => routes.routes,
      ),
    data: {
      title: "Model Overview | Overview of mouse models of Alzheimer's Disease",
      description: "Explore next-generation mouse models of Alzheimer's Disease.",
    },
  },
  {
    path: ROUTE_PATHS.GENE_EXPRESSION,
    loadChildren: () =>
      import('@sagebionetworks/model-ad/gene-expression-comparison-tool').then(
        (routes) => routes.routes,
      ),
    data: {
      title: 'Gene Expression | Visual comparison tool for differential expression results',
      description:
        "Explore high-dimensional omics data for next-generation mouse models of Alzheimer's Disease.",
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
    path: `${ROUTE_PATHS.MODELS}/:model`,
    loadChildren: () =>
      import('@sagebionetworks/model-ad/model-details').then((routes) => routes.routes),
    data: {
      title: (route: ActivatedRouteSnapshot) => `Model Details | ${route.params['model']} AD model`,
      description: (route: ActivatedRouteSnapshot) =>
        `Explore information and results for the ${route.params['model']} Alzheimer's Disease mouse model.`,
    },
  },
  {
    path: `${ROUTE_PATHS.MODELS}/:model/:tab`,
    loadChildren: () =>
      import('@sagebionetworks/model-ad/model-details').then((routes) => routes.routes),
  },
  {
    path: `${ROUTE_PATHS.MODELS}/:model/:tab/:subtab`,
    loadChildren: () =>
      import('@sagebionetworks/model-ad/model-details').then((routes) => routes.routes),
  },
  // ensure that all models match a route, so the custom url serializer can encode special characters
  {
    path: `${ROUTE_PATHS.MODELS}/**`,
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
      description:
        "Discover next-generation mouse models of Alzheimer's Disease from the MODEL-AD Consortium.",
      supportEmail: SUPPORT_EMAIL,
    },
  },
  {
    path: '**',
    redirectTo: `/${ROUTE_PATHS.NOT_FOUND}`,
  },
];
