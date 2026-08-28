import { PointStyle } from '@sagebionetworks/explorers/charts';
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
  MARMOSET_MODEL_OVERVIEW: 'comparison/model/marmoset',
  MOUSE_MODEL_OVERVIEW: 'comparison/model',
  DIFFERENTIAL_EXPRESSION: 'comparison/expression',
  GENES: 'genes',
  DISEASE_CORRELATION: 'comparison/correlation',
  MODELS: 'models',
  TERMS_OF_SERVICE: 'terms-of-service',
  NOT_FOUND: 'not-found',
  ERROR: 'error',
} as const;

// Dropdown values served by the comparison tool config endpoint for the Differential Expression
// page. A `categories` selection is one value per level, ordered outermost first: category, then
// tissue.
export const DIFFERENTIAL_EXPRESSION_CATEGORIES = {
  RNA: 'RNA - DIFFERENTIAL EXPRESSION',
  PROTEIN: 'PROTEIN - DIFFERENTIAL EXPRESSION',
} as const;

export type DifferentialExpressionCategory =
  (typeof DIFFERENTIAL_EXPRESSION_CATEGORIES)[keyof typeof DIFFERENTIAL_EXPRESSION_CATEGORIES];

// Available tissues differ per category; Hemibrain is the only one offered for both, so the header
// nav pins it regardless of which category is linked. Switching tissue is done from the CT itself.
export const DIFFERENTIAL_EXPRESSION_DEFAULT_TISSUE = 'Tissue - Hemibrain';

export const BOXPLOT_POINT_STYLES: PointStyle[] = [
  {
    label: 'Female',
    color: '#D72247',
    shape: 'triangle',
    opacity: 0.5,
  },
  {
    label: 'Male',
    color: '#245299',
    shape: 'circle',
    opacity: 0.5,
  },
];
