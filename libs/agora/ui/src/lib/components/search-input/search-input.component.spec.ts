import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { GeneService, SearchResult } from '@sagebionetworks/agora/api-client';
import { render, screen } from '@testing-library/angular';
import { of } from 'rxjs';
import { SearchInputComponent } from './search-input.component';

const mockSearchResults: SearchResult[] = [
  { id: 'ENSG00000000010', match_field: 'alias', match_value: 'Alias 1', hgnc_symbol: 'GENE1' },
  { id: 'ENSG00000000011', match_field: 'alias', match_value: 'Alias 2', hgnc_symbol: 'GENE1' },
  { id: 'ENSG00000000012', match_field: 'hgnc_symbol', match_value: 'GENE2', hgnc_symbol: 'GENE2' },
  { id: 'ENSG00000000013', match_field: 'hgnc_symbol', match_value: 'GENE3', hgnc_symbol: 'GENE3' },
];

const mockGeneService = {
  searchGeneEnhanced: jest.fn(),
};

async function setup(inputs?: { searchPlaceholder?: string }) {
  const { fixture } = await render(SearchInputComponent, {
    providers: [
      provideHttpClient(),
      provideRouter([]),
      { provide: GeneService, useValue: mockGeneService },
    ],
    componentInputs: inputs,
  });
  const component = fixture.componentInstance;
  return { component };
}

