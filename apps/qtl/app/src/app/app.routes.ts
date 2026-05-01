import { Route } from '@angular/router';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { ROUTE_PATHS, SUPPORT_EMAIL } from '@sagebionetworks/qtl/config';

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
  {
    path: ROUTE_PATHS.TERMS_OF_SERVICE,
    loadChildren: () =>
      import('@sagebionetworks/explorers/shared').then((routes) => routes.termsOfServiceRoute),
    data: {
      title: 'xQTL Explorer | Terms of Service',
      description:
        'The xQTL Explorer is powered by Synapse, a platform for supporting scientific collaborations centered around shared biomedical data sets. Our goal is to make biomedical research more transparent, more reproducible, and more accessible to a broader audience of scientists.',
    },
  },
  {
    path: ROUTE_PATHS.NOT_FOUND,
    loadChildren: () =>
      import('@sagebionetworks/explorers/shared').then((routes) => routes.notFoundRoute),
    data: {
      title: 'xQTL Explorer | Page Not Found',
      description: 'The link you followed may be broken, or the page may have been removed.',
      robots: 'noindex, follow',
      supportEmail: SUPPORT_EMAIL,
    },
  },
  {
    path: '**',
    redirectTo: `/${ROUTE_PATHS.NOT_FOUND}`,
  },
];
