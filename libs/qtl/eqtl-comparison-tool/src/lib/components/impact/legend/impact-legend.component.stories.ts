import type { Meta, StoryObj } from '@storybook/angular';
import { ImpactLegendComponent } from './impact-legend.component';

const meta: Meta<ImpactLegendComponent> = {
  component: ImpactLegendComponent,
  title: 'eQTL Comparison Tool/ImpactLegendComponent',
};
export default meta;
type Story = StoryObj<ImpactLegendComponent>;

export const Demo: Story = {
  args: {
    visible: true,
  },
};
