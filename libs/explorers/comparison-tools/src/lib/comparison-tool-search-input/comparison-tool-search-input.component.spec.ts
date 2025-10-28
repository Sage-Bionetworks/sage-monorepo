import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal } from '@angular/core';
import { DEBOUNCE_TIME_MS } from '@sagebionetworks/explorers/constants';
import { ComparisonToolFilterService, SvgIconService } from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen, waitFor } from '@testing-library/angular';
import { userEvent } from '@testing-library/user-event';
import { ComparisonToolSearchInputComponent } from './comparison-tool-search-input.component';

const DEBOUNCE_TIME_FOR_TESTING = DEBOUNCE_TIME_MS + 100;

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
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
  });

  const component = fixture.componentInstance;
  const user = userEvent.setup();
  return { component, fixture, user };
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
    const { user } = await setup();

    const searchInput = screen.getByRole('textbox');
    await user.type(searchInput, 'search');

    expect(mockComparisonToolFilterService.updateSearchTerm).not.toHaveBeenCalled();

    await waitFor(
      () => {
        expect(mockComparisonToolFilterService.updateSearchTerm).toHaveBeenCalledWith('search');
      },
      { timeout: DEBOUNCE_TIME_FOR_TESTING },
    );
  });

  it('should update search term after debounce delay', async () => {
    const { user } = await setup();

    const searchInput = screen.getByRole('textbox');
    await user.type(searchInput, 'test');

    await waitFor(
      () => {
        expect(mockComparisonToolFilterService.updateSearchTerm).toHaveBeenCalledWith('test');
      },
      { timeout: DEBOUNCE_TIME_FOR_TESTING },
    );
  });

  it('should clear search when clear button is clicked', async () => {
    const { user } = await setup('existing search');

    const clearButton = screen.getByRole('button');
    await user.click(clearButton);

    expect(mockComparisonToolFilterService.updateSearchTerm).toHaveBeenCalledWith('');
  });

  it('should handle multiple rapid input changes', async () => {
    const { user } = await setup();

    const searchInput = screen.getByRole('textbox');

    // Rapidly type multiple characters
    await user.type(searchInput, 'a');
    await user.type(searchInput, 'b');
    await user.type(searchInput, 'c');

    // Should not call updateSearchTerm immediately
    expect(mockComparisonToolFilterService.updateSearchTerm).not.toHaveBeenCalled();

    // After debounce, should only call once with the final value
    await waitFor(
      () => {
        expect(mockComparisonToolFilterService.updateSearchTerm).toHaveBeenCalledTimes(1);
        expect(mockComparisonToolFilterService.updateSearchTerm).toHaveBeenCalledWith('abc');
      },
      { timeout: DEBOUNCE_TIME_FOR_TESTING },
    );
  });
});
