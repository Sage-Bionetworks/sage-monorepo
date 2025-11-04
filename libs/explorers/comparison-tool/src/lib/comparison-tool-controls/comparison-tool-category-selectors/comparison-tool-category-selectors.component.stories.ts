import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { provideComparisonToolService } from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolConfigs,
  mockComparisonToolSelectorsWikiParams,
  provideLoadingIconColors,
} from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { ComparisonToolCategorySelectorsComponent } from './comparison-tool-category-selectors.component';

const meta: Meta<ComparisonToolCategorySelectorsComponent> = {
  component: ComparisonToolCategorySelectorsComponent,
  title: 'Comparison Tool/ComparisonToolSelectorsComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideLoadingIconColors(),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<ComparisonToolCategorySelectorsComponent>;

export const MultipleDropdowns: Story = {
  decorators: [
    applicationConfig({
      providers: [
        ...provideComparisonToolService({
          configs: mockComparisonToolConfigs,
          viewConfig: { selectorsWikiParams: mockComparisonToolSelectorsWikiParams },
        }),
      ],
    }),
  ],
  args: {},
};

export const NoDropdowns: Story = {
  decorators: [
    applicationConfig({
      providers: [
        ...provideComparisonToolService({
          configs: [{ ...mockComparisonToolConfigs[0], dropdowns: [] }],
        }),
      ],
    }),
  ],
  args: {},
};
