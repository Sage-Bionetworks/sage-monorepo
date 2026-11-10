import type { Meta, StoryObj } from '@storybook/angular';
import { DescriptionListComponent } from './description-list.component';

const meta: Meta<DescriptionListComponent> = {
  component: DescriptionListComponent,
  title: 'UI/DescriptionListComponent',
};
export default meta;
type Story = StoryObj<DescriptionListComponent>;

export const Demo: Story = {
  args: {
    items: [
      { term: 'Beta', description: '.98' },
      { term: 'P-Value', description: '1.25e-08' },
      { term: 'Variant Loc.', description: 'chr:11:54455—252455' },
      { term: 'Gene Loc.', description: 'chr:11:54455—252455' },
      { term: 'Cell Class', description: 'Astrocyte' },
    ],
  },
};

export const FixedTermWidth: Story = {
  args: Demo.args,
  render: (args) => ({
    props: args,
    template: `
      <explorers-description-list
        [items]="items"
        style="--description-list-term-width: 105px; width: 260px"
      />
    `,
  }),
};
