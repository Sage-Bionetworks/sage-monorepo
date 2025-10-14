import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import {
  mockComparisonToolConfigs,
  mockComparisonToolSelectorPopoverWikiIds,
} from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { ComparisonToolSelectorsComponent } from './comparison-tool-selectors.component';

const meta: Meta<ComparisonToolSelectorsComponent> = {
  component: ComparisonToolSelectorsComponent,
  title: 'Comparison Tools/ComparisonToolSelectorsComponent',
  decorators: [
    applicationConfig({
      providers: [provideRouter([]), provideHttpClient(withInterceptorsFromDi())],
    }),
  ],
  argTypes: {
    selectionChanged: { control: false },
  },
};
export default meta;
type Story = StoryObj<ComparisonToolSelectorsComponent>;

export const MultipleDropdowns: Story = {
  args: {
    pageConfigs: mockComparisonToolConfigs,
    popoverWikis: mockComparisonToolSelectorPopoverWikiIds,
    initialSelection: mockComparisonToolConfigs[0].dropdowns,
    selectionChanged: (selection: string[]) => console.log(selection),
  },
};

export const NoDropdowns: Story = {
  args: {
    pageConfigs: [{ ...mockComparisonToolConfigs[0], dropdowns: [] }],
  },
};
