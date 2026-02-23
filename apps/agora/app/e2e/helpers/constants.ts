// --------- GCT -----------------------------------------------------------------------------------
export const GCT_CATEGORIES = {
  RNA: 'RNA - Differential Expression',
  PROTEIN: 'Protein - Differential Expression',
};

export const GCT_RNA_SUBCATEGORIES = {
  AD: 'AD Diagnosis (males and females)',
  AD_AOD: 'AD Diagnosis x AOD (males and females)',
  AD_SEX_F: 'AD Diagnosis x Sex (females only)',
  AD_SEX_M: 'AD Diagnosis x Sex (males only)',
};

export const GCT_PROTEIN_SUBCATEGORIES = {
  SRM: 'Targeted Selected Reaction Monitoring (SRM)',
  TMT: 'Genome-wide Tandem Mass Tag (TMT)',
  LFQ: 'Genome-wide Label-free Quantification (LFQ)',
};

export const URL_GCT = '/genes/comparison';
export const URL_GCT_PROTEIN = `${URL_GCT}?category=${GCT_CATEGORIES.PROTEIN}`;
export const URL_GCT_PROTEIN_TMT = `${URL_GCT_PROTEIN}&subCategory=TMT`;

// --------- explorers-based CTs -------------------------------------------------------------------
export const COMPARISON_TOOL_PATHS: Record<string, string> = {
  'Nominated Targets': '/comparison/targets',
  'Nominated Drugs': '/comparison/drugs',
};

export const COMPARISON_TOOL_API_PATHS: Record<string, string> = {
  'Nominated Targets': '/comparison-tools/nominated-target',
  'Nominated Drugs': '/comparison-tools/drugs',
};

export const COMPARISON_TOOL_CONFIG_PATH = 'comparison-tools/config';

// Default sort configurations for each comparison tool (required by API)
export const COMPARISON_TOOL_DEFAULT_SORTS: Record<string, { field: string; order: 1 | -1 }[]> = {
  'Nominated Targets': [
    { field: 'total_nominations', order: -1 },
    { field: 'hgnc_symbol', order: 1 },
  ],
  'Nominated Drugs': [
    { field: 'total_nominations', order: -1 },
    { field: 'common_name', order: 1 },
  ],
};
