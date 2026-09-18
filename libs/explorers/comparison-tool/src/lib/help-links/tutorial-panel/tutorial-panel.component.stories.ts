import { provideComparisonToolService } from '@sagebionetworks/explorers/services';
import { mockComparisonToolDataConfig } from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { MessageService } from 'primeng/api';
import { TutorialPanelComponent } from './tutorial-panel.component';

const meta: Meta<TutorialPanelComponent> = {
  component: TutorialPanelComponent,
  title: 'Comparison Tool/TutorialPanelComponent',
  decorators: [
    applicationConfig({
      providers: [
        MessageService,
        ...provideComparisonToolService({
          configs: mockComparisonToolDataConfig,
          tutorialVisibility: true,
        }),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<TutorialPanelComponent>;

export const Demo: Story = {
  args: {},
};
