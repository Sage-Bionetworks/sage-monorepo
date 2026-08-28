import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { GeneService } from '@sagebionetworks/agora/api-client';
import { mockSearchResults } from '@sagebionetworks/agora/testing';
import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular';
import { delay, of } from 'rxjs';
import { SearchInputComponent } from './search-input.component';

const mockGeneService = {
  searchGeneEnhanced: () => of(mockSearchResults).pipe(delay(1000)),
};

const meta: Meta<SearchInputComponent> = {
  component: SearchInputComponent,
  title: 'UI/SearchInputComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
        { provide: GeneService, useValue: mockGeneService },
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<SearchInputComponent>;

export const HeaderSearchInput: Story = {
  args: {
    searchPlaceholder: 'Search genes',
  },
};

export const HomeSearchInput: Story = {
  args: {
    searchPlaceholder: 'Find Gene by Name...',
    searchImagePath: 'explorers-assets/images/gene-search-icon.svg',
    searchImageAltText: 'gene search icon',
    hasThickBorder: true,
  },
};
