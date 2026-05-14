import type { AxisLineStyle, AxisTickLabelStyle, GridLineStyle } from './axis';
import type { TooltipStyle } from './tooltip';

export type ForestPlotItem = {
  yAxisCategory: string; // e.g. "ACC", "CBE"
  value: number; // center point (log fold change)
  ciLeft: number; // left CI bound
  ciRight: number; // right CI bound
  color?: string; // optional per-item color override
};

export type RowHoverHighlightStyle = {
  backgroundColor: string;
  /** Band thickness in pixels. Centered on the row tick; keep it smaller than the row pitch so adjacent rows don't overlap. */
  thickness: number;
};

export interface ForestPlotProps {
  items: ForestPlotItem[];
  title?: string;
  xAxisTitle?: string;
  /**
   * Explicit x-axis bounds. **Both must be provided together** -- if only one is set,
   * both are ignored and bounds are auto-calculated symmetrically from CI data (*1.1).
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
  /** Stroke width of each CI (stick) line in pixels. Defaults to CI_LINE_WIDTH (1.5px). */
  ciLineWidth?: number;
  showCILabels?: boolean; // show ciLeft/ciRight values as text next to line
  // Optional formatter for CI label text; defaults to toPrecision(2)
  ciLabelFormatter?: (value: number) => string;
  noDataStyle?: 'textOnly' | 'grayBackground';
  /** If false, hides the vertical reference line at x=0. Defaults to true. */
  showZeroLine?: boolean;
  /**
   * Explicit step between x-axis ticks. Ticks fall on multiples of the interval (anchored
   * to 0), so they land on round values regardless of `xAxisMin` -- e.g. interval 0.2
   * within [-0.1, 0.7] yields ticks at 0, 0.2, 0.4, 0.6. Defaults to `(xMax - xMin) / 10`.
   * Non-positive values fall through to the default.
   */
  xAxisInterval?: number;
  /** Number of decimal places to render on each x-axis tick label. Defaults to 2. */
  xAxisLabelPrecision?: number;
  /** If defined, overrides the x-axis line stroke. */
  xAxisLineStyle?: AxisLineStyle;
  /**
   * If defined, draws a styled y-axis frame line at the left edge of the plot (hidden by
   * default). The x=0 reference line is a separate concept controlled via `showZeroLine`
   * -- the frame line is always anchored to the left edge, never to x=0.
   */
  yAxisLineStyle?: AxisLineStyle;
  /** If defined, overrides x-axis tick label typography. */
  xAxisTickLabelStyle?: AxisTickLabelStyle;
  /** If defined, overrides y-axis tick label typography. */
  yAxisTickLabelStyle?: AxisTickLabelStyle;
  /** If true, mirrors x-axis tick labels on the top of the plot. */
  showXAxisLabelsOnTop?: boolean;
  /** If defined, draws a vertical grid line at each x-axis tick. */
  xAxisGridLineStyle?: GridLineStyle;
  /** If defined, draws a horizontal grid line at each y-axis tick. */
  yAxisGridLineStyle?: GridLineStyle;
  /** If defined, enables row hover highlighting with the given style. */
  rowHoverHighlightStyle?: RowHoverHighlightStyle;
  /** If defined, overrides individual fields on the default tooltip styling. */
  tooltipStyle?: TooltipStyle;
}
