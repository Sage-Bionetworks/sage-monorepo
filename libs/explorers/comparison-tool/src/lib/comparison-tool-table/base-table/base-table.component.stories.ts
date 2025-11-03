import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import {
  provideComparisonToolFilterService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolData,
  mockComparisonToolDataConfig,
} from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { BaseTableComponent } from './base-table.component';

const meta: Meta<BaseTableComponent> = {
  component: BaseTableComponent,
  title: 'Comparison Tool/ComparisonToolTable/BaseTableComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideComparisonToolService({
          configs: mockComparisonToolDataConfig,
          pinnedItems: ['68fff1aaeb12b9674515fd58', '68fff1aaeb12b9674515fd5c'],
        }),
        ...provideComparisonToolFilterService({
          significanceThresholdActive: false,
          significanceThreshold: 0.05,
        }),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<BaseTableComponent>;

export const Demo: Story = {
  args: {
    data: mockComparisonToolData,
    shouldPaginate: true,
  },
};

export const NoData: Story = {
  args: {
    data: [],
    shouldPaginate: true,
    shouldShowNoDataMessage: true,
  },
};
