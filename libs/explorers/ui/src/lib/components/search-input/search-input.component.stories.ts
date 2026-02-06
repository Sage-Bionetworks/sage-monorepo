import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import {
  mockCheckQueryForErrors,
  mockFormatResultSubtextForDisplay,
  mockGetNoSearchResultsMessage,
  mockGetSearchResults,
  mockNavigateToResult,
  mockSanitizeQuery,
} from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { SearchInputComponent } from './search-input.component';

const meta: Meta<SearchInputComponent> = {
  component: SearchInputComponent,
  title: 'UI/SearchInputComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
      ],
    }),
  ],
  argTypes: {
    navigateToResult: { control: false },
    getSearchResults: { control: false },
    getNoSearchResultsMessage: { control: false },
    checkQueryForErrors: { control: false },
    sanitizeQuery: { control: false },
    formatResultSubtextForDisplay: { control: false },
  },
};
export default meta;
type Story = StoryObj<SearchInputComponent>;

export const HeaderSearchInput: Story = {
  args: {
    searchPlaceholder: 'Search genes',
    navigateToResult: mockNavigateToResult,
    getSearchResults: mockGetSearchResults,
    checkQueryForErrors: mockCheckQueryForErrors,
  },
};

export const HomeSearchInput: Story = {
  args: {
    searchPlaceholder: 'Find Gene by Name...',
    searchImagePath: '/explorers-assets/images/gene-search-icon.svg',
    searchImageAltText: 'gene search icon',
    hasThickBorder: true,
    formatResultSubtextForDisplay: mockFormatResultSubtextForDisplay,
    navigateToResult: mockNavigateToResult,
    getSearchResults: mockGetSearchResults,
    getNoSearchResultsMessage: mockGetNoSearchResultsMessage,
    checkQueryForErrors: mockCheckQueryForErrors,
    sanitizeQuery: mockSanitizeQuery,
    minimumSearchLength: 2,
  },
};
