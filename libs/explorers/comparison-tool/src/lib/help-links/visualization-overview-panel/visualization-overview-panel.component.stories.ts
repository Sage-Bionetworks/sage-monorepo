import { provideComparisonToolService } from '@sagebionetworks/explorers/services';
import { mockComparisonToolDataConfig } from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { VisualizationOverviewPanelComponent } from './visualization-overview-panel.component';

const meta: Meta<VisualizationOverviewPanelComponent> = {
  component: VisualizationOverviewPanelComponent,
  title: 'Comparison Tool/VisualizationOverviewPanelComponent',
  decorators: [
    applicationConfig({
      providers: [
        ...provideComparisonToolService({
          configs: mockComparisonToolDataConfig,
        }),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<VisualizationOverviewPanelComponent>;

export const Demo: Story = {
  args: {},
};
