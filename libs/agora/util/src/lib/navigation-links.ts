import { NavigationLink } from '@sagebionetworks/explorers/models';
import { HELP_URL, ROUTE_PATHS } from '@sagebionetworks/agora/config';

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
