import { render, screen, waitFor } from '@testing-library/angular';
import { userEvent } from '@testing-library/user-event';
import { ComparisonToolSearchInputComponent } from './comparison-tool-search-input.component';
import { ComparisonToolFilterService } from '@sagebionetworks/explorers/services';
import { signal } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { DEBOUNCE_TIME_MS } from '@sagebionetworks/explorers/constants';

const mockComparisonToolFilterService = {
  searchTerm: signal(''),
  updateSearchTerm: jest.fn(),
};

async function setup(searchTerm = '') {
  mockComparisonToolFilterService.searchTerm.set(searchTerm);

  const { fixture } = await render(ComparisonToolSearchInputComponent, {
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      {
        provide: ComparisonToolFilterService,
        useValue: mockComparisonToolFilterService,
      },
    ],
  });

  const component = fixture.componentInstance;
  return { component, fixture };
}

describe('ComparisonToolSearchInputComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockComparisonToolFilterService.searchTerm.set('');
  });

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should render the search input', async () => {
    await setup();

    const searchInput = screen.getByRole('textbox');
    expect(searchInput).toBeInTheDocument();
  });

  it('should display the current search term', async () => {
    await setup('test query');

    const searchInput = screen.getByRole('textbox') as HTMLInputElement;
    expect(searchInput.value).toBe('test query');
  });

  it('should debounce search term updates', async () => {
    const user = userEvent.setup();
    await setup();

    const searchInput = screen.getByRole('textbox');
    await user.type(searchInput, 'search');

    expect(mockComparisonToolFilterService.updateSearchTerm).not.toHaveBeenCalled();

    await waitFor(
      () => {
        expect(mockComparisonToolFilterService.updateSearchTerm).toHaveBeenCalledWith('search');
      },
      { timeout: DEBOUNCE_TIME_MS + 100 },
    );
  });

  it('should update search term after debounce delay', async () => {
    const user = userEvent.setup();
    await setup();

    const searchInput = screen.getByRole('textbox');
    await user.type(searchInput, 'test');

    await waitFor(
      () => {
        expect(mockComparisonToolFilterService.updateSearchTerm).toHaveBeenCalledWith('test');
      },
      { timeout: DEBOUNCE_TIME_MS + 100 },
    );
  });

  it('should clear search when clear button is clicked', async () => {
    const user = userEvent.setup();
    await setup('existing search');

    const clearButton = screen.getByRole('button');
    await user.click(clearButton);

    expect(mockComparisonToolFilterService.updateSearchTerm).toHaveBeenCalledWith('');
  });

  it('should handle multiple rapid input changes', async () => {
    const user = userEvent.setup();
    await setup();

    const searchInput = screen.getByRole('textbox');
    await user.type(searchInput, 'abc');

    await waitFor(
      () => {
        expect(mockComparisonToolFilterService.updateSearchTerm).toHaveBeenCalledTimes(1);
        expect(mockComparisonToolFilterService.updateSearchTerm).toHaveBeenCalledWith('abc');
      },
      { timeout: DEBOUNCE_TIME_MS + 100 },
    );
  });
});
