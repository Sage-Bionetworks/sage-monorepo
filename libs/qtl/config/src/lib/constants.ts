import { LoadingIconColors } from '@sagebionetworks/explorers/models';

export const HELP_URL =
  'https://help.adknowledgeportal.org/apd/xQTL-Explorer-Resources.4669014109.html';

export const SUPPORT_EMAIL = 'xqtlexplorer@sagebionetworks.org';

// TODO: update to QTL-specific colors (QTL-69)
export const QTL_LOADING_ICON_COLORS: LoadingIconColors = {
  colorInnermost: '#00C9BA',
  colorCentral: '#6F51C7',
  colorOutermost: '#00737C',
};

export const ROUTE_PATHS = {
  HOME: '',
  ABOUT: 'about',
  NEWS: 'news',
  TERMS_OF_SERVICE: 'terms-of-service',
  NOT_FOUND: 'not-found',
  ERROR: 'error',
} as const;
