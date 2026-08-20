import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { SearchResult, SearchService } from '@sagebionetworks/model-ad/api-client';
import { render, screen } from '@testing-library/angular';
import { of } from 'rxjs';
import { SearchInputComponent } from './search-input.component';

const mockSearchResults: SearchResult[] = [
  { id: 'model1', match_field: 'name', match_value: 'model1', model_organism: 'mouse' },
  { id: 'model2', match_field: 'aliases', match_value: 'Alias Model', model_organism: 'mouse' },
  { id: 'model3', match_field: 'jax_id', match_value: 'JAX123', model_organism: 'mouse' },
  { id: 'model4', match_field: 'rrid', match_value: 'RRID123', model_organism: 'mouse' },
];

const mockSearchService = {
  searchModels: jest.fn(),
};

async function setup(inputs?: { searchPlaceholder?: string }) {
  const { fixture } = await render(SearchInputComponent, {
    providers: [
      provideHttpClient(),
      provideRouter([]),
      { provide: SearchService, useValue: mockSearchService },
    ],
    componentInputs: inputs,
  });
  const component = fixture.componentInstance;
  return { component };
}

describe('SearchInputComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockSearchService.searchModels.mockReturnValue(of(mockSearchResults));
  });
  afterAll(() => jest.restoreAllMocks());

  it('should render with default placeholder', async () => {
    await setup();
    const searchInput = screen.getByPlaceholderText('Find model by name or ID...');
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
    expect(mockSearchService.searchModels).toHaveBeenCalledWith('test query', undefined);
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
      model_organism: 'mouse',
    };
    expect(component.formatResultForDisplay(unknownResult)).toBe('model5');
  });

  it('should return empty string for checkQueryForErrors', async () => {
    const { component } = await setup();
    expect(component.checkQueryForErrors('any query')).toBe('');
  });

  it('should navigate with modelOrganism query param', async () => {
    const { component } = await setup();
    const navigateSpy = jest.spyOn(component.router, 'navigate');
    component.navigateToResult(mockSearchResults[0]);
    expect(navigateSpy).toHaveBeenCalledWith(['models', 'model1'], {
      queryParams: { modelOrganism: 'mouse' },
    });
  });

  it('should navigate with marmoset modelOrganism for marmoset results', async () => {
    const { component } = await setup();
    const navigateSpy = jest.spyOn(component.router, 'navigate');
    const marmosetResult: SearchResult = {
      id: 'Presenilin 1',
      match_field: 'name',
      match_value: 'Presenilin 1',
      model_organism: 'marmoset',
    };
    component.navigateToResult(marmosetResult);
    expect(navigateSpy).toHaveBeenCalledWith(['models', 'Presenilin 1'], {
      queryParams: { modelOrganism: 'marmoset' },
    });
  });

  it('should return mouse icon for mouse results', async () => {
    const { component } = await setup();
    const icon = component.getResultIcon(mockSearchResults[0]);
    expect(icon).toEqual({
      imagePath: 'model-ad-assets/images/mouse-head.svg',
      label: 'mouse',
    });
  });

  it('should return marmoset icon for marmoset results', async () => {
    const { component } = await setup();
    const marmosetResult: SearchResult = {
      id: 'Presenilin 1',
      match_field: 'name',
      match_value: 'Presenilin 1',
      model_organism: 'marmoset',
    };
    const icon = component.getResultIcon(marmosetResult);
    expect(icon).toEqual({
      imagePath: 'model-ad-assets/images/marmoset-head.svg',
      label: 'marmoset',
    });
  });
});
