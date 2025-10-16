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
import { ComparisonToolSelectorsComponent } from './comparison-tool-selectors.component';

const meta: Meta<ComparisonToolSelectorsComponent> = {
  component: ComparisonToolSelectorsComponent,
  title: 'Comparison Tools/ComparisonToolSelectorsComponent',
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
type Story = StoryObj<ComparisonToolSelectorsComponent>;

export const MultipleDropdowns: Story = {
  decorators: [
    applicationConfig({
      providers: [
        ...provideComparisonToolService({
          configs: mockComparisonToolConfigs,
        }),
      ],
    }),
  ],
  args: {
    selectorsWikiParams: mockComparisonToolSelectorsWikiParams,
  },
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
