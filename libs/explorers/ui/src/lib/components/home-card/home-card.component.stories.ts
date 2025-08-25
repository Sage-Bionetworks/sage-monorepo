import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import {
  mockCheckQueryForErrors,
  mockGetSearchResults,
  mockNavigateToResult,
} from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { HomeCardComponent } from './home-card.component';

const meta: Meta<HomeCardComponent> = {
  component: HomeCardComponent,
  title: 'UI/Cards/HomeCardComponent',
  decorators: [
    applicationConfig({
      providers: [provideRouter([]), provideHttpClient(withInterceptorsFromDi())],
    }),
  ],
  argTypes: {
    navigateToResult: { control: false },
    getSearchResults: { control: false },
    checkQueryForErrors: { control: false },
  },
};
export default meta;
type Story = StoryObj<HomeCardComponent>;

export const LinkHomeCard: Story = {
  args: {
    title: 'Gene Comparison',
    description:
      'Compare differential RNA and protein expression results for 20k+ human genes in AD. Build custom result sets by sorting, filtering, and searching for genes of interest.',
    imagePath: '/explorers-assets/images/gene-comparison-icon.svg',
    imageAltText: 'gene comparison icon',
    routerLink: '/genes/comparison',
  },
};

export const SearchHomeCard: Story = {
  args: {
    title: 'Gene Search',
    description:
      'Search for a gene by name or Ensembl gene ID to view related experimental evidence, find detailed information about nominations, and explore its association with AD.',
    imagePath: '/explorers-assets/images/gene-search-icon.svg',
    imageAltText: 'gene comparison icon',
    searchPlaceholder: 'Find Gene by Name...',
    navigateToResult: mockNavigateToResult,
    getSearchResults: mockGetSearchResults,
    checkQueryForErrors: mockCheckQueryForErrors,
  },
};
