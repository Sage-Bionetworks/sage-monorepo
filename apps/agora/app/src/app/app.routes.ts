import { Route } from '@angular/router';

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
    path: 'about',
    loadChildren: () => import('@sagebionetworks/agora/about').then((routes) => routes.routes),
    data: {
      title: 'About Agora',
      description:
        'Agora is funded by the National Institute on Aging, and is developed and maintained by Sage Bionetworks.',
    },
  },
  {
    path: 'news',
    loadChildren: () => import('@sagebionetworks/agora/news').then((routes) => routes.routes),
    data: {
      title: 'News | Agora Releases',
      description: "See what's new in Agora, from new features to our latest data updates.",
    },
  },
  {
    path: 'genes/comparison',
    loadChildren: () =>
      import('@sagebionetworks/agora/gene-comparison-tool').then((routes) => routes.routes),
    data: {
      title: 'Gene Comparison | Visual comparison tool for AD genes',
      description:
        'Explore high-dimensional omics data with our visual gene comparison tool, then build, share, and download visualizations for your own custom gene lists.',
    },
  },
  {
    path: 'genes/nominated-targets',
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
    path: 'genes/:id/similar',
    loadChildren: () =>
      import('@sagebionetworks/agora/gene-similar').then((routes) => routes.routes),
  },
  {
    path: 'genes/:id/:tab/:subtab',
    loadChildren: () =>
      import('@sagebionetworks/agora/gene-details').then((routes) => routes.routes),
  },
  {
    path: 'genes/:id/:tab',
    loadChildren: () =>
      import('@sagebionetworks/agora/gene-details').then((routes) => routes.routes),
  },
  {
    path: 'genes/:id',
    loadChildren: () =>
      import('@sagebionetworks/agora/gene-details').then((routes) => routes.routes),
    data: {
      title: 'Agora | Gene Details',
      description: "View information and evidence about genes in Alzheimer's disease.",
    },
  },
  {
    path: 'not-found',
    loadChildren: () => import('@sagebionetworks/agora/not-found').then((routes) => routes.routes),
    data: {
      title: 'Page not found',
      description: '',
    },
  },
  {
    path: 'teams',
    loadChildren: () => import('@sagebionetworks/agora/teams').then((routes) => routes.routes),
    data: {
      title: 'Contributing Teams',
      description:
        'Find information about the NIA-funded and community research teams that have contributed evidence to Agora.',
    },
  },
  {
    path: '**',
    redirectTo: '/not-found',
  },
];
