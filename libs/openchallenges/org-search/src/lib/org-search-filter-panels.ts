import { FilterPanel } from '@sagebionetworks/openchallenges/ui';
import {
  challengeContributionRolesFilter,
  organizationCategoriesFilter,
} from './org-search-filters';

export const challengeContributionRolesFilterPanel: FilterPanel = {
  query: 'challengeContributionRoles',
  label: 'Contribution Role',
  options: challengeContributionRolesFilter,
  collapsed: false,
};

export const organizationCategoriesFilterPanel: FilterPanel = {
  query: 'categories',
  label: 'Category',
  options: organizationCategoriesFilter,
  collapsed: false,
};
