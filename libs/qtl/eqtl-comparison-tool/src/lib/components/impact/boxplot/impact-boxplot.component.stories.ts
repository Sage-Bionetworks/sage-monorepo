import type { Meta, StoryObj } from '@storybook/angular';
import { ImpactBoxplotComponent } from './impact-boxplot.component';
import { IMPACT_BOXPLOT_EXAMPLE_SUMMARIES } from './impact-boxplot.constants';

const meta: Meta<ImpactBoxplotComponent> = {
  component: ImpactBoxplotComponent,
  title: 'eQTL Comparison Tool/ImpactBoxplotComponent',
};
export default meta;
type Story = StoryObj<ImpactBoxplotComponent>;

export const Demo: Story = {
  args: {
    cellSubclass: 'Astrocyte',
    expressionSummaries: IMPACT_BOXPLOT_EXAMPLE_SUMMARIES,
  },
};

export const LongSubclassName: Story = {
  args: {
    cellSubclass: 'Layer 3-5 Intratelencephalic Excitatory Neuron 1',
    expressionSummaries: IMPACT_BOXPLOT_EXAMPLE_SUMMARIES,
  },
};
