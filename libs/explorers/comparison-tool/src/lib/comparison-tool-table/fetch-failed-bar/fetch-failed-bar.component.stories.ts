import type { Meta, StoryObj } from '@storybook/angular';
import { FetchFailedBarComponent } from './fetch-failed-bar.component';

const meta: Meta<FetchFailedBarComponent> = {
  component: FetchFailedBarComponent,
  title: 'Comparison Tool/ComparisonToolTable/FetchFailedBarComponent',
};
export default meta;
type Story = StoryObj<FetchFailedBarComponent>;

export const Default: Story = {
  args: {
    message: 'We encountered a problem loading your pinned proteins.',
    buttonLabel: 'Retry Loading Pins',
    loading: false,
  },
};

export const Loading: Story = {
  args: {
    ...Default.args,
    loading: true,
  },
};
