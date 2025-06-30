import { NavigationLink } from '@sagebionetworks/explorers/models';
import { HELP_URL, ROUTE_PATHS } from '@sagebionetworks/model-ad/config';

export const headerLinks: NavigationLink[] = [
  {
    label: 'Home',
    routerLink: [ROUTE_PATHS.HOME],
    activeOptions: { exact: true },
  },
  {
    label: 'Model Overview',
    routerLink: [ROUTE_PATHS.MODEL_OVERVIEW],
  },
  {
    label: 'Gene Expression',
    routerLink: [ROUTE_PATHS.GENE_EXPRESSION],
  },
  {
    label: 'Disease Correlation',
    routerLink: [ROUTE_PATHS.DISEASE_CORRELATION],
  },
];

export const footerLinks: NavigationLink[] = [
  {
    label: 'About',
    routerLink: [ROUTE_PATHS.ABOUT],
  },
  {
    label: 'Help',
    url: HELP_URL,
    target: '_blank',
  },
  {
    label: 'News',
    routerLink: [ROUTE_PATHS.NEWS],
  },
  {
    label: 'Terms of Service',
    routerLink: [ROUTE_PATHS.TERMS_OF_SERVICE],
  },
];
