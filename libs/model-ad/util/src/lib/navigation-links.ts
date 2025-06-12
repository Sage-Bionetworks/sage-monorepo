import { NavigationLink } from '@sagebionetworks/explorers/models';
import { HELP_URL } from '@sagebionetworks/model-ad/util';

export const headerLinks: NavigationLink[] = [
  {
    label: 'Home',
    routerLink: [''],
    activeOptions: { exact: true },
  },
  {
    label: 'Model Overview',
    routerLink: ['comparison/model'],
  },
  {
    label: 'Gene Expression',
    routerLink: ['comparison/expression'],
  },
  {
    label: 'Disease Correlation',
    routerLink: ['comparison/correlation'],
  },
];

export const footerLinks: NavigationLink[] = [
  {
    label: 'About',
    routerLink: ['about'],
  },
  {
    label: 'Help',
    url: HELP_URL,
    target: '_blank',
  },
  {
    label: 'News',
    routerLink: ['news'],
  },
  {
    label: 'Terms of Service',
    routerLink: ['terms-of-service'],
  },
];
