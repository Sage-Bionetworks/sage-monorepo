import { VisualizationOverviewPane } from '@sagebionetworks/explorers/models';
import { provideComparisonToolService } from '@sagebionetworks/explorers/services';
import { mockComparisonToolDataConfig } from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { VisualizationOverviewPanelComponent } from './visualization-overview-panel.component';

const mockPanes: VisualizationOverviewPane[] = [
  {
    heading: 'Welcome to Visualization',
    content:
      '<p>This is the first pane content. It introduces users to the visualization features.</p>',
  },
  {
    heading: 'How to Use Filters',
    content:
      '<p>This pane explains how to use filters to refine your data.</p><ul><li>Select filters from the sidebar</li><li>Apply multiple filters at once</li><li>Clear filters to reset</li></ul>',
  },
  {
    heading: 'Export Your Data',
    content:
      '<p>Learn how to export your results:</p><ul><li>Click the export button</li><li>Choose your preferred format</li><li>Download the file</li></ul>',
  },
];

const meta: Meta<VisualizationOverviewPanelComponent> = {
  component: VisualizationOverviewPanelComponent,
  title: 'Comparison Tool/VisualizationOverviewPanelComponent',
  decorators: [
    applicationConfig({
      providers: [
        ...provideComparisonToolService({
          configs: mockComparisonToolDataConfig,
          viewConfig: {
            visualizationOverviewPanes: mockPanes,
          },
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

export const SinglePane: Story = {
  decorators: [
    applicationConfig({
      providers: [
        ...provideComparisonToolService({
          configs: mockComparisonToolDataConfig,
          viewConfig: {
            visualizationOverviewPanes: [
              {
                heading: 'Only Pane',
                content:
                  '<p>This is a single pane example. The Close button appears immediately.</p>',
              },
            ],
          },
        }),
      ],
    }),
  ],
  args: {},
};

export const ManyPanes: Story = {
  decorators: [
    applicationConfig({
      providers: [
        ...provideComparisonToolService({
          configs: mockComparisonToolDataConfig,
          viewConfig: {
            visualizationOverviewPanes: [
              {
                heading: 'Getting Started',
                content: '<p>Welcome! This guide has many steps.</p>',
              },
              {
                heading: 'Step 2',
                content: '<p>Navigate through the panes using the Back and Next buttons.</p>',
              },
              {
                heading: 'Step 3',
                content: '<p>Each pane can contain different content.</p>',
              },
              {
                heading: 'Step 4',
                content: '<p>HTML content is supported in each pane.</p>',
              },
              {
                heading: 'Final Step',
                content: '<p>Close the dialog or check "Don\'t show this again" to hide it.</p>',
              },
            ],
          },
        }),
      ],
    }),
  ],
  args: {},
};
