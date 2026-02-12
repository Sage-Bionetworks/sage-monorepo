import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import {
  mockCheckQueryForErrors,
  mockGetSearchResults,
  mockNavigateToResult,
} from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { SearchInputComponent } from '../search-input/search-input.component';
import { HomeCardComponent } from './home-card.component';

const meta: Meta<HomeCardComponent> = {
  component: HomeCardComponent,
  title: 'UI/Cards/HomeCardComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<HomeCardComponent>;

export const LinkHomeCard: Story = {
  args: {
    title: 'Gene Comparison',
    description:
      'Compare differential RNA and protein expression results for 20k+ human genes in AD. Build custom result sets by sorting, filtering, and searching for genes of interest.',
    imagePath: 'explorers-assets/images/gene-comparison-icon.svg',
    imageAltText: 'gene comparison icon',
    routerLink: '/genes/comparison',
  },
};

export const SearchHomeCard: Story = {
  args: {
    title: 'Gene Search',
    description:
      'Search for a gene by name or Ensembl gene ID to view related experimental evidence, find detailed information about nominations, and explore its association with AD.',
    imagePath: 'explorers-assets/images/gene-search-icon.svg',
    imageAltText: 'gene comparison icon',
  },
  render: (args) => ({
    props: {
      ...args,
      navigateToResult: mockNavigateToResult,
      getSearchResults: mockGetSearchResults,
      checkQueryForErrors: mockCheckQueryForErrors,
    },
    template: `
      <explorers-home-card
        [title]="title"
        [description]="description"
      >
        <explorers-search-input
          searchPlaceholder="Find Gene by Name..."
          [searchImagePath]="imagePath"
          [searchImageAltText]="imageAltText"
          [navigateToResult]="navigateToResult"
          [getSearchResults]="getSearchResults"
          [checkQueryForErrors]="checkQueryForErrors"
          [hasThickBorder]="true"
        />
      </explorers-home-card>
    `,
    moduleMetadata: {
      imports: [HomeCardComponent, SearchInputComponent],
    },
  }),
};
