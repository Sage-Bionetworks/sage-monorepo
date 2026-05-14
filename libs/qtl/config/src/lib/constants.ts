import { LoadingIconColors } from '@sagebionetworks/explorers/models';

export const HELP_URL =
  'https://help.adknowledgeportal.org/apd/xQTL-Explorer-Resources.4669014109.html';

export const SUPPORT_EMAIL = 'xqtlexplorer@sagebionetworks.org';

export const DEFAULT_HERO_BACKGROUND_IMAGE_PATH = 'qtl-assets/images/hero-background.svg';

export const QTL_LOADING_ICON_COLORS: LoadingIconColors = {
  colorInnermost: '#8D96D6',
  colorCentral: '#8D96D6',
  colorOutermost: '#8D96D6',
};

export const ROUTE_PATHS = {
  HOME: '',
  ABOUT: 'about',
  NEWS: 'news',
  TERMS_OF_SERVICE: 'terms-of-service',
  NOT_FOUND: 'not-found',
  ERROR: 'error',
} as const;

export interface CellClass {
  color: string;
  short: string;
}

export const CELL_CLASSES = {
  'Immune Cell': { color: '#C80813', short: 'Immune' },
  Astrocyte: { color: '#D2AF81', short: 'Astro' },
  'Oligodendrocyte Progenitor Cell': { color: '#FED439', short: 'OPC' },
  Oligodendrocyte: { color: '#AEC365', short: 'Oligo' },
  'Inhibitory Neuron': { color: '#1A9993', short: 'IN' },
  'Excitatory Neuron': { color: '#197EC0', short: 'EN' },
  Endothelial: { color: '#FD7446', short: 'Endo' },
  'Vascular Cell': { color: '#FD8CC1', short: 'Mural' },
} as const satisfies Record<string, CellClass>;

export const CELL_CLASSES_FULL = {
  ...CELL_CLASSES,
  'Adaptive Immune Cell': { color: '#6F4E37', short: 'Adaptive' },
  'Layer 2-3 Intratelencephalic Excitatory Neuron': { color: '#659EC7', short: 'L2/3 IT' },
  'Layer 3-5 Intratelencephalic Excitatory Neuron 1': { color: '#95B9C7', short: 'L3/5 IT 1' },
  'Layer 3-5 Intratelencephalic Excitatory Neuron 2': { color: '#6495ED', short: 'L3/5 IT 2' },
  'Layer 3-5 Intratelencephalic Excitatory Neuron 3': { color: '#79BAEC', short: 'L3/5 IT 3' },
  'Layer 5 Extratelencephalic Excitatory Neuron': { color: '#4863A0', short: 'L5 ET' },
  'Layer 5-6 Near-Projecting Excitatory Neuron': { color: '#0020C2', short: 'L5/6 NP' },
  'Layer 6 Corticothalamic Excitatory Neuron': { color: '#3BB9FF', short: 'L6 CT' },
  'Layer 6 Excitatory Neuron 1': { color: '#B4CFEC', short: 'L6 IT 1' },
  'Layer 6 Excitatory Neuron 2': { color: '#1589FF', short: 'L6 IT 2' },
  'Layer 6B Excitatory Neuron': { color: '#488AC7', short: 'L6B' },
  'ADARB2 Inhibitory Neuron': { color: '#4E8975', short: 'ADARB2' },
  'Neurogliaform cell (LAMP5 RELN Inhibitory Neuron)': { color: '#3B9C9C', short: 'RELN' },
  'VIP Inhibitory Neuron': { color: '#89C35C', short: 'VIP' },
  'Ivy cell (LAMP5 LHX6 Inhibitory Neuron)': { color: '#7BCCB5', short: 'LHX6' },
  'Basket Cell (PVALB Inhibitory Neuron)': { color: '#B2C248', short: 'PVALB' },
  'Chandelier Cell (PVALB CHC Inhibitory Neuron)': { color: '#008080', short: 'CHC' },
  'Martinotti and Non-Martinotti cell (SST Inhibitory Neuron)': {
    color: '#728C00',
    short: 'SST',
  },
  'Perivascular Macrophage': { color: '#C11B17', short: 'PVM' },
  Microglia: { color: '#F75D59', short: 'Micro' },
  Pericyte: { color: '#E0B0FF', short: 'PC' },
  'Smooth Muscle Cell': { color: '#4E387E', short: 'SMC' },
  'Vascular Leptomeningeal Cell': { color: '#B93B8F', short: 'VLMC' },
} as const satisfies Record<string, CellClass>;
