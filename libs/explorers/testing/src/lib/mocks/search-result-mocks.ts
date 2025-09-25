import { SearchResult } from '@sagebionetworks/explorers/models';
import { delay, Observable, of } from 'rxjs';

const dummySearchResults: SearchResult[] = [
  {
    id: 'dummy_id',
    match_field: 'id',
    match_value: 'Dummy Name',
  },
  {
    id: 'dummy_id_2',
    match_field: 'aliases',
    match_value: 'Alternate Name',
  },
];

const dummyEmptySearchResults: SearchResult[] = [];

export function mockNavigateToResult(id: string): void {
  alert(`Navigating to ${id}`);
}

export function mockGetSearchResults(query: string): Observable<SearchResult[]> {
  if (query === 'notfound' || query === 'ensg00000000000') {
    return of(dummyEmptySearchResults).pipe(delay(2000));
  }
  return of(dummySearchResults).pipe(delay(1000));
}

export function mockGetNoSearchResultsMessage(query: string): string {
  if (isEnsemblId(query) && query.length === 15) {
    return 'Unable to find a matching gene. Try searching by gene symbol.';
  }
  return 'No results found. Try searching by the Ensembl Gene ID.';
}

function isEnsemblId(query: string): boolean {
  return 'ensg' === query.toLowerCase().substring(0, 4);
}

export function mockCheckQueryForErrors(query: string): string {
  if (isEnsemblId(query)) {
    const digits = query.toLowerCase().substring(4, query.length);
    if (digits.length !== 11 || !/^\d+$/.test(digits)) {
      return 'You must enter a full 15-character value to search for a gene by Ensembl identifier.';
    }
  }
  return '';
}

export function mockFormatResultSubtextForDisplay(result: SearchResult): string | undefined {
  return result.match_value;
}
