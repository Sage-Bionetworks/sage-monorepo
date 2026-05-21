import {
  forestPlotItems,
  forestPlotItemsColoredByLine,
  ForestPlotProps,
} from '@sagebionetworks/explorers/charts';
import { Meta, StoryObj } from '@storybook/angular';
import { ForestPlotDirective } from './forest-plot.directive';

const meta: Meta<ForestPlotDirective> = {
  component: ForestPlotDirective,
  title: 'directives/sageForestPlot',
  argTypes: {
    yAxisLabelTooltipFormatter: { control: false },
    pointTooltipFormatter: { control: false },
    ciLabelFormatter: { control: false },
  },
  render: (args: ForestPlotProps) => ({
    props: args,
    template: `<div
      sageForestPlot
      [items]="items"
      [title]="title"
      [xAxisTitle]="xAxisTitle"
      [xAxisMin]="xAxisMin"
      [xAxisMax]="xAxisMax"
      [yAxisCategories]="yAxisCategories"
      [yAxisLabelTooltipFormatter]="yAxisLabelTooltipFormatter"
      [pointTooltipFormatter]="pointTooltipFormatter"
      [ciLabelFormatter]="ciLabelFormatter"
      [defaultLineColor]="defaultLineColor"
      [defaultPointColor]="defaultPointColor"
      [pointSize]="pointSize"
      [ciLineWidth]="ciLineWidth"
      [showCILabels]="showCILabels"
      [noDataStyle]="noDataStyle"
      [showZeroLine]="showZeroLine"
      [xAxisInterval]="xAxisInterval"
      [xAxisLabelPrecision]="xAxisLabelPrecision"
      [xAxisLineStyle]="xAxisLineStyle"
      [yAxisLineStyle]="yAxisLineStyle"
      [xAxisTickLabelStyle]="xAxisTickLabelStyle"
      [yAxisTickLabelStyle]="yAxisTickLabelStyle"
      [showXAxisLabelsOnTop]="showXAxisLabelsOnTop"
      [xAxisGridLineStyle]="xAxisGridLineStyle"
      [yAxisGridLineStyle]="yAxisGridLineStyle"
      [rowHoverHighlightStyle]="rowHoverHighlightStyle"
      [tooltipStyle]="tooltipStyle"
    ></div>`,
  }),
};
export default meta;
type Story = StoryObj<ForestPlotDirective>;

export const NoData: Story = {
  args: {
    items: [],
  },
};

const TISSUE_NAMES: Record<string, string> = {
  ACC: 'Anterior Cingulate Cortex',
  CBE: 'Cerebellum',
  DLPFC: 'Dorsolateral Prefrontal Cortex',
  FP: 'Frontal Pole',
  IFG: 'Inferior Frontal Gyrus',
  PHG: 'Parahippocampal Gyrus',
  STG: 'Superior Temporal Gyrus',
  TCX: 'Temporal Cortex',
};

export const Default: Story = {
  args: {
    items: forestPlotItems,
    showCILabels: true,
    xAxisTitle: 'LOG 2 FOLD CHANGE',
    yAxisLabelTooltipFormatter: (category: string) => TISSUE_NAMES[category] ?? category,
    pointTooltipFormatter: (item) => `Log Fold Change: ${item.value.toPrecision(3)}`,
  },
};

export const Styled: Story = {
  args: {
    items: forestPlotItemsColoredByLine,
    xAxisMin: -0.1,
    xAxisMax: 0.7,
    showCILabels: false,
    pointSize: 11,
    ciLineWidth: 1,
    pointTooltipFormatter: (item) => `Effect size: ${item.value.toPrecision(2)}`,
    showZeroLine: false,
    xAxisInterval: 0.2,
    xAxisLabelPrecision: 1,
    xAxisLineStyle: { width: 1, color: '#22252A' },
    yAxisLineStyle: { width: 1, color: '#22252A' },
    xAxisTickLabelStyle: { fontSize: '18px', fontWeight: 400, color: '#4A5056' },
    yAxisTickLabelStyle: { fontSize: '18px', fontWeight: 400, color: '#4A5056' },
    showXAxisLabelsOnTop: true,
    xAxisGridLineStyle: { width: 1, color: '#D0D4D9', type: 'dotted' },
    yAxisGridLineStyle: { width: 1, color: '#F1F2F4', type: 'solid' },
    rowHoverHighlightStyle: { backgroundColor: 'rgba(158, 158, 158, 0.15)', thickness: 13 },
    tooltipStyle: { backgroundColor: '#22252A' },
  },
};
