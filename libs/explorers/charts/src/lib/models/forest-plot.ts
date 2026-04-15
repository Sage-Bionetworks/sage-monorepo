export type ForestPlotItem = {
  yAxisCategory: string; // e.g. "ACC", "CBE"
  value: number; // center point (log fold change)
  ciLeft: number; // left CI bound
  ciRight: number; // right CI bound
  color?: string; // optional per-item color override
};

export interface ForestPlotProps {
  items: ForestPlotItem[];
  title?: string;
  xAxisTitle?: string;
  /**
   * Explicit x-axis bounds. **Both must be provided together** — if only one is set,
   * both are ignored and bounds are auto-calculated symmetrically from CI data (×1.1).
   */
  xAxisMin?: number;
  xAxisMax?: number;
  yAxisCategories?: string[]; // controls display order
  // Receives the category label string directly (ECharts params unwrapped internally)
  yAxisLabelTooltipFormatter?: (category: string) => string;
  pointTooltipFormatter?: (item: ForestPlotItem) => string;
  defaultLineColor?: string;
  defaultPointColor?: string;
  pointSize?: number;
  showCILabels?: boolean; // show ciLeft/ciRight values as text next to line
  // Optional formatter for CI label text; defaults to toPrecision(2)
  ciLabelFormatter?: (value: number) => string;
  noDataStyle?: 'textOnly' | 'grayBackground';
}
