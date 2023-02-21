export interface OrganizationSearchQuery {
  pageNumber?: number;
  pageSize?: number;
  sort?: string;
  direction?: string | null;
  searchTerms?: string;
}
