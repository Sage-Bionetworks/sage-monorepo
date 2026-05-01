import { Route } from '@angular/router';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { ROUTE_PATHS } from '@sagebionetworks/qtl/config';

export const routes: Route[] = [
  {
    path: ROUTE_PATHS.HOME,
    loadChildren: () => import('@sagebionetworks/qtl/home').then((routes) => routes.routes),
    data: {
      title: 'xQTL Explorer',
      description: 'home page',
    },
  },
  {
    path: ROUTE_PATHS.ABOUT,
    loadChildren: () =>
      import('@sagebionetworks/explorers/shared').then((routes) => routes.wikiHeroRoute),
    data: {
      title: 'About the xQTL Explorer',
      description:
        'The xQTL Explorer is funded by the National Institute on Aging, and is developed and maintained by Sage Bionetworks.',
      heroTitle: 'About',
      wikiParams: {
        wikiId: '640479',
        ownerId: 'syn74592415',
      } as SynapseWikiParams,
    },
  },
  {
    path: ROUTE_PATHS.NEWS,
    loadChildren: () =>
      import('@sagebionetworks/explorers/shared').then((routes) => routes.wikiHeroRoute),
    data: {
      title: 'News | xQTL Explorer Releases',
      description:
        "See what's new in the xQTL Explorer, from new features to our latest data updates.",
      heroTitle: 'News',
      wikiParams: {
        wikiId: '640478',
        ownerId: 'syn74592415',
      } as SynapseWikiParams,
    },
  },
];
