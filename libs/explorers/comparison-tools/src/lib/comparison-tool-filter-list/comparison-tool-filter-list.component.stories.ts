import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { mockComparisonToolFiltersWithSelections } from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { ComparisonToolFilterListComponent } from './comparison-tool-filter-list.component';

const meta: Meta<ComparisonToolFilterListComponent> = {
  component: ComparisonToolFilterListComponent,
  title: 'Comparison Tools/ComparisonToolFilterListComponent',
  decorators: [
    applicationConfig({
      providers: [provideRouter([]), provideHttpClient(withInterceptorsFromDi())],
    }),
  ],
};
export default meta;
type Story = StoryObj<ComparisonToolFilterListComponent>;

export const Demo: Story = {
  args: {
    filters: mockComparisonToolFiltersWithSelections,
    significanceThreshold: 0.05,
    significanceThresholdActive: true,
  },
};
