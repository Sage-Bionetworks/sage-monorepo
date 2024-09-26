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
    path: 'not-found',
    loadChildren: () => import('@sagebionetworks/agora/not-found').then((routes) => routes.routes),
    data: {
      title: 'Page not found',
      description: '',
    },
  },
  {
    path: 'teams',
    loadChildren: () => import('@sagebionetworks/agora/teams').then((routes) => routes.teamsRoutes),
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
