export const COMPARISON_TOOL_PATHS: Record<string, string> = {
  'Model Overview': '/comparison/model',
  'Gene Expression': '/comparison/expression',
  'Disease Correlation': '/comparison/correlation',
};

export const COMPARISON_TOOL_API_PATHS: Record<string, string> = {
  'Model Overview': '/comparison-tools/model-overview',
  'Gene Expression': '/comparison-tools/gene-expression',
  'Disease Correlation': '/comparison-tools/disease-correlation',
};

export const COMPARISON_TOOL_CONFIG_PATH = 'comparison-tools/config';

// Default sort configurations for each comparison tool (required by API)
export const COMPARISON_TOOL_DEFAULT_SORTS: Record<string, { field: string; order: 1 | -1 }[]> = {
  'Model Overview': [
    { field: 'model_type', order: -1 },
    { field: 'name', order: 1 },
  ],
  'Gene Expression': [
    { field: 'gene_symbol', order: 1 },
    { field: 'name', order: 1 },
  ],
  'Disease Correlation': [
    { field: 'name', order: 1 },
    { field: 'age', order: 1 },
    { field: 'sex', order: 1 },
  ],
};
