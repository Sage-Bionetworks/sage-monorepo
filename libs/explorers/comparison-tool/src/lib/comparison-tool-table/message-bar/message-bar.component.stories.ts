import type { Meta, StoryObj } from '@storybook/angular';
import { MessageBarComponent } from './message-bar.component';

const meta: Meta<MessageBarComponent> = {
  component: MessageBarComponent,
  title: 'Comparison Tool/ComparisonToolTable/MessageBarComponent',
};
export default meta;
type Story = StoryObj<MessageBarComponent>;

export const Warn: Story = {
  args: {
    message: 'We encountered a problem loading your pinned proteins.',
    severity: 'warn',
    buttonLabel: 'Retry Loading Pins',
    loading: false,
  },
};

export const WarnLoading: Story = {
  args: {
    ...Warn.args,
    loading: true,
  },
};

export const Info: Story = {
  args: {
    message: 'No matching proteins exist in the current view.',
    severity: 'info',
    buttonLabel: null,
  },
};
