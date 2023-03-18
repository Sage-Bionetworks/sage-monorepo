import { Filter } from '@sagebionetworks/openchallenges/ui';
import { contributorRolesFilterValues } from './org-search-filters-values';

export const contributorRolesFilter: Filter = {
  query: 'challengeContributorRoles',
  label: 'Contributor Role',
  values: contributorRolesFilterValues,
  collapsed: false,
};
