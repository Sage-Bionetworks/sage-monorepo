import { provideHttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { provideRouter, Router } from '@angular/router';
import { SearchResult } from '@sagebionetworks/explorers/models';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import {
  mockCheckQueryForErrors,
  mockGetSearchResults,
  SvgIconServiceStub,
} from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { Observable } from 'rxjs';
import { HomeCardComponent } from './home-card.component';

const mockRouterLink = 'mock-router-link';
const mockTitle = 'Mock Title';
const mockDescription = 'Mock Description';
const mockImagePath = '/path/to/image.png';
const mockImageAltText = 'Mock Image Alt Text';

// Define a dummy component for the route to navigate to
@Component({ template: '<div>Dummy Content</div>' })
class DummyComponent {}

async function setup(
  routerLink?: string,
  searchPlaceholder?: string,
  navigateToResult?: (id: string) => void,
  getSearchResults?: (query: string) => Observable<SearchResult[]>,
  checkQueryForErrors?: (query: string) => string,
) {
  const user = userEvent.setup();
  const component = await render(HomeCardComponent, {
    providers: [
      provideHttpClient(),
      provideRouter([
        {
          path: mockRouterLink,
          component: DummyComponent,
        },
      ]),
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
      getSearchResults: getSearchResults,
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

  it('should navigate to route when a router link is specified', async () => {
    const { user, component } = await setup(mockRouterLink);
    const button = screen.getByRole('button');
    const router = component.fixture.debugElement.injector.get(Router);
    await user.click(button);
    expect(router.url).toBe(`/${mockRouterLink}`);
  });

  it('should display search input when specified', async () => {
    const mockNavigateToResult = jest.fn();
    const mockSearchPlaceholder = 'Search...';
    await setup(
      undefined,
      mockSearchPlaceholder,
      mockNavigateToResult,
      mockGetSearchResults,
      mockCheckQueryForErrors,
    );
    expect(screen.getByPlaceholderText(mockSearchPlaceholder)).toBeInTheDocument();
  });
});
