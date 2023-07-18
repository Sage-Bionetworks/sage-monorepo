import { Filter } from '@sagebionetworks/openchallenges/ui';
import {
  challengeContributionRolesFilterValues,
  organizationCategoriesFilterValues,
} from './org-search-filters-values';

export const challengeContributionRolesFilter: Filter = {
  query: 'challengeContributionRoles',
  label: 'Contribution Role',
  values: challengeContributionRolesFilterValues,
  collapsed: false,
};

export const organizationCategoriesFilter: Filter = {
  query: 'categories',
  label: 'Category',
  values: organizationCategoriesFilterValues,
  collapsed: false,
};
