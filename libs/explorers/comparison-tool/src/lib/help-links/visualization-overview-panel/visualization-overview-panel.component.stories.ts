import { provideComparisonToolService } from '@sagebionetworks/explorers/services';
import { mockComparisonToolDataConfig } from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { MessageService } from 'primeng/api';
import { VisualizationOverviewPanelComponent } from './visualization-overview-panel.component';

const meta: Meta<VisualizationOverviewPanelComponent> = {
  component: VisualizationOverviewPanelComponent,
  title: 'Comparison Tool/VisualizationOverviewPanelComponent',
  decorators: [
    applicationConfig({
      providers: [
        MessageService,
        ...provideComparisonToolService({
          configs: mockComparisonToolDataConfig,
          visualizationOverviewVisibility: true,
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
