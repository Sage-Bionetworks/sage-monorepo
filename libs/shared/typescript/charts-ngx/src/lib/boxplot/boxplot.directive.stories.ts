import {
  BoxplotProps,
  CategoryPoint,
  dynamicBoxplotPoints,
  staticBoxplotPoints,
  staticBoxplotSummaries,
} from '@sagebionetworks/shared/charts';
import { Meta, StoryObj } from '@storybook/angular';
import { BoxplotDirective } from './boxplot.directive';

const meta: Meta<BoxplotDirective> = {
  component: BoxplotDirective,
  title: 'directives/sageBoxplot',
  argTypes: {
    pointTooltipFormatter: {
      control: { type: 'function' },
    },
  },
  render: (args: BoxplotProps) => ({
    props: {
      points: args.points,
      summaries: args.summaries,
      title: args.title,
      xAxisTitle: args.xAxisTitle,
      yAxisTitle: args.yAxisTitle,
      yAxisMin: args.yAxisMin,
      yAxisMax: args.yAxisMax,
      xAxisCategoryToTooltipText: args.xAxisCategoryToTooltipText,
      pointTooltipFormatter: args.pointTooltipFormatter,
    },
    template: `<div sageBoxplot [points]="points" [summaries]="summaries" [title]="title" [xAxisTitle]="xAxisTitle" [yAxisTitle]="yAxisTitle" [yAxisMin]="yAxisMin" [yAxisMax]="yAxisMax" [xAxisCategoryToTooltipText]="xAxisCategoryToTooltipText" [pointTooltipFormatter]="pointTooltipFormatter"></div>`,
  }),
};
export default meta;
type Story = StoryObj<BoxplotDirective>;

export const NoData: Story = {
  args: {
    points: [],
  },
};

export const StaticSummary: Story = {
  args: {
    points: staticBoxplotPoints,
    summaries: staticBoxplotSummaries,
    xAxisCategoryToTooltipText: {
      CAT1: 'Category 1',
      CAT2: 'Category 2',
      CAT3: 'Category 3',
      CAT4: 'Category 4',
    },
    xAxisTitle: 'BRAIN REGION',
    yAxisTitle: 'LOG 2 FOLD CHANGE',
    yAxisMax: 100,
    yAxisMin: -100,
    title: 'AD Diagnosis (males and females)',
    pointTooltipFormatter: (pt: CategoryPoint) => {
      return `Value: ${pt.value}`;
    },
  },
};

export const DynamicSummary: Story = {
  args: {
    points: dynamicBoxplotPoints,
    xAxisTitle: 'MODEL',
    yAxisTitle: '#objects/sqmm',
    pointTooltipFormatter: (pt: CategoryPoint) =>
      `${pt.pointCategory}: ${pt.value}`,
  },
};
