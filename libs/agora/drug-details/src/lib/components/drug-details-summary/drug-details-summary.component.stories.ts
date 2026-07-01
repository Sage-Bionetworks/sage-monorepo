import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { drugMock } from '@sagebionetworks/agora/testing';
import { applicationConfig } from '@storybook/angular';
import { type Meta, type StoryObj } from '@storybook/angular';
import { DrugDetailsSummaryComponent } from './drug-details-summary.component';

const meta: Meta<DrugDetailsSummaryComponent> = {
  component: DrugDetailsSummaryComponent,
  title: 'DrugDetails/DrugDetailsSummary',
  decorators: [
    applicationConfig({
      providers: [provideRouter([]), provideLocationMocks()],
    }),
  ],
};
export default meta;
type Story = StoryObj<DrugDetailsSummaryComponent>;

export const DrugDetailsSummary: Story = {
  args: {
    drug: drugMock,
  },
};
