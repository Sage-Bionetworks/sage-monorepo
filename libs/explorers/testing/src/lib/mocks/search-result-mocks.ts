import { SearchResult } from '@sagebionetworks/explorers/models';
import { delay, Observable, of } from 'rxjs';

const dummySearchResults: SearchResult[] = [
  {
    name: 'Dummy Name',
    alias: ['Dummy Alias', 'Another Alias'],
    id: 'dummy_id',
  },
  {
    name: 'Dummy Name 2',
    alias: ['Dummy Alias 2', 'Another Alias 2'],
    id: 'dummy_id_2',
  },
];

const dummyEmptySearchResults: SearchResult[] = [];

export function mockNavigateToResult(id: string): void {
  alert(`Navigating to ${id}`);
}

export function mockGetSearchResults(query: string): Observable<SearchResult[]> {
  if (query === 'notfound') {
    return of(dummyEmptySearchResults).pipe(delay(2000));
  }
  return of(dummySearchResults).pipe(delay(1000));
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
