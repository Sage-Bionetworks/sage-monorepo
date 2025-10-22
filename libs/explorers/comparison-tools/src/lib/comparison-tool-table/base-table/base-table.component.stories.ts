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
  title: 'Comparison Tools/BaseTableComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideComparisonToolService({
          configs: mockComparisonToolDataConfig,
          pinnedItems: ['3xTg-AD', 'APOE4'],
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
