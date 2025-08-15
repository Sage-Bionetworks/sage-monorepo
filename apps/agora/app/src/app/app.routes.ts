import { Route } from '@angular/router';
import { ROUTE_PATHS } from '@sagebionetworks/agora/config';

export const routes: Route[] = [
  {
    path: '',
    loadChildren: () => import('@sagebionetworks/agora/home').then((routes) => routes.routes),
    data: {
      title: "Agora | Explore Alzheimer's Disease Genes",
      description:
        "Explore transcriptomic, proteomic, and metabolomic evidence for whether or not genes are associated with Alzheimer's disease using the Agora portal.",
    },
  },
  {
    path: ROUTE_PATHS.ABOUT,
    loadChildren: () => import('@sagebionetworks/agora/about').then((routes) => routes.routes),
    data: {
      title: 'About Agora',
      description:
        'Agora is funded by the National Institute on Aging, and is developed and maintained by Sage Bionetworks.',
    },
  },
  {
    path: ROUTE_PATHS.NEWS,
    loadChildren: () => import('@sagebionetworks/agora/news').then((routes) => routes.routes),
    data: {
      title: 'News | Agora Releases',
      description: "See what's new in Agora, from new features to our latest data updates.",
    },
  },
  {
    path: ROUTE_PATHS.GENE_COMPARISON,
    loadChildren: () =>
      import('@sagebionetworks/agora/gene-comparison-tool').then((routes) => routes.routes),
    data: {
      title: 'Gene Comparison | Visual comparison tool for AD genes',
      description:
        'Explore high-dimensional omics data with our visual gene comparison tool, then build, share, and download visualizations for your own custom gene lists.',
    },
  },
  {
    path: ROUTE_PATHS.NOMINATED_TARGETS,
    loadChildren: () =>
      import('@sagebionetworks/agora/nominated-targets').then(
        (routes) => routes.nominatedTargetsRoute,
      ),
    data: {
      title: 'Nominated Targets | Candidate genes for AD treatment or prevention',
      description:
        'Browse a list of genes that researchers have identified using computational analyses of high-dimensional human genomic, proteomic and metabolomic data.',
    },
  },
  {
    path: 'genes/genes-router:genes-list',
    redirectTo: ROUTE_PATHS.NOMINATED_TARGETS,
    pathMatch: 'full',
  },
  {
    path: 'genes/genes-router:gene-details/:id',
    redirectTo: 'genes/:id',
    pathMatch: 'full',
  },
  {
    path: 'genes/:id/similar',
    loadChildren: () =>
      import('@sagebionetworks/agora/genes').then((routes) => routes.similarRoute),
  },
  {
    path: 'genes/:id/:tab/:subtab',
    loadChildren: () =>
      import('@sagebionetworks/agora/genes').then((routes) => routes.detailsRoute),
  },
  {
    path: 'genes/:id/:tab',
    loadChildren: () =>
      import('@sagebionetworks/agora/genes').then((routes) => routes.detailsRoute),
  },
  {
    path: 'genes/:id',
    loadChildren: () =>
      import('@sagebionetworks/agora/genes').then((routes) => routes.detailsRoute),
    data: {
      title: 'Agora | Gene Details',
      description: "View information and evidence about genes in Alzheimer's disease.",
    },
  },
  {
    path: 'genes',
    redirectTo: '',
    pathMatch: 'full',
  },
  {
    path: 'nomination-form',
    loadChildren: () =>
      import('@sagebionetworks/agora/nominated-targets').then(
        (routes) => routes.nominationFormRoute,
      ),
    data: {
      title: 'Nominate a Target | Suggest a new AD therapeutic target',
      description: 'Nominate a gene as a new candidate for AD treatment or prevention.',
    },
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
    loadChildren: () => import('@sagebionetworks/agora/not-found').then((routes) => routes.routes),
    data: {
      title: 'Page not found',
      description: '',
    },
  },
  {
    path: ROUTE_PATHS.TEAMS,
    loadChildren: () => import('@sagebionetworks/agora/teams').then((routes) => routes.routes),
    data: {
      title: 'Contributing Teams',
      description:
        'Find information about the NIA-funded and community research teams that have contributed evidence to Agora.',
    },
  },
  {
    path: '**',
    redirectTo: ROUTE_PATHS.NOT_FOUND,
  },
];
