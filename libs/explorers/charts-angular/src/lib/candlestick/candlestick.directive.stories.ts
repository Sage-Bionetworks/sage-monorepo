import {
  CandlestickProps,
  candlestickItems,
  candlestickItemsColored,
} from '@sagebionetworks/explorers/charts';
import { Meta, StoryObj } from '@storybook/angular';
import { CandlestickDirective } from './candlestick.directive';

const meta: Meta<CandlestickDirective> = {
  component: CandlestickDirective,
  title: 'directives/sageCandlestick',
  argTypes: {
    xAxisLabelTooltipFormatter: { control: false },
    pointTooltipFormatter: { control: false },
  },
  render: (args: CandlestickProps) => ({
    props: args,
    template: `<div
      sageCandlestick
      [items]="items"
      [title]="title"
      [xAxisTitle]="xAxisTitle"
      [yAxisTitle]="yAxisTitle"
      [yAxisMin]="yAxisMin"
      [yAxisMax]="yAxisMax"
      [xAxisCategories]="xAxisCategories"
      [xAxisLabelTooltipFormatter]="xAxisLabelTooltipFormatter"
      [pointTooltipFormatter]="pointTooltipFormatter"
      [defaultLineColor]="defaultLineColor"
      [defaultPointColor]="defaultPointColor"
      [pointSize]="pointSize"
      [referenceLineValue]="referenceLineValue"
      [referenceLineColor]="referenceLineColor"
      [noDataStyle]="noDataStyle"
    ></div>`,
  }),
};
export default meta;
type Story = StoryObj<CandlestickDirective>;

export const NoData: Story = {
  args: {
    items: [],
  },
};

const NEUROPATH_NAMES: Record<string, string> = {
  BRAAK: 'Braak Stage (tau pathology)',
  CERAD: 'CERAD Score (neuritic plaques)',
  COGDX: 'Cognitive Diagnosis',
};

export const Default: Story = {
  args: {
    items: candlestickItems,
    xAxisTitle: 'Phenotype',
    yAxisTitle: 'ODDS RATIO',
    yAxisMin: 0,
    yAxisMax: 2,
    referenceLineValue: 1,
    xAxisLabelTooltipFormatter: (category: string) => NEUROPATH_NAMES[category] ?? category,
    pointTooltipFormatter: (item) => `Odds Ratio: ${item.value.toPrecision(3)}`,
  },
};

export const ColoredPerItem: Story = {
  args: {
    items: candlestickItemsColored,
    xAxisTitle: 'Phenotype',
    yAxisTitle: 'ODDS RATIO',
    yAxisMin: 0,
    yAxisMax: 2,
    referenceLineValue: 1,
    pointTooltipFormatter: (item) => `Odds Ratio: ${item.value.toPrecision(3)}`,
  },
};
