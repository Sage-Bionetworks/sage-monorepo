import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import {
  mockCheckQueryForErrors,
  mockGetSearchResults,
  SvgIconServiceStub,
} from '@sagebionetworks/explorers/testing';
import { render, screen, waitFor } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { SearchInputComponent } from './search-input.component';

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

async function setup() {
  const user = userEvent.setup();
  const component = render(SearchInputComponent, {
    providers: [
      provideHttpClient(),
      provideRouter([]),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
    componentInputs: {
      searchPlaceholder: searchPlaceholder,
      searchImagePath: '/explorers-assets/icons/gene-search-icon.svg',
      searchImageAltText: 'gene search icon',
      hasThickBorder: true,
      navigateToResult: mockNavigateToResult,
      getSearchResults: mockGetSearchResults,
      checkQueryForErrors: mockCheckQueryForErrors,
    },
  });
  return { user, component };
}

describe('SearchInputComponent', () => {
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

    await screen.findByText('No results found. Try searching again.');
  });

  it('should display results when search is successful', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'dummy');
    await waitForSpinner();

    expect(input).toHaveValue('dummy');
    await screen.findByText('dummy_id');
    await screen.findByText('dummy_id_2');
  });

  it('should navigate to result when clicked', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'dummy');
    await waitForSpinner();

    await user.click(screen.getByText('dummy_id'));
    expect(mockNavigateToResult).toHaveBeenCalledWith('dummy_id');

    expect(getInput()).toHaveValue('');
  });

  it('should clear input when x is clicked', async () => {
    const { user } = await setup();
    const input = getInput();

    await user.type(input, 'dummy');
    await waitForSpinner();

    const clearButton = screen.getByRole('button');
    await user.click(clearButton);

    expect(getInput()).toHaveValue('');
  });
});
