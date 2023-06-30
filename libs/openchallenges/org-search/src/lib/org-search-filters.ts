import { Filter } from '@sagebionetworks/openchallenges/ui';
import { challengeContributionRolesFilterValues } from './org-search-filters-values';

export const challengeContributionRolesFilter: Filter = {
  query: 'challengeContributionRoles',
  label: 'Contribution Role',
  values: challengeContributionRolesFilterValues,
  collapsed: false,
};
