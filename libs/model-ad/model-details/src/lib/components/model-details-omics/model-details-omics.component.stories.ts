import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { mouseModelMock } from '@sagebionetworks/model-ad/testing';
import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular';
import { ModelDetailsOmicsComponent } from './model-details-omics.component';

const transcriptomicsLink = `comparison/expression?name=${mouseModelMock.name}`;
const diseaseCorrelationLink = `comparison/correlation?name=${mouseModelMock.name}`;
const proteomicsLink = `comparison/expression?categories=PROTEIN%2520-%2520DIFFERENTIAL%2520EXPRESSION&models=${mouseModelMock.name}`;

const meta: Meta<ModelDetailsOmicsComponent> = {
  component: ModelDetailsOmicsComponent,
  title: 'Model Details/ModelDetailsOmicsComponent',
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
type Story = StoryObj<ModelDetailsOmicsComponent>;

export const AllOmicsAvailable: Story = {
  args: {
    model: {
      ...mouseModelMock,
      transcriptomics: transcriptomicsLink,
      disease_correlation: diseaseCorrelationLink,
      proteomics: proteomicsLink,
    },
  },
};

export const WithoutProteomics: Story = {
  args: {
    model: {
      ...mouseModelMock,
      transcriptomics: transcriptomicsLink,
      disease_correlation: diseaseCorrelationLink,
      proteomics: null,
    },
  },
};

export const ProteomicsOnly: Story = {
  args: {
    model: {
      ...mouseModelMock,
      transcriptomics: null,
      disease_correlation: null,
      proteomics: proteomicsLink,
    },
  },
};
