import { SynapseWikiParams } from './synapse-wiki';

export interface HeatmapDetailsPanelData {
  label?: { left: string; right?: string };
  heading: string;
  subHeadings: string[];
  value?: number;
  valueLabel: string;
  pValue?: number;
  footer: string;
}

/**
 * Context provided to the heatmap circle click transform function.
 * Contains all the data needed to build the panel content.
 */
export interface HeatmapCircleClickTransformFnContext {
  rowData: unknown; // The row data object for the clicked cell
  cellData: unknown; // The cell data (typically HeatmapCircleData) for the clicked cell
  columnKey: string; // The column key (data_key) for the clicked cell
}

/**
 * Transform function that maps consumer-specific data to generic panel data.
 * Return null to prevent the panel from showing.
 */
export type heatmapCircleClickTransformFn = (
  context: HeatmapCircleClickTransformFnContext,
) => HeatmapDetailsPanelData | null;

export interface LegendPanelConfig {
  colorChartLowerLabel: string;
  colorChartUpperLabel: string;
  colorChartText: string;
  sizeChartLowerLabel: string;
  sizeChartUpperLabel: string;
  sizeChartText: string;
}

export interface VisualizationOverviewPane {
  heading: string;
  content: string;
}

export type SortOrder = 1 | -1;

export interface ComparisonToolQuery {
  categories: string[];
  pinnedItems: string[];
  pageNumber: number;
  pageSize: number;
  multiSortMeta: { field: string; order: number }[];
  searchTerm: string | null;
  filters: ComparisonToolFilter[];
}

export interface ComparisonToolViewConfig {
  selectorsWikiParams: Record<string, SynapseWikiParams>;
  headerTitle: string;
  headerTitleWikiParams?: SynapseWikiParams;
  filterResultsButtonTooltip: string;
  showSignificanceControls: boolean;
  viewDetailsTooltip: string;
  viewDetailsClick: (rowData: unknown) => void;
  legendEnabled: boolean;
  legendPanelConfig: LegendPanelConfig;
  rowsPerPage: number;
  rowIdDataKey: string;
  allowPinnedImageDownload: boolean;
  defaultSort?: readonly { readonly field: string; readonly order: 1 | -1 }[];
  heatmapCircleClickTransformFn?: heatmapCircleClickTransformFn;
  linkExportField: 'link_url' | 'link_text';
}

export interface ComparisonToolFilterOption {
  label: string;
  selected: boolean;
}

export interface ComparisonToolFilter {
  name: string;
  data_key: string;
  short_name?: string;
  query_param_key: string;
  options: ComparisonToolFilterOption[];
}

export interface ComparisonToolConfigFilter {
  name: string;
  data_key: string;
  short_name?: string;
  query_param_key: string;
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
  is_exported: boolean;
  is_hidden: boolean;
}

export interface ComparisonToolColumn extends ComparisonToolConfigColumn {
  selected: boolean;
}

export interface ComparisonToolConfig {
  page: ComparisonToolPage;
  dropdowns: string[];
  row_count: string | null;
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

export type ComparisonToolLink = {
  link_text?: string;
  link_url?: string;
};

export interface ComparisonToolUrlParams {
  pinnedItems?: string[] | null;
  categories?: string[] | null;
  sortFields?: string[] | null;
  sortOrders?: SortOrder[] | null;
  filterSelections?: Record<string, string[]> | null;
}

export interface PaginationParams {
  pageNumber: number;
  pageSize: number;
}