describe('SearchInputComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockGeneService.searchGeneEnhanced.mockReturnValue(of(mockSearchResults));
  });
  afterAll(() => jest.restoreAllMocks());

  it('should render with default placeholder', async () => {
    await setup();
    const searchInput = screen.getByPlaceholderText('Find Gene by Name...');
    expect(searchInput).toBeInTheDocument();
  });

  it('should render with custom placeholder', async () => {
    await setup({ searchPlaceholder: 'Custom placeholder' });
    const searchInput = screen.getByPlaceholderText('Custom placeholder');
    expect(searchInput).toBeInTheDocument();
  });

  describe('getSearchResults', () => {
    it('should call searchGeneEnhanced when getSearchResults is invoked', async () => {
      const { component } = await setup();
      const result = component.getSearchResults('test query');
      expect(mockGeneService.searchGeneEnhanced).toHaveBeenCalledWith('test query');
      expect(result).toBeDefined();
    });
  });

  describe('formatResultForDisplay', () => {
    it('should format results correctly for different match fields', async () => {
      const { component } = await setup();
      expect(component.formatResultForDisplay(mockSearchResults[0])).toBe(
        'GENE1 (Also known as Alias 1)',
      );
      expect(component.formatResultForDisplay(mockSearchResults[1])).toBe(
        'GENE1 (Also known as Alias 2)',
      );
      expect(component.formatResultForDisplay(mockSearchResults[2])).toBe('GENE2');
      expect(component.formatResultForDisplay(mockSearchResults[3])).toBe('GENE3');
    });

    it('should format result without hgnc_symbol', async () => {
      const { component } = await setup();
      const unknownResult: SearchResult = {
        id: 'ENSG00000000014',
        match_field: 'ensembl_gene_id',
        match_value: 'some value',
        hgnc_symbol: 'GENE4',
      };
      expect(component.formatResultForDisplay(unknownResult)).toBe('GENE4');
    });

    it('should format result without hgnc_symbol as fallback to id', async () => {
      const { component } = await setup();
      const resultWithoutSymbol: SearchResult = {
        id: 'ENSG00000000014',
        match_field: 'ensembl_gene_id',
        match_value: 'ENSG00000000014',
        hgnc_symbol: undefined,
      };
      expect(component.formatResultForDisplay(resultWithoutSymbol)).toBe('ENSG00000000014');
    });

    it('should use default case for unknown match fields', async () => {
      const { component } = await setup();
      const unknownFieldResult: SearchResult = {
        id: 'ENSG00000000014',
        match_field: 'unknown_field' as any,
        match_value: 'some value',
        hgnc_symbol: 'GENE4',
      };
      expect(component.formatResultForDisplay(unknownFieldResult)).toBe('ENSG00000000014');
    });
  });

  describe('formatResultSubtextForDisplay', () => {
    it('should format result subtext correctly', async () => {
      const { component } = await setup();
      component.getHgncSymbolCounts(mockSearchResults);
      expect(component.formatResultSubtextForDisplay(mockSearchResults[0])).toBe('ENSG00000000010');
      expect(component.formatResultSubtextForDisplay(mockSearchResults[1])).toBe('ENSG00000000011');
      expect(component.formatResultSubtextForDisplay(mockSearchResults[2])).toBeUndefined();
      expect(component.formatResultSubtextForDisplay(mockSearchResults[3])).toBeUndefined();
    });

    it('should return id for ensembl_gene_id match field', async () => {
      const { component } = await setup();
      const ensemblResult: SearchResult = {
        id: 'ENSG00000000014',
        match_field: 'ensembl_gene_id',
        match_value: 'ENSG00000000014',
        hgnc_symbol: 'GENE4',
      };
      component.getHgncSymbolCounts([ensemblResult]);
      expect(component.formatResultSubtextForDisplay(ensemblResult)).toBe('ENSG00000000014');
    });

    it('should return undefined for result without hgnc_symbol', async () => {
      const { component } = await setup();
      const resultWithoutSymbol: SearchResult = {
        id: 'ENSG00000000014',
        match_field: 'alias',
        match_value: 'some alias',
        hgnc_symbol: undefined,
      };
      component.getHgncSymbolCounts([resultWithoutSymbol]);
      expect(component.formatResultSubtextForDisplay(resultWithoutSymbol)).toBeUndefined();
    });
  });

  describe('checkQueryForErrors', () => {
    it('should return error for invalid Ensembl ID', async () => {
      const { component } = await setup();
      expect(component.checkQueryForErrors('ENSG00000001')).toBe(
        'You must enter a full 15-character value to search for a gene by Ensembl identifier.',
      );
      expect(component.checkQueryForErrors('ENSG00000ABCD12')).toBe(
        'You must enter a full 15-character value to search for a gene by Ensembl identifier.',
      );
    });

    it('should return empty string for valid Ensembl ID', async () => {
      const { component } = await setup();
      expect(component.checkQueryForErrors('ENSG00000000010')).toBe('');
    });

    it('should return empty string for non-Ensembl queries', async () => {
      const { component } = await setup();
      expect(component.checkQueryForErrors('BRCA1')).toBe('');
      expect(component.checkQueryForErrors('some other query')).toBe('');
    });
  });

  describe('navigateToResult', () => {
    it('should navigate to correct route when navigateToResult is called', async () => {
      const { component } = await setup();
      const navigateSpy = jest.spyOn(component.router, 'navigate');
      component.navigateToResult('ENSG00000000010');
      expect(navigateSpy).toHaveBeenCalledWith(['genes', 'ENSG00000000010']);
    });
  });

  describe('getHgncSymbolCounts', () => {
    it('should correctly count hgnc symbols', async () => {
      const { component } = await setup();
      component.getHgncSymbolCounts(mockSearchResults);
      expect(component.hgncSymbolCounts['GENE1']).toBe(2);
      expect(component.hgncSymbolCounts['GENE2']).toBe(1);
      expect(component.hgncSymbolCounts['GENE3']).toBe(1);
    });

    it('should handle empty results', async () => {
      const { component } = await setup();
      component.getHgncSymbolCounts([]);
      expect(component.hgncSymbolCounts).toEqual({});
    });

    it('should handle results with undefined hgnc_symbol', async () => {
      const { component } = await setup();
      const resultsWithUndefined: SearchResult[] = [
        {
          id: 'ENSG00000000014',
          match_field: 'ensembl_gene_id',
          match_value: 'test',
          hgnc_symbol: undefined,
        },
        { id: 'ENSG00000000015', match_field: 'alias', match_value: 'test2', hgnc_symbol: 'GENE1' },
      ];
      component.getHgncSymbolCounts(resultsWithUndefined);
      expect(component.hgncSymbolCounts['GENE1']).toBe(1);
      expect(Object.keys(component.hgncSymbolCounts)).toHaveLength(1);
    });
  });
});
