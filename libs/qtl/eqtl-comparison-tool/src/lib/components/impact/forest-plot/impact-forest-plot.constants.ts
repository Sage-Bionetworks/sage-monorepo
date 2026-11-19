import {
  PlotAreaMargins,
  RowHoverHighlightStyle,
  TooltipStyle,
} from '@sagebionetworks/explorers/charts';
import { CELL_CLASSES } from '@sagebionetworks/qtl/config';
import { CellClassEffectSize } from './impact-forest-plot.component';

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

// Top-to-bottom row order from the design.
export const IMPACT_FOREST_PLOT_CELL_CLASS_ORDER = [
  'Endothelial',
  'Vascular Cell',
  'Immune Cell',
  'Astrocyte',
  'Oligodendrocyte Progenitor Cell',
  'Oligodendrocyte',
  'Inhibitory Neuron',
  'Excitatory Neuron',
] as const satisfies readonly (keyof typeof CELL_CLASSES)[];

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

// The astro document's cell class effect sizes for rs2120461 in the eqtl_phenotype collection.
export const IMPACT_FOREST_PLOT_EXAMPLE_EFFECT_SIZES: CellClassEffectSize[] = [
  {
    cell_type_id: 'class_astro',
    cell_class_short: 'Astro',
    cell_class: 'Astrocyte',
    value: 0.442711,
    ci_left: 0.137115,
    ci_right: 0.748307,
  },
  {
    cell_type_id: 'class_en',
    cell_class_short: 'EN',
    cell_class: 'Excitatory Neuron',
    value: -0.033462,
    ci_left: -0.638222,
    ci_right: 0.571298,
  },
  {
    cell_type_id: 'class_immune',
    cell_class_short: 'Immune',
    cell_class: 'Immune Cell',
    value: -0.22059,
    ci_left: -0.786843,
    ci_right: 0.345663,
  },
  {
    cell_type_id: 'class_in',
    cell_class_short: 'IN',
    cell_class: 'Inhibitory Neuron',
    value: -0.271663,
    ci_left: -0.46386,
    ci_right: -0.079467,
  },
  {
    cell_type_id: 'class_oligo',
    cell_class_short: 'Oligo',
    cell_class: 'Oligodendrocyte',
    value: 0.045695,
    ci_left: -0.027471,
    ci_right: 0.11886,
  },
  {
    cell_type_id: 'class_opc',
    cell_class_short: 'OPC',
    cell_class: 'Oligodendrocyte Progenitor Cell',
    value: 0.515378,
    ci_left: 0.375932,
    ci_right: 0.654825,
  },
  {
    cell_type_id: 'class_vascular',
    cell_class_short: 'Vascular',
    cell_class: 'Vascular Cell',
    value: 0.124516,
    ci_left: -0.434836,
    ci_right: 0.683868,
  },
];
