import type { Meta, StoryObj } from '@storybook/angular';
import { LegendComponent } from './legend.component';

const meta: Meta<LegendComponent> = {
  component: LegendComponent,
  title: 'Comparison Tool/Legend',
};
export default meta;
type Story = StoryObj<LegendComponent>;

export const Legend: Story = {
  args: {
    colorChartLowerLabel: 'Downregulated',
    colorChartUpperLabel: 'Upregulated',
    colorChartText:
      'Circle color indicates the log2 fold change value. Red shades indicate reduced expression levels in AD patients compared to controls, while blue shades indicate increased expression levels in AD patients relative to controls.',
    sizeChartLowerLabel: 'Significant',
    sizeChartUpperLabel: 'Insignificant',
    sizeChartText:
      'Circle diameter indicates P-value. Larger circles indicate higher statistical significance, while smaller circles indicate lower statistical significance.',
  },
};
