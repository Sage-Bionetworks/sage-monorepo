import type { ComparisonToolSort, HeaderNavTrail } from '@sagebionetworks/explorers/testing/e2e';
import type { ComparisonToolPage } from '@sagebionetworks/model-ad/api-client';

export const COMPARISON_TOOL_PATHS: Record<ComparisonToolPage, string> = {
  'Marmoset Model Overview': '/comparison/model/marmoset',
  'Model Overview': '/comparison/model/mouse',
  'Differential Expression': '/comparison/expression',
  'Disease Correlation': '/comparison/correlation',
};

export const LEGACY_MOUSE_MODEL_OVERVIEW_PATH = '/comparison/model';

// Comparison tools whose displayed header title differs from their ui_config page name.
// TODO(MG-1057): drop this map once ui_config explicitly names the mouse page 'Mouse Model
// Overview', since the keys above will then match the displayed titles.
export const COMPARISON_TOOL_HEADER_TITLES: Partial<Record<ComparisonToolPage, string>> = {
  'Model Overview': 'Mouse Model Overview',
};

export const COMPARISON_TOOL_API_PATHS: Record<ComparisonToolPage, string> = {
  'Marmoset Model Overview': '/comparison-tools/marmoset-model-overview',
  'Model Overview': '/comparison-tools/mouse-model-overview',
  'Differential Expression': '/comparison-tools/transcriptomics',
  'Disease Correlation': '/comparison-tools/disease-correlation',
};

export const COMPARISON_TOOL_NAV_TRAILS: Record<ComparisonToolPage, HeaderNavTrail> = {
  'Marmoset Model Overview': {
    dropdown: 'Model Overview',
    link: 'Marmoset Models',
  },
  'Model Overview': {
    dropdown: 'Model Overview',
    link: 'Mouse Models',
  },
  'Differential Expression': {
    dropdown: 'Differential Expression',
    link: 'RNA - Differential Expression',
  },
  'Disease Correlation': {
    link: 'Disease Correlation',
  },
};

export const COMPARISON_TOOL_CONFIG_PATH = 'comparison-tools/config';

// Default sort configurations for each comparison tool (required by API)
export const COMPARISON_TOOL_DEFAULT_SORTS: Record<ComparisonToolPage, ComparisonToolSort[]> = {
  'Marmoset Model Overview': [{ field: 'name', order: 1 }],
  'Model Overview': [
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
