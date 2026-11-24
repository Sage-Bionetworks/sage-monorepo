import {
  PlotAreaMargins,
  RowHoverHighlightStyle,
  TooltipStyle,
} from '@sagebionetworks/explorers/charts';

export const IMPACT_FOREST_PLOT_TITLE = 'Effect Sizes by Cell Class';

export const IMPACT_FOREST_PLOT_X_AXIS_TITLE = 'Effect Size (Beta)';

// The chart helper only keeps a height set inline on its element, so this is bound in the template.
export const IMPACT_FOREST_PLOT_CHART_HEIGHT_PX = 230;

// The right margin leaves room for half of the last x-axis tick label, which sits on the plot edge.
export const IMPACT_FOREST_PLOT_PLOT_AREA_MARGINS: PlotAreaMargins = {
  top: 11,
  right: 10,
  bottom: 0,
  left: 0,
};

// Distances from each axis line to its tick labels, from the design.
export const IMPACT_FOREST_PLOT_X_AXIS_LABEL_MARGIN_PX = 17;
export const IMPACT_FOREST_PLOT_Y_AXIS_LABEL_MARGIN_PX = 10;

export const IMPACT_FOREST_PLOT_POINT_SIZE_PX = 11;

export const IMPACT_FOREST_PLOT_CI_LINE_WIDTH_PX = 1;

export const IMPACT_FOREST_PLOT_TARGET_TICK_INTERVALS = 4;

export const IMPACT_FOREST_PLOT_TOOLTIP_VALUE_DECIMALS = 2;

// The chart draws on a canvas, which can't resolve CSS custom properties, so these are hex values.
export const IMPACT_FOREST_PLOT_COLORS = {
  categoryLabel: '#353A3F',
  tickLabel: '#4A5056',
  axisLine: '#22252A',
  xAxisGridLine: '#D0D4D9',
  yAxisGridLine: '#F1F2F4',
  tooltipBackground: '#22252A',
} as const;

export const IMPACT_FOREST_PLOT_TOOLTIP_STYLE: TooltipStyle = {
  backgroundColor: IMPACT_FOREST_PLOT_COLORS.tooltipBackground,
  pinAbove: true,
  textAlign: 'left',
  lineHeight: 16,
  padding: 8,
  borderRadius: 2,
};

export const IMPACT_FOREST_PLOT_ROW_HOVER_HIGHLIGHT_STYLE: RowHoverHighlightStyle = {
  backgroundColor: 'rgba(158, 158, 158, 0.15)',
  thickness: 13,
};
