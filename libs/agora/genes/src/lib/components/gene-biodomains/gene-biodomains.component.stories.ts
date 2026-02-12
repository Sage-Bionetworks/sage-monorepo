import { geneMock1 } from '@sagebionetworks/agora/testing';
import { type Meta, type StoryObj } from '@storybook/angular';
import { GeneBioDomainsComponent } from './gene-biodomains.component';

const meta: Meta<GeneBioDomainsComponent> = {
  component: GeneBioDomainsComponent,
  title: 'Genes/GeneBioDomains',
};
export default meta;
type Story = StoryObj<GeneBioDomainsComponent>;

export const GeneBioDomains: Story = {
  args: {
    gene: geneMock1,
  },
};
