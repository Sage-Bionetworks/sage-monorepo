import { SynapseWikiParams } from './synapse-wiki';

export interface ComparisonToolViewConfig {
  selectorsWikiParams: Record<string, SynapseWikiParams>;
  headerTitle: string;
  filterResultsButtonTooltip: string;
}

export interface ComparisonToolFilterOption {
  label: string;
  selected: boolean;
}

export interface ComparisonToolFilter {
  name: string;
  data_key: string;
  short_name?: string;
  options: ComparisonToolFilterOption[];
}

export interface ComparisonToolConfigFilter {
  name: string;
  data_key: string;
  short_name?: string;
  values: string[];
}

export type ComparisonToolPage = 'Model Overview' | 'Gene Expression' | 'Disease Correlation';

export type ComparisonToolConfigColumnType =
  | 'text'
  | 'heat_map'
  | 'link_internal'
  | 'link_external'
  | 'primary';
export const ComparisonToolConfigColumnTypeEnum = {
  Text: 'text' as ComparisonToolConfigColumnType,
  HeatMap: 'heat_map' as ComparisonToolConfigColumnType,
  LinkInternal: 'link_internal' as ComparisonToolConfigColumnType,
  LinkExternal: 'link_external' as ComparisonToolConfigColumnType,
  Primary: 'primary' as ComparisonToolConfigColumnType,
} as const;

export interface ComparisonToolConfigColumn {
  name?: string;
  type: ComparisonToolConfigColumnType;
  data_key: string;
  tooltip?: string;
  sort_tooltip?: string;
  link_text?: string;
  link_url?: string;
}

export interface ComparisonToolColumn extends ComparisonToolConfigColumn {
  selected: boolean;
}

export interface ComparisonToolColumns {
  dropdowns: string[];
  columns: ComparisonToolColumn[];
}

export interface ComparisonToolConfig {
  page: ComparisonToolPage;
  dropdowns: string[];
  row_count: string;
  columns: ComparisonToolConfigColumn[];
  filters: ComparisonToolConfigFilter[];
}

export type HeatmapCircleData<ColorKey extends string = string> = {
  adj_p_val: number | null | undefined;
} & Record<ColorKey, number | null | undefined>;

export type HeatmapCircleColorKey<T extends HeatmapCircleData> = Extract<
  Exclude<keyof T, 'adj_p_val'>,
  string
>;
