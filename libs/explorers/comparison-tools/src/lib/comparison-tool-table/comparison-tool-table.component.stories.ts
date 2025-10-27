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
import { ComparisonToolTableComponent } from './comparison-tool-table.component';

const meta: Meta<ComparisonToolTableComponent> = {
  component: ComparisonToolTableComponent,
  title: 'Comparison Tools/ComparisonToolTable/ComparisonToolTableComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideComparisonToolFilterService(),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<ComparisonToolTableComponent>;

export const NoPinned: Story = {
  args: {},
  decorators: [
    applicationConfig({
      providers: [
        ...provideComparisonToolService({
          pinnedItems: [],
          maxPinnedItems: 5,
          pinnedData: [],
          unpinnedData: mockComparisonToolData,
          configs: mockComparisonToolDataConfig,
        }),
      ],
    }),
  ],
};

export const PinnedWithoutSearchTerm: Story = {
  args: {},
  decorators: [
    applicationConfig({
      providers: [
        ...provideComparisonToolService({
          pinnedItems: ['3xTg-AD', '5xFAD (UCI)', '5xFAD (IU/Jax/Pitt)'],
          maxPinnedItems: 5,
          pinnedData: mockComparisonToolData.slice(0, 3),
          unpinnedData: mockComparisonToolData.slice(3),
          configs: mockComparisonToolDataConfig,
        }),
      ],
    }),
  ],
};

export const SearchTermActive: Story = {
  args: {},
  decorators: [
    applicationConfig({
      providers: [
        ...provideComparisonToolService({
          pinnedItems: ['3xTg-AD', '5xFAD (UCI)', '5xFAD (IU/Jax/Pitt)'],
          maxPinnedItems: 5,
          pinnedData: mockComparisonToolData.slice(0, 3),
          unpinnedData: mockComparisonToolData.slice(3),
          configs: mockComparisonToolDataConfig,
        }),
        ...provideComparisonToolFilterService({
          searchTerm: '5xFAD',
        }),
      ],
    }),
  ],
};
