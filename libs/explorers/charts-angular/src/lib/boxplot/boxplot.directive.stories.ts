import {
  BoxplotProps,
  CategoryPoint,
  dynamicBoxplotPoints,
  staticBoxplotPoints,
  staticBoxplotSummaries,
} from '@sagebionetworks/explorers/charts';
import { Meta, StoryObj } from '@storybook/angular';
import { CallbackDataParams } from 'echarts/types/dist/shared';
import { BoxplotDirective } from './boxplot.directive';

const meta: Meta<BoxplotDirective> = {
  component: BoxplotDirective,
  title: 'directives/sageBoxplot',
  argTypes: {
    xAxisLabelFormatter: { control: false },
    xAxisLabelTooltipFormatter: { control: false },
    pointTooltipFormatter: { control: false },
  },
  render: (args: BoxplotProps) => ({
    props: args,
    template: `<div sageBoxplot [points]="points" [summaries]="summaries" [title]="title" [xAxisTitle]="xAxisTitle" [xAxisLabelFormatter]="xAxisLabelFormatter" [xAxisCategories]="xAxisCategories" [yAxisTitle]="yAxisTitle" [yAxisMin]="yAxisMin" [yAxisMax]="yAxisMax" [capPointsAboveYAxisMax]="capPointsAboveYAxisMax" [xAxisLabelTooltipFormatter]="xAxisLabelTooltipFormatter" [pointTooltipFormatter]="pointTooltipFormatter" [pointCategoryColors]="pointCategoryColors" [pointCategoryShapes]="pointCategoryShapes" [showLegend]="showLegend" [pointOpacity]="pointOpacity" [noDataStyle]="noDataStyle" [chartStyle]="chartStyle" [laneBackgroundColors]="laneBackgroundColors" [showAxisTicks]="showAxisTicks" [boxplotBoxStyle]="boxplotBoxStyle" [axisTickLabelStyle]="axisTickLabelStyle" [axisLineStyle]="axisLineStyle" [tooltipStyle]="tooltipStyle"></div>`,
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
    xAxisLabelTooltipFormatter: (params: CallbackDataParams) => {
      const catMap: Record<string, string> = {
        CAT1: 'Category 1',
        CAT2: 'Category 2',
        CAT3: 'Category 3',
        CAT4: 'Category 4',
      };
      return catMap[params.name] || 'Label not found';
    },
    xAxisTitle: 'BRAIN REGION',
    yAxisTitle: 'LOG2 FOLD CHANGE',
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
    xAxisCategories: ['Experimental', 'Control2', 'Control1'],
    pointTooltipFormatter: (pt: CategoryPoint, params: CallbackDataParams) =>
      `${pt.pointCategory}: ${pt.value}`,
    pointCategoryColors: { Male: 'yellow', Female: 'green' },
    pointCategoryShapes: { Male: 'circle', Female: 'triangle' },
    showLegend: true,
    pointOpacity: 0.5,
    noDataStyle: 'grayBackground',
    chartStyle: 'grayGrid',
  },
};

export const CappedPoints: Story = {
  args: {
    ...DynamicSummary.args,
    points: [
      ...['Experimental', 'Control2', 'Control1'].flatMap((xAxisCategory, i) =>
        ['Female', 'Male'].flatMap((pointCategory, j) =>
          Array.from({ length: 15 }, (_, k) => ({
            xAxisCategory,
            pointCategory,
            value: 300 + ((k * 37 + i * 53 + j * 71) % 500),
          })),
        ),
      ),
      { xAxisCategory: 'Experimental', value: 3100, pointCategory: 'Female' },
      { xAxisCategory: 'Experimental', value: 3600, pointCategory: 'Female' },
      { xAxisCategory: 'Experimental', value: 4200, pointCategory: 'Male' },
      { xAxisCategory: 'Control2', value: 3500, pointCategory: 'Male' },
    ],
    yAxisMax: 1300,
    capPointsAboveYAxisMax: true,
  },
};

export const Styled: Story = {
  args: {
    summaries: staticBoxplotSummaries,
    points: [],
    pointTooltipFormatter: (pt: CategoryPoint) => `${pt.pointCategory}: ${pt.value}`,
    laneBackgroundColors: ['#F9F9FA', '#FFFFFF'],
    showAxisTicks: false,
    boxplotBoxStyle: {
      fillColor: 'rgba(210, 175, 129, 0.5)',
      borderColor: '#22252A',
      borderWidth: 1,
    },
    axisTickLabelStyle: { fontSize: '12px', fontWeight: 400, color: '#4A5056' },
    axisLineStyle: { width: 1, color: '#000' },
    tooltipStyle: { backgroundColor: '#22252A' },
  },
};
