export interface SearchResult {
  name: string;
  alias: string[];
  id: string;
}

export interface SearchResultsList {
  items?: Array<SearchResult>;
}
