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
    routerLink: ['model-overview'],
  },
  {
    label: 'Gene Expression',
    routerLink: ['gene-expression'],
  },
  {
    label: 'Disease Correlation',
    routerLink: ['disease-correlation'],
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
