import { Meta, StoryObj } from '@storybook/angular';
import { BoxplotDirective } from './boxplot.directive';

const meta: Meta<BoxplotDirective> = {
  component: BoxplotDirective,
  title: 'directives/sageBoxplot',
  tags: ['autodocs'],
  render: (args) => ({
    props: args,
    template: `<p sageBoxplot [text]="'${args.text}'">{{ text }}</p>`,
  }),
  argTypes: {
    text: { control: 'text', description: 'text to display' },
  },
};
export default meta;
type Story = StoryObj<BoxplotDirective>;

export const Default: Story = {
  args: {
    text: 'Hello, World!',
  },
};
