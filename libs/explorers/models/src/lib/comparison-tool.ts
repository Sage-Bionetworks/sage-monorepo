export interface ComparisonToolFilterOption {
  label: string;
  selected: boolean;
}

export interface ComparisonToolFilter {
  name: string;
  field: string;
  options: ComparisonToolFilterOption[];
}

export interface ComparisonToolConfigFilter {
  name: string;
  field: string;
  values: string[];
}

export type ComparisonToolPage = 'Model Overview' | 'Gene Expression' | 'Disease Correlation';

export type ComparisonToolConfigColumnType =
  | 'text'
  | 'heat_map'
  | 'link_internal'
  | 'link_external';
export const ComparisonToolConfigColumnTypeEnum = {
  Text: 'text' as ComparisonToolConfigColumnType,
  HeatMap: 'heat_map' as ComparisonToolConfigColumnType,
  LinkInternal: 'link_internal' as ComparisonToolConfigColumnType,
  LinkExternal: 'link_external' as ComparisonToolConfigColumnType,
} as const;

export interface ComparisonToolConfigColumn {
  name: string;
  field: string;
  type: ComparisonToolConfigColumnType;
  tooltip: string;
  sort_tooltip: string;
  link_text?: string;
  link_url?: string;
}

export interface ComparisonToolConfig {
  page: ComparisonToolPage;
  dropdowns: Array<string>;
  columns: Array<ComparisonToolConfigColumn>;
  filters?: Array<ComparisonToolConfigFilter>;
}

export type HeatmapCircleData = Record<string, number | null | undefined> & {
  adj_p_val: number | null | undefined;
};
