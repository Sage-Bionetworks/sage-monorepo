import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import {
  mockCheckQueryForErrors,
  mockGetSearchResultsList,
  mockNavigateToResult,
} from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { SearchInputComponent } from './search-input.component';

const meta: Meta<SearchInputComponent> = {
  component: SearchInputComponent,
  title: 'UI/SearchInputComponent',
  decorators: [
    applicationConfig({
      providers: [provideRouter([]), provideHttpClient(withInterceptorsFromDi())],
    }),
  ],
  argTypes: {
    navigateToResult: { control: false },
    getSearchResultsList: { control: false },
    checkQueryForErrors: { control: false },
  },
};
export default meta;
type Story = StoryObj<SearchInputComponent>;

export const HeaderSearchInput: Story = {
  args: {
    searchPlaceholder: 'Search genes',
    navigateToResult: mockNavigateToResult,
    getSearchResultsList: mockGetSearchResultsList,
    checkQueryForErrors: mockCheckQueryForErrors,
  },
};

export const HomeSearchInput: Story = {
  args: {
    searchPlaceholder: 'Find Gene by Name...',
    searchIconPath: '/explorers-assets/icons/gene-search-icon.svg',
    searchIconAltText: 'gene search icon',
    hasThickBorder: true,
    navigateToResult: mockNavigateToResult,
    getSearchResultsList: mockGetSearchResultsList,
    checkQueryForErrors: mockCheckQueryForErrors,
  },
};
