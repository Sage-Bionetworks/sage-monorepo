import { PlotAreaMargins } from '@sagebionetworks/explorers/charts';

export const IMPACT_BOXPLOT_TITLE_PREFIX = 'Expression by Genotype';

// The chart helper only keeps a height set inline on its element, so this is bound in the template.
export const IMPACT_BOXPLOT_CHART_HEIGHT_PX = 184;

export const IMPACT_BOXPLOT_PLOT_AREA_MARGINS: PlotAreaMargins = {
  top: 8,
  right: 10,
  bottom: 20,
  left: 44,
};

// The chart draws on a canvas, which can't resolve CSS custom properties, so these are hex values.
export const IMPACT_BOXPLOT_COLORS = {
  laneBackgrounds: ['#F9F9FA', '#FFFFFF'],
  boxBorder: '#22252A',
  axisLine: '#22252A',
  axisTickLabel: '#4A5056',
  tooltipBackground: '#22252A',
} as const;
