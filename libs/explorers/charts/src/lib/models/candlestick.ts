export type CandlestickItem = {
  xAxisCategory: string;
  value: number;
  ciLower: number;
  ciUpper: number;
  color?: string;
};

export interface CandlestickProps {
  items: CandlestickItem[];
  title?: string;
  xAxisTitle?: string;
  yAxisTitle?: string;
  /**
   * Explicit y-axis bounds. **Both must be provided together** -- if only one is set,
   * both are ignored and bounds are auto-calculated symmetrically from CI data (x1.1).
   */
  yAxisMin?: number;
  yAxisMax?: number;
  xAxisCategories?: string[];
  xAxisLabelTooltipFormatter?: (category: string) => string;
  pointTooltipFormatter?: (item: CandlestickItem) => string;
  defaultLineColor?: string;
  defaultPointColor?: string;
  pointSize?: number;
  /** Draws a horizontal reference line across the grid at this y value (e.g. 1.0). */
  referenceLineValue?: number;
  /** Color of the reference line. Default `red`. */
  referenceLineColor?: string;
  noDataStyle?: 'textOnly' | 'grayBackground';
}
