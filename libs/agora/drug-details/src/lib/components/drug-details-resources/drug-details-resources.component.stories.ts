import { drugMock } from '@sagebionetworks/agora/testing';
import { type Meta, type StoryObj } from '@storybook/angular';
import { DrugDetailsResourcesComponent } from './drug-details-resources.component';

const meta: Meta<DrugDetailsResourcesComponent> = {
  component: DrugDetailsResourcesComponent,
  title: 'DrugDetails/DrugDetailsResources',
};
export default meta;
type Story = StoryObj<DrugDetailsResourcesComponent>;

export const DrugDetailsResources: Story = {
  args: {
    drug: drugMock,
  },
};
