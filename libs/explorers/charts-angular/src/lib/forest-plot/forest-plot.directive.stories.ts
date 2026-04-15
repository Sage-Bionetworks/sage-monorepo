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
      [showCILabels]="showCILabels"
      [noDataStyle]="noDataStyle"
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

export const ColoredByLine: Story = {
  args: {
    items: forestPlotItemsColoredByLine,
    showCILabels: true,
    xAxisTitle: 'LOG 2 FOLD CHANGE',
    yAxisLabelTooltipFormatter: (category: string) => TISSUE_NAMES[category] ?? category,
    pointTooltipFormatter: (item) => `Log Fold Change: ${item.value.toPrecision(3)}`,
  },
};
