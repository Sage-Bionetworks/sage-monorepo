import { KEEP_COMPARISON_TOOL_STATE } from '@sagebionetworks/explorers/constants';
import { NavigationLink } from '@sagebionetworks/explorers/models';
import {
  DIFFERENTIAL_EXPRESSION_CATEGORIES,
  HELP_URL,
  ROUTE_PATHS,
} from '@sagebionetworks/model-ad/config';

export const headerLinks: NavigationLink[] = [
  {
    label: 'Home',
    routerLink: [ROUTE_PATHS.HOME],
    activeOptions: { exact: true },
  },
  {
    label: 'Model Overview',
    children: [
      {
        label: 'Mouse Models',
        routerLink: [ROUTE_PATHS.MOUSE_MODEL_OVERVIEW],
        state: KEEP_COMPARISON_TOOL_STATE,
      },
      {
        label: 'Marmoset Models',
        routerLink: [ROUTE_PATHS.MARMOSET_MODEL_OVERVIEW],
        state: KEEP_COMPARISON_TOOL_STATE,
      },
    ],
  },
  {
    label: 'Differential Expression',
    children: [
      {
        label: 'Mouse Models',
        isSubheader: true,
        children: [
          {
            label: 'RNA - Differential Expression',
            routerLink: [ROUTE_PATHS.DIFFERENTIAL_EXPRESSION],
            queryParams: { categories: DIFFERENTIAL_EXPRESSION_CATEGORIES.RNA },
            state: KEEP_COMPARISON_TOOL_STATE,
          },
          {
            label: 'Protein - Differential Expression',
            routerLink: [ROUTE_PATHS.DIFFERENTIAL_EXPRESSION],
            queryParams: { categories: DIFFERENTIAL_EXPRESSION_CATEGORIES.PROTEIN },
            state: KEEP_COMPARISON_TOOL_STATE,
          },
        ],
      },
    ],
  },
  {
    label: 'Disease Correlation',
    routerLink: [ROUTE_PATHS.DISEASE_CORRELATION],
    state: KEEP_COMPARISON_TOOL_STATE,
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
