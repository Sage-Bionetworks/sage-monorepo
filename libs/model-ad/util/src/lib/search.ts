import { SearchResult } from '@sagebionetworks/explorers/models';
import { EMPTY, Observable } from 'rxjs';

export const navigateToResult = (id: string): void => {
  alert(`Navigating to result with ID: ${id}`);
};

export const getSearchResults = (query: string): Observable<SearchResult[]> => {
  return EMPTY;
};

export const checkQueryForErrors = (query: string): string => {
  return '';
};
