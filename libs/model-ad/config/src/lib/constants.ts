import { LoadingIconColors } from '@sagebionetworks/explorers/models';

export const HELP_URL =
  'https://help.adknowledgeportal.org/apd/Model-AD+Explorer+Resources.4077682781.html';

export const SUPPORT_EMAIL = 'modeladexplorer@sagebionetworks.org';

export const MODEL_AD_LOADING_ICON_COLORS: LoadingIconColors = {
  colorInnermost: '#00C9BA',
  colorCentral: '#6F51C7',
  colorOutermost: '#00737C',
};

export const ROUTE_PATHS = {
  HOME: '',
  ABOUT: 'about',
  NEWS: 'news',
  MODEL_OVERVIEW: 'comparison/model',
  GENE_EXPRESSION: 'comparison/expression',
  DISEASE_CORRELATION: 'comparison/correlation',
  MODELS: 'models',
  TERMS_OF_SERVICE: 'terms-of-service',
  NOT_FOUND: 'not-found',
  ERROR: 'error',
} as const;
