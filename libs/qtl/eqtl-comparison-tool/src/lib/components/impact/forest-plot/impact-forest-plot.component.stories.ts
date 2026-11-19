import type { Meta, StoryObj } from '@storybook/angular';
import { ImpactForestPlotComponent } from './impact-forest-plot.component';
import { IMPACT_FOREST_PLOT_EXAMPLE_EFFECT_SIZES } from './impact-forest-plot.constants';

const meta: Meta<ImpactForestPlotComponent> = {
  component: ImpactForestPlotComponent,
  title: 'eQTL Comparison Tool/ImpactForestPlotComponent',
};
export default meta;
type Story = StoryObj<ImpactForestPlotComponent>;

export const Demo: Story = {
  args: {
    selectedCellClass: 'Astrocyte',
    effectSizes: IMPACT_FOREST_PLOT_EXAMPLE_EFFECT_SIZES,
  },
};

export const ImmuneCellSelected: Story = {
  args: {
    selectedCellClass: 'Immune Cell',
    effectSizes: IMPACT_FOREST_PLOT_EXAMPLE_EFFECT_SIZES,
  },
};
