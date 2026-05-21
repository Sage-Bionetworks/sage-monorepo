import { Route } from '@angular/router';
import { StatCardData, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import {
  DEFAULT_HERO_BACKGROUND_IMAGE_PATH,
  ROUTE_PATHS,
  SUPPORT_EMAIL,
} from '@sagebionetworks/qtl/config';

const HOME_STAT_CARDS: StatCardData[] = [
  {
    iconPath: 'explorers-assets/icons/people.svg',
    iconAltText: 'people',
    iconColor: 'white',
    iconBackgroundColor: '#2C5182',
    header: '4000',
    subHeader: 'Brain Donors',
    link: `/${ROUTE_PATHS.NOT_FOUND}`,
  },
  {
    iconPath: 'explorers-assets/icons/cell.svg',
    iconAltText: 'cell',
    iconColor: 'white',
    iconBackgroundColor: '#388C95',
    header: '81',
    subHeader: 'Cell Types',
    link: `/${ROUTE_PATHS.NOT_FOUND}`,
  },
  {
    iconPath: 'explorers-assets/icons/gene-search.svg',
    iconAltText: 'gene search',
    header: '3',
    subHeader: 'Ancestries',
    iconBackgroundColor: '#A5C7F3',
    iconColor: 'white',
    link: `/${ROUTE_PATHS.NOT_FOUND}`,
  },
  {
    iconPath: 'explorers-assets/icons/erythrocytes.svg',
    iconAltText: 'erythrocytes',
    iconColor: 'white',
    iconBackgroundColor: '#6E76AE',
    header: '2 million',
    subHeader: 'Single Cells',
    link: `/${ROUTE_PATHS.NOT_FOUND}`,
  },
  {
    iconPath: 'explorers-assets/icons/eqtl.svg',
    iconAltText: 'eQTL',
    iconColor: 'white',
    iconBackgroundColor: '#D7AE0C',
    header: '252 million',
    subHeader: 'eQTLs',
    link: `/${ROUTE_PATHS.ABOUT}`,
  },
];

export const routes: Route[] = [
  {
    path: ROUTE_PATHS.HOME,
    loadChildren: () => import('@sagebionetworks/qtl/home').then((routes) => routes.routes),
    data: {
      title: 'xQTL Explorer',
      description: 'home page',
      statCards: HOME_STAT_CARDS,
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
      heroBackgroundImagePath: DEFAULT_HERO_BACKGROUND_IMAGE_PATH,
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
      heroBackgroundImagePath: DEFAULT_HERO_BACKGROUND_IMAGE_PATH,
      wikiParams: {
        wikiId: '640478',
        ownerId: 'syn74592415',
      } as SynapseWikiParams,
    },
  },
  {
    path: ROUTE_PATHS.EQTL,
    loadChildren: () =>
      import('@sagebionetworks/qtl/eqtl-comparison-tool').then((routes) => routes.routes),
    data: {
      // TODO: update based on values in QTL-34
      title: 'eQTL Comparison',
      description: '',
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
      heroBackgroundImagePath: DEFAULT_HERO_BACKGROUND_IMAGE_PATH,
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
      backgroundImagePath: DEFAULT_HERO_BACKGROUND_IMAGE_PATH,
    },
  },
  {
    path: ROUTE_PATHS.ERROR,
    loadChildren: () =>
      import('@sagebionetworks/explorers/shared').then((routes) => routes.errorPageRoute),
    data: {
      title: 'xQTL Explorer | Error',
      description: 'Error Page',
      supportEmail: SUPPORT_EMAIL,
      backgroundImagePath: DEFAULT_HERO_BACKGROUND_IMAGE_PATH,
    },
  },
  {
    path: '**',
    redirectTo: `/${ROUTE_PATHS.NOT_FOUND}`,
  },
];
