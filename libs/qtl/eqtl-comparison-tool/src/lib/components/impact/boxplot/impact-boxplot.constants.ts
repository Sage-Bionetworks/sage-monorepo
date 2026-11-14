import { BoxplotGridMargins } from '@sagebionetworks/explorers/charts';
import { GenotypeExpressionSummary } from './impact-boxplot.component';

export const IMPACT_BOXPLOT_TITLE_PREFIX = 'Expression by Genotype';

// The chart helper only keeps a height set inline on its element, so this is bound in the template.
export const IMPACT_BOXPLOT_CHART_HEIGHT_PX = 184;

export const IMPACT_BOXPLOT_GRID_MARGINS: BoxplotGridMargins = {
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

// The astro document's expression summaries for rs2120461 in the eqtl_phenotype collection,
// ordered and labeled reference allele (T) first.
export const IMPACT_BOXPLOT_EXAMPLE_SUMMARIES: GenotypeExpressionSummary[] = [
  {
    genotype: 'TT',
    min: -2.4852,
    first_quartile: -0.989,
    median: -0.5503,
    third_quartile: 0.0085,
    max: 1.5046,
  },
  {
    genotype: 'TC',
    min: -1.3446,
    first_quartile: 0.0496,
    median: 0.4904,
    third_quartile: 0.9791,
    max: 2.3732,
  },
  {
    genotype: 'CC',
    min: -1.4006,
    first_quartile: 0.5802,
    median: 1.354,
    third_quartile: 1.9008,
    max: 3.8816,
  },
];
