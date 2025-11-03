import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import {
  provideComparisonToolFilterService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolData,
  mockComparisonToolDataConfig,
  mockComparisonToolSelectorsWikiParams,
  provideLoadingIconColors,
} from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { ComparisonToolComponent } from './comparison-tool.component';

const meta: Meta<ComparisonToolComponent> = {
  component: ComparisonToolComponent,
  title: 'Comparison Tools/ComparisonToolComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideComparisonToolService({
          configs: mockComparisonToolDataConfig,
          totalResultsCount: 1000,
          viewConfig: {
            selectorsWikiParams: mockComparisonToolSelectorsWikiParams,
            headerTitle: 'Gene Comparison',
            filterResultsButtonTooltip: 'Filter the results based on the selected criteria',
          },
          pinnedItems: [
            '68fff1aaeb12b9674515fd58',
            '68fff1aaeb12b9674515fd59',
            '68fff1aaeb12b9674515fd5a',
          ],
          maxPinnedItems: 5,
          pinnedData: mockComparisonToolData.slice(0, 3),
          unpinnedData: mockComparisonToolData.slice(3),
        }),
        ...provideComparisonToolFilterService(),
        ...provideLoadingIconColors(),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<ComparisonToolComponent>;

export const Demo: Story = {
  args: {
    isLoading: false,
  },
};
