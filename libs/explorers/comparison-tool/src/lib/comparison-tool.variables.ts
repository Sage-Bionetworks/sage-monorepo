export const knownColorMetricToDisplayName = [
  { field: 'log2_fc', displayName: 'L2FC' },
  { field: 'correlation', displayName: 'Correlation' },
];

export const NUMBER_OF_ROWS_TO_DISPLAY = 10;

export interface PaginationParams {
  pageNumber: number;
  pageSize: number;
}
