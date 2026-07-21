import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { mouseModelMock } from '@sagebionetworks/model-ad/testing';
import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular';
import { MouseModelDetailsBoxplotsSelectorComponent } from './mouse-model-details-boxplots-selector.component';

const meta: Meta<MouseModelDetailsBoxplotsSelectorComponent> = {
  component: MouseModelDetailsBoxplotsSelectorComponent,
  title: 'Model Details/MouseModelDetailsBoxplotsSelectorComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<MouseModelDetailsBoxplotsSelectorComponent>;

export const MouseBiomarkers: Story = {
  args: {
    title: 'Biomarkers',
    modelName: mouseModelMock.name,
    modelControls: mouseModelMock.matched_controls,
    modelDataList: mouseModelMock.biomarkers,
    wikiParams: { ownerId: 'syn66271427', wikiId: '632871' },
  },
};

export const MousePathology: Story = {
  args: {
    title: 'Pathology',
    modelName: mouseModelMock.name,
    modelControls: mouseModelMock.matched_controls,
    modelDataList: mouseModelMock.pathology,
    wikiParams: { ownerId: 'syn66271427', wikiId: '632872' },
  },
};
