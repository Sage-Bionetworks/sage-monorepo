import type { ComparisonToolSort, HeaderNavTrail } from '@sagebionetworks/explorers/testing/e2e';
import type { ComparisonToolPage } from '@sagebionetworks/model-ad/api-client';

export const COMPARISON_TOOL_PATHS: Record<ComparisonToolPage, string> = {
  'Marmoset Model Overview': '/comparison/model/marmoset',
  'Mouse Model Overview': '/comparison/model/mouse',
  'Differential Expression': '/comparison/expression',
  'Disease Correlation': '/comparison/correlation',
};

export const LEGACY_MOUSE_MODEL_OVERVIEW_PATH = '/comparison/model';

// Comparison tools whose displayed header title differs from their ui_config page name.
// Keys are ui_config page names, values are the titles rendered in the header.
// TODO(MG-1107): add the other comparison tools whose ui_config page name won't match their
// updated displayed header titles.
// Drop this map once ui_config page names match the CT displayed titles.
export const COMPARISON_TOOL_HEADER_TITLES: Partial<Record<ComparisonToolPage, string>> = {};

export const COMPARISON_TOOL_API_PATHS: Record<ComparisonToolPage, string> = {
  'Marmoset Model Overview': '/comparison-tools/marmoset-model-overview',
  'Mouse Model Overview': '/comparison-tools/mouse-model-overview',
  'Differential Expression': '/comparison-tools/transcriptomics',
  'Disease Correlation': '/comparison-tools/disease-correlation',
};

export const DIFFERENTIAL_EXPRESSION_NAV_TRAILS: Record<'RNA' | 'PROTEIN', HeaderNavTrail> = {
  RNA: {
    dropdown: 'Differential Expression',
    link: 'RNA - Differential Expression',
  },
  PROTEIN: {
    dropdown: 'Differential Expression',
    link: 'Protein - Differential Expression',
  },
};

export const COMPARISON_TOOL_NAV_TRAILS: Record<ComparisonToolPage, HeaderNavTrail> = {
  'Marmoset Model Overview': {
    dropdown: 'Model Overview',
    link: 'Marmoset Models',
  },
  'Mouse Model Overview': {
    dropdown: 'Model Overview',
    link: 'Mouse Models',
  },
  // RNA is the default sub-link for the 'Differential Expression' page.
  'Differential Expression': DIFFERENTIAL_EXPRESSION_NAV_TRAILS.RNA,
  'Disease Correlation': {
    link: 'Disease Correlation',
  },
};

export const COMPARISON_TOOL_CONFIG_PATH = 'comparison-tools/config';

// Default sort configurations for each comparison tool (required by API)
export const COMPARISON_TOOL_DEFAULT_SORTS: Record<ComparisonToolPage, ComparisonToolSort[]> = {
  'Marmoset Model Overview': [{ field: 'name', order: 1 }],
  'Mouse Model Overview': [
    { field: 'model_type', order: -1 },
    { field: 'name', order: 1 },
  ],
  'Differential Expression': [
    { field: 'gene_symbol', order: 1 },
    { field: 'name', order: 1 },
    { field: 'sex', order: 1 },
  ],
  'Disease Correlation': [
    { field: 'name', order: 1 },
    { field: 'age', order: 1 },
    { field: 'sex', order: 1 },
  ],
};
