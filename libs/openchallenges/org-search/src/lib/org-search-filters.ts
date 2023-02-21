import { Filter } from '@sagebionetworks/openchallenges/ui';
import { organizationTypesFilterValues } from './org-search-filters-values';

export const organizationTypesFilter: Filter = {
  query: 'types',
  label: 'Type',
  values: organizationTypesFilterValues,
  collapsed: false,
};
