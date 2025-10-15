import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';
import { mockComparisonToolConfigFilters } from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { BaseComparisonToolComponent } from './base-comparison-tool.component';

const meta: Meta<BaseComparisonToolComponent> = {
  component: BaseComparisonToolComponent,
  title: 'Comparison Tools/BaseComparisonToolComponent',
  decorators: [
    applicationConfig({
      providers: [provideRouter([]), provideHttpClient(withInterceptorsFromDi())],
    }),
  ],
  argTypes: {
    onFiltersChange: { control: false },
  },
};
export default meta;
type Story = StoryObj<BaseComparisonToolComponent>;

export const Demo: Story = {
  args: {
    isLoading: false,
    resultsCount: 1000,
    headerTitle: 'Gene Comparison',
    filterResultsButtonTooltip: 'Filter the results based on the selected criteria',
    filterConfigs: mockComparisonToolConfigFilters,
    onFiltersChange: (filters: ComparisonToolFilter[]) => {
      console.log('Filters changed:', filters);
    },
  },
};
