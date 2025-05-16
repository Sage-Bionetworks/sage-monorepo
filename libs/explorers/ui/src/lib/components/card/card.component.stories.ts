import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import {
  mockCheckQueryForErrors,
  mockGetSearchResultsList,
  mockNavigateToResult,
} from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { CardComponent } from './card.component';

const meta: Meta<CardComponent> = {
  component: CardComponent,
  title: 'UI/CardComponent',
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
type Story = StoryObj<CardComponent>;

export const LinkCard: Story = {
  args: {
    title: 'Gene Comparison',
    description:
      'Compare differential RNA and protein expression results for 20k+ human genes in AD. Build custom result sets by sorting, filtering, and searching for genes of interest.',
    imagePath: '/explorers-assets/images/gene-comparison-icon.svg',
    imageAltText: 'gene comparison icon',
    routerLink: '/genes/comparison',
  },
};

export const SearchCard: Story = {
  args: {
    title: 'Gene Search',
    description:
      'Search for a gene by name or Ensembl gene ID to view related experimental evidence, find detailed information about nominations, and explore its association with AD.',
    imagePath: '/explorers-assets/images/gene-search-icon.svg',
    imageAltText: 'gene comparison icon',
    searchPlaceholder: 'Find Gene by Name...',
    navigateToResult: mockNavigateToResult,
    getSearchResultsList: mockGetSearchResultsList,
    checkQueryForErrors: mockCheckQueryForErrors,
  },
};
