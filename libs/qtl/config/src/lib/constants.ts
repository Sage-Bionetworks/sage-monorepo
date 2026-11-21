import { NOT_FOUND_ROUTE_PATH } from '@sagebionetworks/explorers/constants';
import { LoadingIconColors } from '@sagebionetworks/explorers/models';

export const HELP_URL =
  'https://help.adknowledgeportal.org/apd/xQTL-Explorer-Resources.4669014109.html';

export const SUPPORT_EMAIL = 'xqtlexplorer@sagebionetworks.org';

export const SITE_NAME = 'QTL';

export const DEFAULT_SYNAPSE_WIKI_OWNER_ID = 'syn74592415';

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
  EQTL: 'comparison/eqtl',
  TERMS_OF_SERVICE: 'terms-of-service',
  NOT_FOUND: NOT_FOUND_ROUTE_PATH,
  ERROR: 'error',
} as const;

// Keys are the data's cell_type_id. Colors follow QTL's cell type color guide:
// https://www.synapse.org/Synapse:syn77455755
export interface CellClass {
  name: string;
  short: string;
  color: string;
}

export const CELL_CLASSES = {
  class_astro: { name: 'Astrocyte', short: 'Astro', color: '#D2AF80' },
  class_en: { name: 'Excitatory Neuron', short: 'EN', color: '#197EC0' },
  class_immune: { name: 'Immune Cell', short: 'Immune', color: '#C80813' },
  class_in: { name: 'Inhibitory Neuron', short: 'IN', color: '#199992' },
  class_oligo: { name: 'Oligodendrocyte', short: 'Oligo', color: '#AEC365' },
  class_opc: { name: 'Oligodendrocyte Progenitor Cell', short: 'OPC', color: '#F0C400' },
  class_vascular: { name: 'Vascular Cell', short: 'Vascular', color: '#FD8CC1' },
} as const satisfies Record<string, CellClass>;

export type CellClassId = keyof typeof CELL_CLASSES;

// --color-gray-700 as a hex value, since canvas charts can't resolve CSS custom properties.
export const UNKNOWN_CELL_TYPE_COLOR = '#878E95';

export interface CellSubclass {
  name: string;
  cellClassId: CellClassId;
  color: string;
}

export const CELL_SUBCLASSES = {
  astro: { name: 'Astrocyte', cellClassId: 'class_astro', color: '#C1996A' },
  enl23it: {
    name: 'Layer 2-3 Intratelencephalic Excitatory Neuron',
    cellClassId: 'class_en',
    color: '#659DC7',
  },
  enl35it1: {
    name: 'Layer 3-5 Intratelencephalic Excitatory Neuron 1',
    cellClassId: 'class_en',
    color: '#95B9C7',
  },
  enl35it2: {
    name: 'Layer 3-5 Intratelencephalic Excitatory Neuron 2',
    cellClassId: 'class_en',
    color: '#6395EC',
  },
  enl35it3: {
    name: 'Layer 3-5 Intratelencephalic Excitatory Neuron 3',
    cellClassId: 'class_en',
    color: '#78B9EC',
  },
  enl5et: {
    name: 'Layer 5 Extratelencephalic Excitatory Neuron',
    cellClassId: 'class_en',
    color: '#4862A1',
  },
  enl56np: {
    name: 'Layer 5-6 Near-Projecting Excitatory Neuron',
    cellClassId: 'class_en',
    color: '#0020C2',
  },
  enl6ct: {
    name: 'Layer 6 Corticothalamic Excitatory Neuron',
    cellClassId: 'class_en',
    color: '#3AB9FF',
  },
  enl6it1: {
    name: 'Layer 6 Intratelencephalic Excitatory Neuron 1',
    cellClassId: 'class_en',
    color: '#5B70DD',
  },
  enl6it2: {
    name: 'Layer 6 Intratelencephalic Excitatory Neuron 2',
    cellClassId: 'class_en',
    color: '#1589FF',
  },
  enl6b: { name: 'Layer 6B Excitatory Neuron', cellClassId: 'class_en', color: '#488AC7' },
  adaptiveimmune: { name: 'Adaptive Immune Cell', cellClassId: 'class_immune', color: '#6F4E37' },
  micropvm: {
    name: 'Microglia and Perivascular Macrophage',
    cellClassId: 'class_immune',
    color: '#F75D58',
  },
  incgekcng1: { name: 'ADARB2 Inhibitory Neuron', cellClassId: 'class_in', color: '#4D8974' },
  incgelamp5reln: {
    name: 'Neurogliaform cell (LAMP5 RELN Inhibitory Neuron)',
    cellClassId: 'class_in',
    color: '#3A9C9C',
  },
  incgevip: { name: 'VIP Inhibitory Neuron', cellClassId: 'class_in', color: '#89C35B' },
  inlamp5lhx6: {
    name: 'Ivy cell (LAMP5 LHX6 Inhibitory Neuron)',
    cellClassId: 'class_in',
    color: '#7BCCB6',
  },
  inmgepvalb: {
    name: 'Basket Cell (PVALB Inhibitory Neuron)',
    cellClassId: 'class_in',
    color: '#B2C149',
  },
  inmgepvalbchc: {
    name: 'Chandelier Cell (PVALB CHC Inhibitory Neuron)',
    cellClassId: 'class_in',
    color: '#007F7F',
  },
  inmgesst: {
    name: 'Martinotti and Non-Martinotti cell (SST Inhibitory Neuron)',
    cellClassId: 'class_in',
    color: '#718B01',
  },
  oligo: { name: 'Oligodendrocyte', cellClassId: 'class_oligo', color: '#D2C98D' },
  opc: { name: 'Oligodendrocyte Progenitor Cell', cellClassId: 'class_opc', color: '#F7DB3B' },
  endo: { name: 'Endothelial Cell', cellClassId: 'class_vascular', color: '#FFA62F' },
  pc: { name: 'Pericyte', cellClassId: 'class_vascular', color: '#E0B0FF' },
  smc: { name: 'Smooth Muscle Cell', cellClassId: 'class_vascular', color: '#4E387E' },
  vlmc: { name: 'Vascular Leptomeningeal Cell', cellClassId: 'class_vascular', color: '#B93B8F' },
} as const satisfies Record<string, CellSubclass>;

export type CellSubclassId = keyof typeof CELL_SUBCLASSES;
