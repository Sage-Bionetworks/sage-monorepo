import { Filter } from '@sagebionetworks/openchallenges/ui';
import { contributionRolesFilterValues } from './org-search-filters-values';

export const contributionRolesFilter: Filter = {
  query: 'challengeContributionRoles',
  label: 'Contribution Role',
  values: contributionRolesFilterValues,
  collapsed: false,
};
