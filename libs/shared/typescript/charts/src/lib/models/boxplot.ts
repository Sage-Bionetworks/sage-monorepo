export type CategoryPoint = {
  // x-axis category for this point
  xAxisCategory: string;
  // y-value of this point
  value: number;
  // if defined, will plot this point in the appropriate grid. otherwise, will not use grids.
  gridCategory?: string;
  // if defined, will use a different shape and color for each pointCategory.
  pointCategory?: string;
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
  yAxisTitle?: string;
  yAxisMin?: number;
  yAxisMax?: number;
  /* if defined, will be used to map x-axis categories to tooltip text and will 
  display a tooltip on each x-axis label, where key is the xAxisCategory and value 
  is the tooltipText. otherwise, tooltips will not be displayed. */
  xAxisCategoryToTooltipText?: Record<string, string>;
  /* if defined, will be used to format tooltip for each point. */
  pointTooltipFormatter?: (pt: CategoryPoint) => string;
}
