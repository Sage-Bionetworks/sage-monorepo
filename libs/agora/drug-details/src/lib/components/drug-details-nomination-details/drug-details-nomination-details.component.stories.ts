import { drugMock } from '@sagebionetworks/agora/testing';
import { type Meta, type StoryObj } from '@storybook/angular';
import { DrugDetailsNominationDetailsComponent } from './drug-details-nomination-details.component';

const meta: Meta<DrugDetailsNominationDetailsComponent> = {
  component: DrugDetailsNominationDetailsComponent,
  title: 'DrugDetails/DrugDetailsNominationDetails',
};
export default meta;
type Story = StoryObj<DrugDetailsNominationDetailsComponent>;

export const DrugDetailsNominationDetails: Story = {
  args: {
    drug: drugMock,
  },
};
