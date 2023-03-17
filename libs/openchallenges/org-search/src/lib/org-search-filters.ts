import { Filter } from '@sagebionetworks/openchallenges/ui';
import { challengeContributionRolesFilterValues } from './org-search-filters-values';

export const contributionRolesFilter: Filter = {
  query: 'challengeContributorRoles',
  label: 'Contribution Role',
  values: challengeContributionRolesFilterValues,
  collapsed: false,
};
