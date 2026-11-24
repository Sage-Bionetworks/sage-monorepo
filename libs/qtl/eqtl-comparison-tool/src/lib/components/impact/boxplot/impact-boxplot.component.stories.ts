import { eqtlPhenotypeMock } from '@sagebionetworks/qtl/testing';
import { resolveCellSubclass } from '@sagebionetworks/qtl/util';
import type { Meta, StoryObj } from '@storybook/angular';
import { ImpactBoxplotComponent } from './impact-boxplot.component';

const meta: Meta<ImpactBoxplotComponent> = {
  component: ImpactBoxplotComponent,
  title: 'eQTL Comparison Tool/ImpactBoxplotComponent',
};
export default meta;
type Story = StoryObj<ImpactBoxplotComponent>;

export const Demo: Story = {
  args: {
    cellSubclass: resolveCellSubclass('astro'),
    expressionSummaries: eqtlPhenotypeMock.expression_summaries,
  },
};

export const LongSubclassName: Story = {
  args: {
    cellSubclass: resolveCellSubclass('enl35it1'),
    expressionSummaries: eqtlPhenotypeMock.expression_summaries,
  },
};

export const Loading: Story = {
  args: {
    cellSubclass: resolveCellSubclass('astro'),
    expressionSummaries: [],
    isLoading: true,
  },
};

export const NoData: Story = {
  args: {
    cellSubclass: resolveCellSubclass('astro'),
    expressionSummaries: [],
  },
};
