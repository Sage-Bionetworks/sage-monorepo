import { provideHttpClient } from '@angular/common/http';
import { modelMock } from '@sagebionetworks/model-ad/testing';
import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular';
import { ModelDetailsBoxplotsSelectorComponent } from './model-details-boxplots-selector.component';

const meta: Meta<ModelDetailsBoxplotsSelectorComponent> = {
  component: ModelDetailsBoxplotsSelectorComponent,
  title: 'Model Details/ModelDetailsBoxplotsSelectorComponent',
  decorators: [
    applicationConfig({
      providers: [provideHttpClient()],
    }),
  ],
};
export default meta;
type Story = StoryObj<ModelDetailsBoxplotsSelectorComponent>;

export const Demo: Story = {
  args: {
    title: 'Biomarkers',
    modelName: modelMock.name,
    modelControls: modelMock.matched_controls,
    modelDataList: modelMock.biomarkers,
    wikiParams: { ownerId: 'syn66271427', wikiId: '632871' },
  },
};
