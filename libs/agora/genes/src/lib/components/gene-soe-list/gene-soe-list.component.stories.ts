import { geneMock1 } from '@sagebionetworks/agora/testing';
import { type Meta, type StoryObj } from '@storybook/angular';
import { GeneSoeListComponent } from './gene-soe-list.component';

const meta: Meta<GeneSoeListComponent> = {
  component: GeneSoeListComponent,
  title: 'Genes/GeneSoeList',
};
export default meta;
type Story = StoryObj<GeneSoeListComponent>;

export const GeneSoeList: Story = {
  args: {
    gene: geneMock1,
  },
};
