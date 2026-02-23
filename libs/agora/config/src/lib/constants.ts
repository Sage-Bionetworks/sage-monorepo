import { LoadingIconColors } from '@sagebionetworks/explorers/models';

export const HELP_URL = 'https://help.adknowledgeportal.org/apd/Agora-Resources.2646671361.html';

export const SUPPORT_EMAIL = 'agora@sagebionetworks.org';

export const DEFAULT_HERO_BACKGROUND_IMAGE_PATH = 'agora-assets/images/hero-background.svg';

export const ROUTE_PATHS = {
  HOME: '',
  GENE_COMPARISON: 'genes/comparison',
  NOMINATED_DRUGS: 'comparison/drugs',
  NOMINATED_TARGETS: 'comparison/targets',
  TEAMS: 'teams',
  NEWS: 'news',
  ABOUT: 'about',
  TERMS_OF_SERVICE: 'terms-of-service',
  NOT_FOUND: 'not-found',
  ERROR: 'error',
  DETAILS: 'genes',
} as const;

// Use color-secondary to match explorers-loading-icon
export const AGORA_LOADING_ICON_COLORS: LoadingIconColors = {
  colorInnermost: '#8b8ad1',
  colorCentral: '#8b8ad1',
  colorOutermost: '#8b8ad1',
};
