import { HELP_URL, ROUTE_PATHS } from '@sagebionetworks/agora/config';
import { NavigationLink } from '@sagebionetworks/explorers/models';

export const headerLinks: NavigationLink[] = [
  {
    label: 'Home',
    routerLink: [ROUTE_PATHS.HOME],
    activeOptions: { exact: true },
  },
  {
    label: 'Gene Comparison',
    routerLink: [ROUTE_PATHS.GENE_COMPARISON],
  },
  {
    label: 'Nominated Targets',
    routerLink: [ROUTE_PATHS.NOMINATED_TARGETS],
  },
  {
    label: 'Teams',
    routerLink: [ROUTE_PATHS.TEAMS],
  },
  {
    label: 'News',
    routerLink: [ROUTE_PATHS.NEWS],
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
    label: 'Terms of Service',
    routerLink: [ROUTE_PATHS.TERMS_OF_SERVICE],
  },
];
