import {
  BoxplotProps,
  CategoryPoint,
  dynamicBoxplotPoints,
  staticBoxplotPoints,
  staticBoxplotSummaries,
} from '@sagebionetworks/shared/charts';
import { Meta, StoryObj } from '@storybook/angular';
import { CallbackDataParams } from 'echarts/types/dist/shared';
import { BoxplotDirective } from './boxplot.directive';

const meta: Meta<BoxplotDirective> = {
  component: BoxplotDirective,
  title: 'directives/sageBoxplot',
  argTypes: {
    pointTooltipFormatter: { control: false },
  },
  render: (args: BoxplotProps) => ({
    props: args,
    template: `<div sageBoxplot [points]="points" [summaries]="summaries" [title]="title" [xAxisTitle]="xAxisTitle" [yAxisTitle]="yAxisTitle" [yAxisMin]="yAxisMin" [yAxisMax]="yAxisMax" [xAxisCategoryToTooltipText]="xAxisCategoryToTooltipText" [pointTooltipFormatter]="pointTooltipFormatter" [pointCategoryColors]="pointCategoryColors" [pointCategoryShapes]="pointCategoryShapes" [showLegend]="showLegend" [pointOpacity]="pointOpacity"></div>`,
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
    pointTooltipFormatter: (pt: CategoryPoint, params: CallbackDataParams) => {
      return `Value: ${pt.value}`;
    },
  },
};

export const DynamicSummary: Story = {
  args: {
    points: dynamicBoxplotPoints,
    xAxisTitle: 'MODEL',
    yAxisTitle: 'Insoluble A&beta;40 #objects/sqmm',
    pointTooltipFormatter: (pt: CategoryPoint, params: CallbackDataParams) =>
      `${pt.pointCategory}: ${pt.value}`,
    pointCategoryColors: { Male: 'yellow', Female: 'green' },
    pointCategoryShapes: { Male: 'circle', Female: 'triangle' },
    showLegend: true,
    pointOpacity: 0.5,
  },
};
