import { NavigationLink } from '@sagebionetworks/explorers/models';

export const headerLinks: NavigationLink[] = [
  {
    label: 'HeaderLink1',
    routerLink: ['/header-link-1'],
    activeOptions: { exact: true },
  },
  {
    label: 'DropdownLink',
    children: [
      { label: 'ChildLink1', routerLink: ['/child-link-1'] },
      { label: 'ChildLink2', routerLink: ['/child-link-2'] },
    ],
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
