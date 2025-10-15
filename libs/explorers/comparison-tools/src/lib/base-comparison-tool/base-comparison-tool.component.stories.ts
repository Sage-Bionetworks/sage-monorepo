import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import {
  provideComparisonToolFilterService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolConfigs,
  provideLoadingIconColors,
} from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { BaseComparisonToolComponent } from './base-comparison-tool.component';

const meta: Meta<BaseComparisonToolComponent> = {
  component: BaseComparisonToolComponent,
  title: 'Comparison Tools/BaseComparisonToolComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideComparisonToolService(),
        ...provideComparisonToolFilterService(),
        ...provideLoadingIconColors(),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<BaseComparisonToolComponent>;

export const Demo: Story = {
  args: {
    isLoading: false,
    resultsCount: 1000,
    headerTitle: 'Gene Comparison',
    filterResultsButtonTooltip: 'Filter the results based on the selected criteria',
    pageConfigs: mockComparisonToolConfigs,
  },
};
