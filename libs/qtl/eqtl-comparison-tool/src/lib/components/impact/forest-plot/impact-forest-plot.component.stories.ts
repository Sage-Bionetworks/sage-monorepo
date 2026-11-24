import { coloredCellClassEffectSizesMock } from '@sagebionetworks/qtl/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { ImpactForestPlotComponent } from './impact-forest-plot.component';

const meta: Meta<ImpactForestPlotComponent> = {
  component: ImpactForestPlotComponent,
  title: 'eQTL Comparison Tool/ImpactForestPlotComponent',
};
export default meta;
type Story = StoryObj<ImpactForestPlotComponent>;

export const Demo: Story = {
  args: {
    selectedCellClassId: 'class_astro',
    effectSizes: coloredCellClassEffectSizesMock,
  },
};

export const ImmuneCellSelected: Story = {
  args: {
    selectedCellClassId: 'class_immune',
    effectSizes: coloredCellClassEffectSizesMock,
  },
};
