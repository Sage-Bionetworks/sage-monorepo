import type { CallbackDataParams } from 'echarts/types/dist/shared';

export type CategoryPoint = {
  // x-axis category for this point
  xAxisCategory: string;
  // y-value of this point
  value: number;
  // if defined, will plot this point in the appropriate grid. otherwise, will not use grids.
  gridCategory?: string;
  // if defined, will use a different shape and color for each pointCategory.
  pointCategory?: string;
  // additional text about this point
  text?: string;
};

export type CategoryAsValuePoint = CategoryPoint & {
  /* x-axis category for this point as a number based on
  1-based indexing of the unique x-axis category values
  if pointCategory values are also defined for the points, then value will be
  offset such that all pointCategories are equally spaced around the
  original index value */
  xAxisValue: number;
};

export type CategoryBoxplotSummary = {
  // x-axis category for this boxplot
  xAxisCategory: string;
  min: number;
  firstQuartile: number;
  median: number;
  thirdQuartile: number;
  max: number;
  // if defined, will plot this boxplot in the appropriate grid. otherwise, will not use grids.
  gridCategory?: string;
};

export type CategoryAsValueBoxplotSummary = CategoryBoxplotSummary & {
  /* x-axis category for this point as a number based on
  1-based indexing of the unique x-axis category values */
  xAxisValue: number;
};

export interface BoxplotProps {
  points: CategoryPoint[];
  /* if defined, will be used to plot boxplots. otherwise, will calculate 
  boxplot summary statistics using the provided points. */
  summaries?: CategoryBoxplotSummary[];
  title?: string;
  xAxisTitle?: string;
  xAxisLabelFormatter?: (value: string) => string;
  xAxisLabelTooltipFormatter?: (params: CallbackDataParams) => string;
  /* if defined will determine the order in which categories appear on the x-axis */
  xAxisCategories?: string[];
  yAxisTitle?: string;
  yAxisMin?: number;
  yAxisMax?: number;
  /* if defined, will be used to format tooltip for each point. */
  pointTooltipFormatter?: (pt: CategoryPoint, params: CallbackDataParams) => string;
  /* if defined, will be used to set color for each point based on its pointCategory, 
  where key is the pointCategory and value is the color. */
  pointCategoryColors?: Record<string, string>;
  /* if defined, will be used to set shape for each point based on its pointCategory, 
  where key is the pointCategory and value is the shape. */
  pointCategoryShapes?: Record<string, string>;
  showLegend?: boolean;
  pointOpacity?: number;
  noDataStyle?: 'textOnly' | 'grayBackground';
  chartStyle?: 'minimal' | 'grayGrid';
}
