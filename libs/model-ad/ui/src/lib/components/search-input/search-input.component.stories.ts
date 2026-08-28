import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { provideRouter } from '@angular/router';
import { mockGetSearchResults } from '@sagebionetworks/explorers/testing';
import { ModelService } from '@sagebionetworks/model-ad/api-client';
import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular';
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
        { provide: ModelService, useValue: { searchModels: mockGetSearchResults } },
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

export const HomeSearchInput: Story = {
  args: {
    searchPlaceholder: 'Find mouse model by name or ID...',
    searchImagePath: 'model-ad-assets/images/mouse-head.svg',
    searchImageAltText: 'mouse head icon',
    hasThickBorder: true,
  },
};
