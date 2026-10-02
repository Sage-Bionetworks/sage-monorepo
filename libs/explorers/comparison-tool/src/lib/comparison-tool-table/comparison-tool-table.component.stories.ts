import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { ComparisonToolConfig, ComparisonToolNoun } from '@sagebionetworks/explorers/models';
import {
  provideComparisonToolFilterService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolData,
  mockComparisonToolDataConfig,
  mockComparisonToolFiltersWithSelections,
} from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { MessageService } from 'primeng/api';
import { ComparisonToolTableComponent } from './comparison-tool-table.component';

const meta: Meta<ComparisonToolTableComponent> = {
  component: ComparisonToolTableComponent,
  title: 'Comparison Tool/ComparisonToolTable/ComparisonToolTableComponent',
  decorators: [
    applicationConfig({
      providers: [
        MessageService,
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideComparisonToolFilterService(),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<ComparisonToolTableComponent>;

const PARENT_NOUN: ComparisonToolNoun = { singular: 'Parent', plural: 'Parents' };
const CHILD_NOUN: ComparisonToolNoun = { singular: 'Child', plural: 'Children' };

export const NoPinned: Story = {
  args: {},
  decorators: [
    applicationConfig({
      providers: [
        MessageService,
        ...provideComparisonToolService({
          pinnedItems: [],
          pinLimit: 5,
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
        MessageService,
        ...provideComparisonToolService({
          pinnedItems: mockComparisonToolData.slice(0, 3).map((item) => item['_id']),
          pinLimit: 5,
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
        MessageService,
        ...provideComparisonToolService({
          pinnedItems: mockComparisonToolData.slice(0, 3).map((item) => item['_id']),
          pinLimit: 5,
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

export const FiltersActivePinLimitReached: Story = {
  args: {},
  decorators: [
    applicationConfig({
      providers: [
        ...provideComparisonToolService({
          pinnedItems: mockComparisonToolData.slice(0, 3).map((item) => item['_id']),
          pinLimit: 3,
          pinnedData: mockComparisonToolData.slice(0, 3),
          unpinnedData: mockComparisonToolData.slice(3),
          configs: mockComparisonToolDataConfig,
        }),
        ...provideComparisonToolFilterService({
          filters: mockComparisonToolFiltersWithSelections,
        }),
      ],
    }),
  ],
};

export const RowSelectionAndHover: Story = {
  args: {},
  decorators: [
    applicationConfig({
      providers: [
        MessageService,
        ...provideComparisonToolService({
          pinnedItems: [],
          pinLimit: 5,
          pinnedData: [],
          unpinnedData: mockComparisonToolData,
          configs: mockComparisonToolDataConfig,
          viewConfig: {
            rowSelectionEnabled: true,
            rowHoverEnabled: true,
            rowIdDataKey: '_id',
          },
        }),
      ],
    }),
  ],
};

export const ViewNoun: Story = {
  args: {},
  decorators: [
    applicationConfig({
      providers: [
        MessageService,
        ...provideComparisonToolService({
          pinnedItems: mockComparisonToolData.slice(0, 3).map((item) => item['_id']),
          pinLimit: 5,
          pinnedData: mockComparisonToolData.slice(0, 3),
          unpinnedData: mockComparisonToolData.slice(3),
          configs: [
            { ...mockComparisonToolDataConfig[0], view_noun: PARENT_NOUN },
          ] satisfies ComparisonToolConfig[],
        }),
      ],
    }),
  ],
};

const childViewPinnedData = [
  mockComparisonToolData[0],
  mockComparisonToolData[1],
  mockComparisonToolData[4],
];

const childViewConfigs = [
  {
    ...mockComparisonToolDataConfig[0],
    row_id_data_key: '_id',
    parent_id_data_key: 'model_type',
    view_noun: CHILD_NOUN,
    parent_noun: PARENT_NOUN,
  },
] satisfies ComparisonToolConfig[];

export const ChildView: Story = {
  args: {},
  decorators: [
    applicationConfig({
      providers: [
        MessageService,
        ...provideComparisonToolService({
          pinnedItems: childViewPinnedData.map((item) => item['_id']),
          pinLimit: 5,
          pinnedData: childViewPinnedData,
          unpinnedData: mockComparisonToolData.filter(
            (item) => !childViewPinnedData.includes(item),
          ),
          configs: childViewConfigs,
        }),
      ],
    }),
  ],
};

export const PinnedFetchFailedChildView: Story = {
  args: {},
  decorators: [
    applicationConfig({
      providers: [
        MessageService,
        ...provideComparisonToolService({
          pinnedItems: childViewPinnedData.map((item) => item['_id']),
          pinnedFetchFails: true,
          pinLimit: 5,
          unpinnedData: mockComparisonToolData.filter(
            (item) => !childViewPinnedData.includes(item),
          ),
          configs: childViewConfigs,
        }),
      ],
    }),
  ],
};
