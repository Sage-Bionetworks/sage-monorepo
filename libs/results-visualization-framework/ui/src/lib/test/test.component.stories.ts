import type { Meta, StoryObj } from '@storybook/angular';
import { TestComponent } from './test.component';

const meta: Meta<TestComponent> = {
  component: TestComponent,
  title: 'TestComponent',
};
export default meta;
type Story = StoryObj<TestComponent>;

export const Demo: Story = {
  args: {
    inputText: 'Example Text',
  },
};
