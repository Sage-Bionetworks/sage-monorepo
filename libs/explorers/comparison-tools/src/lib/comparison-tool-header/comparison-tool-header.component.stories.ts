import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';
import { mockComparisonToolFilterConfigs } from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { ComparisonToolHeaderComponent } from './comparison-tool-header.component';

const meta: Meta<ComparisonToolHeaderComponent> = {
  component: ComparisonToolHeaderComponent,
  title: 'Comparison Tools/ComparisonToolHeaderComponent',
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
type Story = StoryObj<ComparisonToolHeaderComponent>;

export const Demo: Story = {
  args: {
    headerTitle: 'Gene Comparison',
    filterResultsButtonTooltip: 'Filter the results based on the selected criteria',
    filterConfigs: mockComparisonToolFilterConfigs,
    onFiltersChange: (filters: ComparisonToolFilter[]) => {
      console.log('Filters changed:', filters);
    },
  },
};
