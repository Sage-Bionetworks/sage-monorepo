import { PointStyle } from '@sagebionetworks/explorers/charts';
import { DownloadNote, LoadingIconColors } from '@sagebionetworks/explorers/models';

export const HELP_URL =
  'https://help.adknowledgeportal.org/apd/Model-AD+Explorer+Resources.4077682781.html';

export const SUPPORT_EMAIL = 'modeladexplorer@sagebionetworks.org';

export const DOWNLOAD_PINS_NOTE: DownloadNote = {
  textBefore: 'See the ',
  linkText: 'Model AD Explorer documentation',
  linkUrl:
    'https://help.adknowledgeportal.org/apd/Model-AD-Explorer-Resources-Resources.4077682781.html#ModelADExplorerResources-GeneExpressionAnalysis',
  textAfter:
    ' for links to the study-specific data files, metadata files, and methods documentation.',
};

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
  MOUSE_MODEL_OVERVIEW: 'comparison/model/mouse',
  DIFFERENTIAL_EXPRESSION: 'comparison/expression',
  GENES: 'genes',
  PROTEINS: 'proteins',
  DISEASE_CORRELATION: 'comparison/correlation',
  MODELS: 'models',
  TERMS_OF_SERVICE: 'terms-of-service',
  NOT_FOUND: 'not-found',
  ERROR: 'error',
} as const;

// First-level category values only. The URL's `categories` is a comma-separated, outermost-first
// list; each value here must stay its first entry so the header keeps the link active as the page
// appends deeper levels (tissue, ...). See header isLinkActive.
export const DIFFERENTIAL_EXPRESSION_CATEGORIES = {
  RNA: 'RNA - DIFFERENTIAL EXPRESSION',
  PROTEIN: 'PROTEIN - DIFFERENTIAL EXPRESSION',
} as const;

export type DifferentialExpressionCategory =
  (typeof DIFFERENTIAL_EXPRESSION_CATEGORIES)[keyof typeof DIFFERENTIAL_EXPRESSION_CATEGORIES];

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
