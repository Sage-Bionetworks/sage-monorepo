import { Route } from '@angular/router';
import { authGuard } from '@sagebionetworks/bixarena/services';

export const appRoutes: Route[] = [
  {
    path: '',
    data: {
      title: 'BioArena',
      description:
        'Find the best AI for your biomedical research. BioArena crowdsources the benchmarking of AI models to unlock the next breakthrough in biomedicine.',
    },
    loadChildren: () => import('@sagebionetworks/bixarena/home').then((r) => r.routes),
  },
  {
    path: 'battle',
    canActivate: [authGuard],
    data: { title: 'Battle | BioArena' },
    loadChildren: () => import('@sagebionetworks/bixarena/battle').then((r) => r.routes),
  },
  {
    path: 'leaderboard',
    data: {
      title: 'Leaderboard | BioArena',
      description:
        'Explore AI model rankings for biomedical research. BioArena ranks models by crowdsourced community votes across biomedical topics.',
    },
    loadChildren: () => import('@sagebionetworks/bixarena/leaderboard').then((r) => r.routes),
  },
  // {
  //   path: 'arena',
  //   loadChildren: () => import('@sagebionetworks/bixarena/arena').then((r) => r.routes),
  // },
  { path: '**', redirectTo: '' },
];
