import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { ModelOrganism, SearchService } from '@sagebionetworks/model-ad/api-client';
import { mockSearchResults } from '@sagebionetworks/model-ad/testing';
import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular';
import { delay, of } from 'rxjs';
import { SearchInputComponent } from './search-input.component';

const mockSearchService = {
  searchModels: (_query: string, modelOrganisms?: ModelOrganism[]) =>
    of(
      mockSearchResults.filter(
        (result) => !modelOrganisms || modelOrganisms.includes(result.model_organism),
      ),
    ).pipe(delay(1000)),
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
        { provide: SearchService, useValue: mockSearchService },
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<SearchInputComponent>;

export const HeaderSearchInput: Story = {
  args: {
    searchPlaceholder: 'Search models',
  },
};

export const MouseHomeSearchInput: Story = {
  args: {
    searchPlaceholder: 'Find mouse model by name or ID...',
    searchImagePath: 'model-ad-assets/images/mouse-head.svg',
    searchImageAltText: 'mouse head icon',
    hasThickBorder: true,
    modelOrganisms: [ModelOrganism.Mouse],
  },
};

export const MarmosetHomeSearchInput: Story = {
  args: {
    searchPlaceholder: 'Find marmoset model by name...',
    searchImagePath: 'model-ad-assets/images/marmoset-model.svg',
    searchImageAltText: 'marmoset search icon',
    hasThickBorder: true,
    modelOrganisms: [ModelOrganism.Marmoset],
  },
};
