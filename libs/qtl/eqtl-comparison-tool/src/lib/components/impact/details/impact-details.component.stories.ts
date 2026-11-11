import type { Meta, StoryObj } from '@storybook/angular';
import { ImpactDetailsComponent } from './impact-details.component';
import { IMPACT_DETAILS_EXAMPLE } from './impact-details.constants';

const meta: Meta<ImpactDetailsComponent> = {
  component: ImpactDetailsComponent,
  title: 'eQTL Comparison Tool/ImpactDetailsComponent',
};
export default meta;
type Story = StoryObj<ImpactDetailsComponent>;

export const Demo: Story = {
  args: {
    details: IMPACT_DETAILS_EXAMPLE,
  },
};
