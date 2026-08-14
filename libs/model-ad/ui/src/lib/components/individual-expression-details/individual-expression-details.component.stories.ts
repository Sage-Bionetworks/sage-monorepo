import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import {
  proteomicsIndividualMocks,
  transcriptomicsIndividualMocks,
} from '@sagebionetworks/model-ad/testing';
import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular';
import { IndividualExpressionDetailsComponent } from './individual-expression-details.component';

const meta: Meta<IndividualExpressionDetailsComponent> = {
  component: IndividualExpressionDetailsComponent,
  title: 'UI/IndividualExpressionDetailsComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideLoadingIconColors(MODEL_AD_LOADING_ICON_COLORS),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<IndividualExpressionDetailsComponent>;

export const IndividualRnaExpression: Story = {
  args: {
    isLoading: false,
    data: transcriptomicsIndividualMocks,
    modality: 'RNA',
    tissue: transcriptomicsIndividualMocks[0].tissue,
    modelIdentifier: transcriptomicsIndividualMocks[0].name,
    downloadFilenamePrefix: 'transcriptomics_individual',
  },
};

export const IndividualProteinExpression: Story = {
  args: {
    isLoading: false,
    data: proteomicsIndividualMocks,
    modality: 'Protein',
    tissue: proteomicsIndividualMocks[0].tissue,
    modelIdentifier: proteomicsIndividualMocks[0].name,
    downloadFilenamePrefix: 'protein_expression_individual',
  },
};
