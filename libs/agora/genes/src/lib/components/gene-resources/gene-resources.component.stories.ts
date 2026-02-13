import { geneMock1 } from '@sagebionetworks/agora/testing';
import { type Meta, type StoryObj } from '@storybook/angular';
import { GeneResourcesComponent } from './gene-resources.component';

const meta: Meta<GeneResourcesComponent> = {
  component: GeneResourcesComponent,
  title: 'Genes/GeneResources',
};
export default meta;
type Story = StoryObj<GeneResourcesComponent>;

export const GeneResources: Story = {
  args: {
    gene: geneMock1,
  },
};
