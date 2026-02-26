import { type Meta, type StoryObj } from '@storybook/angular';
import { NominationFormComponent } from './nomination-form.component';

const meta: Meta<NominationFormComponent> = {
  component: NominationFormComponent,
  title: 'Pages/NominationForm',
};
export default meta;
type Story = StoryObj<NominationFormComponent>;

export const NominationForm: Story = {};
