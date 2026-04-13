import { CallbackDataParams } from 'echarts/types/dist/shared';

export type ForestPlotItem = {
  yAxisCategory: string; // e.g. "ACC", "CBE"
  value: number; // center point (log fold change)
  ciLeft: number; // left CI bound
  ciRight: number; // right CI bound
  text?: string; // extra tooltip text
  color?: string; // optional per-item color override
};

export interface ForestPlotProps {
  items: ForestPlotItem[];
  title?: string;
  xAxisTitle?: string;
  xAxisMin?: number; // auto-calculated if absent (symmetrical from CI bounds ×1.1)
  xAxisMax?: number;
  yAxisCategories?: string[]; // controls display order
  // Receives the category label string directly (ECharts params unwrapped internally)
  yAxisLabelTooltipFormatter?: (category: string) => string;
  pointTooltipFormatter?: (item: ForestPlotItem, params: CallbackDataParams) => string;
  defaultLineColor?: string;
  defaultPointColor?: string;
  pointSize?: number;
  showCILabels?: boolean; // show ciLeft/ciRight values as text next to line
  // Optional formatter for CI label text; defaults to toPrecision(2)
  ciLabelFormatter?: (value: number) => string;
  noDataStyle?: 'textOnly' | 'grayBackground';
}
