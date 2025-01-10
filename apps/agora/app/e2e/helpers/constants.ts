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
