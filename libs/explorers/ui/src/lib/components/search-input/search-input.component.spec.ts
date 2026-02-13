import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { SearchResult } from '@sagebionetworks/explorers/models';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import {
  mockCheckQueryForErrors,
  mockFormatResultSubtextForDisplay,
  mockGetNoSearchResultsMessage,
  mockGetSearchResults,
  SvgIconServiceStub,
} from '@sagebionetworks/explorers/testing';
import { render, screen, waitFor } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { SearchInputComponent } from './search-input.component';

// Mock scrollIntoView for JSDOM environment since it doesn't exist there
Object.defineProperty(HTMLElement.prototype, 'scrollIntoView', {
  value: jest.fn(),
  writable: true,
});

const mockNavigateToResult = jest.fn();
const searchPlaceholder = 'Find Gene by Name...';

function getInput() {
  return screen.getByPlaceholderText(searchPlaceholder);
}

async function waitForSpinner() {
  const spinner = await screen.findByTestId('spinner');
  await waitFor(
    () => {
      expect(spinner).not.toBeInTheDocument();
    },
    { timeout: 5_000 },
  );
}

async function setup(
  formatResultSubtextForDisplay?: (result: SearchResult) => string | undefined,
  getNoSearchResultsMessage?: (query: string) => string,
) {
  const optionalInputs: Record<string, any> = {};

  if (formatResultSubtextForDisplay) {
    optionalInputs['formatResultSubtextForDisplay'] = formatResultSubtextForDisplay;
  }

  if (getNoSearchResultsMessage) {
    optionalInputs['getNoSearchResultsMessage'] = getNoSearchResultsMessage;
  }

  const user = userEvent.setup();
  const component = await render(SearchInputComponent, {
    providers: [
      provideHttpClient(),
      provideRouter([]),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
    componentInputs: {
      ...optionalInputs,
      searchPlaceholder: searchPlaceholder,
      searchImagePath: 'explorers-assets/icons/gene-search-icon.svg',
      searchImageAltText: 'gene search icon',
      hasThickBorder: true,
      navigateToResult: mockNavigateToResult,
      getSearchResults: mockGetSearchResults,
      checkQueryForErrors: mockCheckQueryForErrors,
    },
  });
  const { container } = component;
  return { user, component, container };
}

describe('SearchInputComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should display', async () => {
    await setup();
    expect(getInput()).toHaveValue('');
  });

  it('should display an error when query is 1 character', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'ab');
    expect(input).toHaveValue('ab');

    await screen.findByText('Please enter at least three characters.');
  });

  it('should display error returned by checkQueryForErrors', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'ensg');
    expect(input).toHaveValue('ensg');

    await screen.findByText(
      'You must enter a full 15-character value to search for a gene by Ensembl identifier.',
    );
  });

  it('should display not found error when search returns no results', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'notfound');
    expect(input).toHaveValue('notfound');

    await waitForSpinner();

    await screen.findByText('No results match your search term.');
  });

  it('should highlight search query with mark', async () => {
    const { container, user } = await setup();

    const input = getInput();
    await user.type(input, 'dummy');
    await waitForSpinner();

    const markedResults = container.querySelectorAll('mark');
    expect(markedResults).toHaveLength(2);
    expect(markedResults[1].textContent).toBe('dummy');
  });

  it('should display results when search is successful', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'dummy');
    await waitForSpinner();

    expect(input).toHaveValue('dummy');
    await screen.findByLabelText('dummy_id');
    await screen.findByLabelText('dummy_id_2');
  });

  it('should navigate to result when clicked', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'dummy');
    await waitForSpinner();

    await user.click(screen.getByLabelText('dummy_id'));
    expect(mockNavigateToResult).toHaveBeenCalledWith('dummy_id');

    expect(input).toHaveValue('');
  });

  it('should clear input when x is clicked', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'dummy');
    await waitForSpinner();

    const clearButton = screen.getByRole('button', { name: 'Clear' });
    await user.click(clearButton);

    expect(input).toHaveValue('');
  });

  it('should highlight first result when pressing down arrow', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'dummy');
    await waitForSpinner();

    await user.keyboard('[ArrowDown]');

    const firstResult = screen.getByLabelText('dummy_id').closest('li');
    expect(firstResult).toHaveClass('selected');
  });

  it('should navigate through results with arrow keys', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'dummy');
    await waitForSpinner();

    await user.keyboard('[ArrowDown]');
    const firstResult = screen.getByLabelText('dummy_id').closest('li');
    expect(firstResult).toHaveClass('selected');

    await user.keyboard('[ArrowDown]');
    const secondResult = screen.getByLabelText('dummy_id_2').closest('li');
    expect(secondResult).toHaveClass('selected');
    expect(firstResult).not.toHaveClass('selected');

    await user.keyboard('[ArrowUp]');
    expect(firstResult).toHaveClass('selected');
    expect(secondResult).not.toHaveClass('selected');
  });

  it('should select result with Enter key', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'dummy');
    await waitForSpinner();

    await user.keyboard('[ArrowDown]');
    await user.keyboard('[Enter]');

    expect(mockNavigateToResult).toHaveBeenCalledWith('dummy_id');
    expect(input).toHaveValue('');
  });

  it('should clear input with Escape key', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'dummy');
    await waitForSpinner();

    await user.keyboard('[Escape]');

    expect(input).toHaveValue('');
  });

  it('should handle arrow keys without results gracefully', async () => {
    const { user } = await setup();

    await user.keyboard('[ArrowDown]');
    await user.keyboard('[ArrowUp]');
    await user.keyboard('[Enter]');

    expect(mockNavigateToResult).not.toHaveBeenCalled();
  });

  it('should sync selection with mouse hover', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'dummy');
    await waitForSpinner();

    await user.keyboard('[ArrowDown]');
    const firstResult = screen.getByLabelText('dummy_id').closest('li');
    expect(firstResult).toHaveClass('selected');

    const secondResult = screen.getByLabelText('dummy_id_2').closest('li');
    if (secondResult) {
      await user.hover(secondResult);
    }

    expect(secondResult).toHaveClass('selected');
    expect(firstResult).not.toHaveClass('selected');
  });

  it('should clear selection when mouse leaves results area', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'dummy');
    await waitForSpinner();

    const firstResult = screen.getByLabelText('dummy_id').closest('li');
    if (firstResult) {
      await user.hover(firstResult);
    }
    expect(firstResult).toHaveClass('selected');

    if (firstResult) {
      await user.unhover(firstResult);
    }

    expect(firstResult).not.toHaveClass('selected');
  });

  it('should not trigger search when using navigation keys', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'dummy');
    await waitForSpinner();

    await user.keyboard('[ArrowDown]');
    await user.keyboard('[ArrowUp]');

    expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();
  });

  it('should not navigate beyond bounds', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'dummy');
    await waitForSpinner();

    await user.keyboard('[ArrowUp]');
    const results = screen.getAllByRole('button', { name: /dummy_id/ });
    results.forEach((result) => {
      expect(result.closest('li')).not.toHaveClass('selected');
    });

    await user.keyboard('[ArrowDown]');
    await user.keyboard('[ArrowDown]');
    await user.keyboard('[ArrowDown]'); // Try to go past last result

    const secondResult = screen.getByLabelText('dummy_id_2').closest('li');
    expect(secondResult).toHaveClass('selected');
  });

  it('should allow searching for the same query after clearing input', async () => {
    const query = 'dummy';

    const { user } = await setup();
    const input = getInput();

    await user.type(input, query);
    await waitForSpinner();

    expect(screen.getByLabelText('dummy_id')).toBeInTheDocument();
    expect(screen.getByLabelText('dummy_id_2')).toBeInTheDocument();

    const clearButton = screen.getByRole('button', { name: 'Clear' });
    await user.click(clearButton);

    expect(input).toHaveValue('');
    expect(screen.queryByLabelText('dummy_id')).not.toBeInTheDocument();

    await user.type(input, query);
    await waitForSpinner();

    expect(screen.getByLabelText('dummy_id')).toBeInTheDocument();
    expect(screen.getByLabelText('dummy_id_2')).toBeInTheDocument();
  });

  it('should not trim search with special characters', async () => {
    const { user } = await setup();
    const input = getInput();

    const specialCharQuery = 'gene-name_1234:();,.\'*&"@#=!$';
    await user.type(input, specialCharQuery);

    await waitForSpinner();
    expect(input).toHaveValue(specialCharQuery);
  });

  it('should display default no results message when no custom function provided', async () => {
    const { user } = await setup();

    const input = getInput();
    await user.type(input, 'ensg00000000000');
    expect(input).toHaveValue('ensg00000000000');

    await waitForSpinner();

    screen.getByText('No results match your search term.');
  });

  it('should display custom no results message when provided', async () => {
    const { user } = await setup(mockFormatResultSubtextForDisplay, mockGetNoSearchResultsMessage);

    const input = getInput();
    await user.type(input, 'ensg00000000000');
    expect(input).toHaveValue('ensg00000000000');

    await waitForSpinner();

    screen.getByText('Unable to find a matching gene. Try searching by gene symbol.');
  });

  it('should construct expected not valid search message for different minimumSearchLength values', async () => {
    const { component } = await setup();
    const { fixture } = component;
    const instance = fixture.componentInstance;

    const testCases = [
      { minLength: 1, expectedMessage: 'Please enter at least one character.' },
      { minLength: 2, expectedMessage: 'Please enter at least two characters.' },
      { minLength: 3, expectedMessage: 'Please enter at least three characters.' },
      { minLength: 4, expectedMessage: 'Please enter at least four characters.' },
      { minLength: 5, expectedMessage: 'Please enter at least five characters.' },
      { minLength: 6, expectedMessage: 'Please enter at least six characters.' },
      { minLength: 7, expectedMessage: 'Please enter at least seven characters.' },
      { minLength: 8, expectedMessage: 'Please enter at least eight characters.' },
      { minLength: 9, expectedMessage: 'Please enter at least nine characters.' },
    ];

    for (const { minLength, expectedMessage } of testCases) {
      expect(instance.getNotValidSearchMessage(minLength)).toBe(expectedMessage);
    }
  });

  it('should only display results text when subtext format function is not provided', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, '123');
    await waitForSpinner();

    const firstResult = screen.getByRole('button', { name: 'dummy_id' });
    expect(firstResult).toHaveTextContent(/dummy_id/);
    expect(firstResult).not.toHaveTextContent(/dummy name/i);
  });

  it('should display results text and subtext when subtext format function is provided', async () => {
    const { user } = await setup(mockFormatResultSubtextForDisplay);
    const input = getInput();

    await user.type(input, '123');
    await waitForSpinner();

    const firstResult = screen.getByRole('button', { name: 'dummy_id' });
    expect(firstResult).toHaveTextContent(/dummy name/i);

    const secondResult = screen.getByRole('button', { name: 'dummy_id_2' });
    expect(secondResult).toHaveTextContent(/alternate name/i);
  });
});
