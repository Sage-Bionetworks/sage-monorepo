import { geneMock1 } from '@sagebionetworks/agora/testing';
import { type Meta, type StoryObj } from '@storybook/angular';
import { GeneSoeComponent } from './gene-soe.component';

const meta: Meta<GeneSoeComponent> = {
  component: GeneSoeComponent,
  title: 'Genes/GeneSoe',
};
export default meta;
type Story = StoryObj<GeneSoeComponent>;

export const GeneSoe: Story = {
  args: {
    gene: geneMock1,
  },
};
