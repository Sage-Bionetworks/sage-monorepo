import { NavigationLink } from '@sagebionetworks/explorers/models';

export const headerLinks: NavigationLink[] = [
  {
    label: 'HeaderLink1',
    routerLink: ['/header-link-1'],
    activeOptions: { exact: true },
  },
  {
    label: 'HeaderLink2',
    routerLink: ['/header-link-2'],
  },
];

export const footerLinks: NavigationLink[] = [
  {
    label: 'FooterLinkInternal1',
    routerLink: ['/footer-link-internal-1'],
  },
  {
    label: 'FooterLinkExternal',
    url: 'https://sagebionetworks.org',
    target: '_blank',
  },
  {
    label: 'FooterLinkInternal2',
    routerLink: ['/footer-link-internal-2'],
  },
];
