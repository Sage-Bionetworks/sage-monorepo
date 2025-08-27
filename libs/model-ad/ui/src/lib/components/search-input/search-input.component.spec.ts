import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { SearchResult } from '@sagebionetworks/explorers/models';
import { ModelsService } from '@sagebionetworks/model-ad/api-client-angular';
import { render, screen } from '@testing-library/angular';
import { of } from 'rxjs';
import { SearchInputComponent } from './search-input.component';

const mockSearchResults: SearchResult[] = [
  { id: 'model1', match_field: 'name', match_value: 'model1' },
  { id: 'model2', match_field: 'aliases', match_value: 'Alias Model' },
  { id: 'model3', match_field: 'jax_id', match_value: 'JAX123' },
  { id: 'model4', match_field: 'rrid', match_value: 'RRID123' },
];

const mockModelsService = {
  searchModels: jest.fn(),
};

async function setup(inputs?: { searchPlaceholder?: string }) {
  const { fixture } = await render(SearchInputComponent, {
    providers: [
      provideHttpClient(),
      provideRouter([]),
      { provide: ModelsService, useValue: mockModelsService },
    ],
    componentInputs: inputs,
  });
  const component = fixture.componentInstance;
  return { component };
}

describe('SearchInputComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockModelsService.searchModels.mockReturnValue(of(mockSearchResults));
  });
  afterAll(() => jest.restoreAllMocks());

  it('should render with default placeholder', async () => {
    await setup();
    const searchInput = screen.getByPlaceholderText('Find model by name...');
    expect(searchInput).toBeInTheDocument();
  });

  it('should render with custom placeholder', async () => {
    await setup({ searchPlaceholder: 'Custom placeholder' });
    const searchInput = screen.getByPlaceholderText('Custom placeholder');
    expect(searchInput).toBeInTheDocument();
  });

  it('should call searchModels when getSearchResults is invoked', async () => {
    const { component } = await setup();
    const result = component.getSearchResults('test query');
    expect(mockModelsService.searchModels).toHaveBeenCalledWith('test query');
    expect(result).toBeDefined();
  });

  it('should format results correctly for different match fields', async () => {
    const { component } = await setup();
    expect(component.formatResultForDisplay(mockSearchResults[0])).toBe('model1');
    expect(component.formatResultForDisplay(mockSearchResults[1])).toBe(
      'model2 (Alias Alias Model)',
    );
    expect(component.formatResultForDisplay(mockSearchResults[2])).toBe('model3 (Jax ID: JAX123)');
    expect(component.formatResultForDisplay(mockSearchResults[3])).toBe('model4 (RRID: RRID123)');
  });

  it('should format unknown match fields with default format', async () => {
    const { component } = await setup();
    const unknownResult: SearchResult = {
      id: 'model5',
      match_field: 'unknown_field',
      match_value: 'some value',
    };
    expect(component.formatResultForDisplay(unknownResult)).toBe('model5');
  });

  it('should return empty string for checkQueryForErrors', async () => {
    const { component } = await setup();
    expect(component.checkQueryForErrors('any query')).toBe('');
  });

  it('should navigate to correct route when navigateToResult is called', async () => {
    const { component } = await setup();
    const navigateSpy = jest.spyOn(component.router, 'navigate');
    component.navigateToResult('model123');
    expect(navigateSpy).toHaveBeenCalledWith(['models', 'model123']);
  });
});
