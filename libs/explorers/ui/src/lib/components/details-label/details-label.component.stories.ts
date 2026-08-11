import type { Meta, StoryObj } from '@storybook/angular';
import { DetailsLabelComponent } from './details-label.component';

const meta: Meta<DetailsLabelComponent> = {
  component: DetailsLabelComponent,
  title: 'UI/DetailsLabelComponent',
};
export default meta;
type Story = StoryObj<DetailsLabelComponent>;

export const DetailsLabel: Story = {
  args: {
    left: 'Trem2',
    right: 'ENSMUSG00000023992',
  },
};
