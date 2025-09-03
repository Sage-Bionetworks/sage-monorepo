import { LegendProps, mockPointStyles } from '@sagebionetworks/shared/charts';
import { Meta, StoryObj } from '@storybook/angular';
import { LegendDirective } from './legend.directive';

const meta: Meta<LegendDirective> = {
  component: LegendDirective,
  title: 'directives/sageLegend',
  render: (args: LegendProps) => ({
    props: args,
    template: `<div sageLegend [pointStyles]="pointStyles"></div>`,
  }),
};
export default meta;
type Story = StoryObj<LegendDirective>;

export const NoData: Story = {
  args: {
    pointStyles: [],
  },
};

export const Demo: Story = {
  args: {
    pointStyles: mockPointStyles,
  },
};
