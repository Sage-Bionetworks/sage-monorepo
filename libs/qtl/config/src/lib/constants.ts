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
  // TODO: update based on values in QTL-34
  EQTL: 'comparison/eqtl',
  TERMS_OF_SERVICE: 'terms-of-service',
  NOT_FOUND: 'not-found',
  ERROR: 'error',
} as const;

export interface CellClass {
  color: string;
  mutedColor?: string;
  short: string;
}

export const CELL_CLASSES = {
  'Immune Cell': { color: '#C80813', mutedColor: '#E38389', short: 'Immune' },
  Astrocyte: { color: '#D2AF81', mutedColor: '#E9D7C0', short: 'Astro' },
  'Oligodendrocyte Progenitor Cell': { color: '#FED439', mutedColor: '#FFE99C', short: 'OPC' },
  Oligodendrocyte: { color: '#AEC365', mutedColor: '#D7E1B2', short: 'Oligo' },
  'Inhibitory Neuron': { color: '#1A9993', mutedColor: '#8CCCC9', short: 'IN' },
  'Excitatory Neuron': { color: '#197EC0', mutedColor: '#8CBEDF', short: 'EN' },
  Endothelial: { color: '#FD7446', mutedColor: '#FEB9A2', short: 'Endo' },
  'Vascular Cell': { color: '#FD8CC1', mutedColor: '#FEC5E0', short: 'Mural' },
} as const satisfies Record<string, CellClass>;

export interface CellSubclass extends CellClass {
  cellClass: keyof typeof CELL_CLASSES;
}

export const CELL_SUBCLASSES = {
  Astrocyte: { ...CELL_CLASSES.Astrocyte, cellClass: 'Astrocyte' },
  'Oligodendrocyte Progenitor Cell': {
    ...CELL_CLASSES['Oligodendrocyte Progenitor Cell'],
    cellClass: 'Oligodendrocyte Progenitor Cell',
  },
  Oligodendrocyte: { ...CELL_CLASSES.Oligodendrocyte, cellClass: 'Oligodendrocyte' },
  Endothelial: { ...CELL_CLASSES.Endothelial, cellClass: 'Endothelial' },
  'Adaptive Immune Cell': { color: '#6F4E37', short: 'Adaptive', cellClass: 'Immune Cell' },
  'Layer 2-3 Intratelencephalic Excitatory Neuron': {
    color: '#659EC7',
    short: 'L2/3 IT',
    cellClass: 'Excitatory Neuron',
  },
  'Layer 3-5 Intratelencephalic Excitatory Neuron 1': {
    color: '#95B9C7',
    short: 'L3/5 IT 1',
    cellClass: 'Excitatory Neuron',
  },
  'Layer 3-5 Intratelencephalic Excitatory Neuron 2': {
    color: '#6495ED',
    short: 'L3/5 IT 2',
    cellClass: 'Excitatory Neuron',
  },
  'Layer 3-5 Intratelencephalic Excitatory Neuron 3': {
    color: '#79BAEC',
    short: 'L3/5 IT 3',
    cellClass: 'Excitatory Neuron',
  },
  'Layer 5 Extratelencephalic Excitatory Neuron': {
    color: '#4863A0',
    short: 'L5 ET',
    cellClass: 'Excitatory Neuron',
  },
  'Layer 5-6 Near-Projecting Excitatory Neuron': {
    color: '#0020C2',
    short: 'L5/6 NP',
    cellClass: 'Excitatory Neuron',
  },
  'Layer 6 Corticothalamic Excitatory Neuron': {
    color: '#3BB9FF',
    short: 'L6 CT',
    cellClass: 'Excitatory Neuron',
  },
  'Layer 6 Excitatory Neuron 1': {
    color: '#B4CFEC',
    short: 'L6 IT 1',
    cellClass: 'Excitatory Neuron',
  },
  'Layer 6 Excitatory Neuron 2': {
    color: '#1589FF',
    short: 'L6 IT 2',
    cellClass: 'Excitatory Neuron',
  },
  'Layer 6B Excitatory Neuron': {
    color: '#488AC7',
    short: 'L6B',
    cellClass: 'Excitatory Neuron',
  },
  'ADARB2 Inhibitory Neuron': {
    color: '#4E8975',
    short: 'ADARB2',
    cellClass: 'Inhibitory Neuron',
  },
  'Neurogliaform cell (LAMP5 RELN Inhibitory Neuron)': {
    color: '#3B9C9C',
    short: 'RELN',
    cellClass: 'Inhibitory Neuron',
  },
  'VIP Inhibitory Neuron': { color: '#89C35C', short: 'VIP', cellClass: 'Inhibitory Neuron' },
  'Ivy cell (LAMP5 LHX6 Inhibitory Neuron)': {
    color: '#7BCCB5',
    short: 'LHX6',
    cellClass: 'Inhibitory Neuron',
  },
  'Basket Cell (PVALB Inhibitory Neuron)': {
    color: '#B2C248',
    short: 'PVALB',
    cellClass: 'Inhibitory Neuron',
  },
  'Chandelier Cell (PVALB CHC Inhibitory Neuron)': {
    color: '#008080',
    short: 'CHC',
    cellClass: 'Inhibitory Neuron',
  },
  'Martinotti and Non-Martinotti cell (SST Inhibitory Neuron)': {
    color: '#728C00',
    short: 'SST',
    cellClass: 'Inhibitory Neuron',
  },
  'Perivascular Macrophage': { color: '#C11B17', short: 'PVM', cellClass: 'Immune Cell' },
  Microglia: { color: '#F75D59', short: 'Micro', cellClass: 'Immune Cell' },
  Pericyte: { color: '#E0B0FF', short: 'PC', cellClass: 'Vascular Cell' },
  'Smooth Muscle Cell': { color: '#4E387E', short: 'SMC', cellClass: 'Vascular Cell' },
  'Vascular Leptomeningeal Cell': { color: '#B93B8F', short: 'VLMC', cellClass: 'Vascular Cell' },
} as const satisfies Record<string, CellSubclass>;
