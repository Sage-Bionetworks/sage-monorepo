import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { SearchResultsList } from '@sagebionetworks/explorers/models';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import {
  mockCheckQueryForErrors,
  mockGetSearchResultsList,
  SvgIconServiceStub,
} from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { Observable } from 'rxjs';
import { HomeCardComponent } from './home-card.component';

const mockRouterLink = '/mock-router-link';
const mockTitle = 'Mock Title';
const mockDescription = 'Mock Description';
const mockImagePath = '/path/to/image.png';
const mockImageAltText = 'Mock Image Alt Text';

async function setup(
  routerLink?: string,
  searchPlaceholder?: string,
  navigateToResult?: (id: string) => void,
  getSearchResultsList?: (query: string) => Observable<SearchResultsList>,
  checkQueryForErrors?: (query: string) => string,
) {
  const user = userEvent.setup();
  const component = await render(HomeCardComponent, {
    providers: [
      provideHttpClient(),
      provideRouter([]),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
    componentInputs: {
      title: mockTitle,
      description: mockDescription,
      imagePath: mockImagePath,
      imageAltText: mockImageAltText,
      routerLink: routerLink,
      searchPlaceholder: searchPlaceholder,
      navigateToResult: navigateToResult,
      getSearchResultsList: getSearchResultsList,
      checkQueryForErrors: checkQueryForErrors,
    },
  });
  return { user, component };
}

describe('HomeCardComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should display title', async () => {
    await setup();
    expect(screen.getByText(mockTitle)).toBeInTheDocument();
  });

  it('should display description', async () => {
    await setup();
    expect(screen.getByText(mockDescription)).toBeInTheDocument();
  });

  it('should include image alt text', async () => {
    await setup(mockRouterLink);
    expect(screen.getByAltText(mockImageAltText)).toBeInTheDocument();
  });

  it('should include router link when specified', async () => {
    await setup(mockRouterLink);
    const button = screen.getByRole('button');
    expect(button).toHaveAttribute('ng-reflect-router-link', mockRouterLink);
  });

  it('should display search input when specified', async () => {
    const mockNavigateToResult = jest.fn();
    const mockSearchPlaceholder = 'Search...';
    await setup(
      undefined,
      mockSearchPlaceholder,
      mockNavigateToResult,
      mockGetSearchResultsList,
      mockCheckQueryForErrors,
    );
    expect(screen.getByPlaceholderText(mockSearchPlaceholder)).toBeInTheDocument();
  });
});
