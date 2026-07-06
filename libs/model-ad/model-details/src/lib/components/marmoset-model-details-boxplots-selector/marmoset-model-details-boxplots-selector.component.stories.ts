import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { marmosetModelDataMock } from '@sagebionetworks/model-ad/testing';
import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular';
import { MarmosetModelDetailsBoxplotsSelectorComponent } from './marmoset-model-details-boxplots-selector.component';

const meta: Meta<MarmosetModelDetailsBoxplotsSelectorComponent> = {
  component: MarmosetModelDetailsBoxplotsSelectorComponent,
  title: 'Model Details/MarmosetModelDetailsBoxplotsSelectorComponent',
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
type Story = StoryObj<MarmosetModelDetailsBoxplotsSelectorComponent>;

export const PlasmaMarmoset: Story = {
  args: {
    title: 'Plasma Biomarkers',
    modelName: 'PSEN1',
    modelDataList: marmosetModelDataMock,
    wikiParams: { ownerId: 'syn66271427', wikiId: '641870' },
  },
};
