import { Filter } from '@sagebionetworks/openchallenges/ui';
import { organizationRolesFilterValues } from './org-search-filters-values';

export const organizationRolesFilter: Filter = {
  query: 'roles',
  label: 'Role',
  values: organizationRolesFilterValues,
  collapsed: false,
};
