import { FilterPanel } from '@sagebionetworks/openchallenges/ui';
import {
  challengeParticipationRolesFilter,
  organizationCategoriesFilter,
} from './org-search-filters';

export const challengeParticipationRolesFilterPanel: FilterPanel = {
  query: 'challengeParticipationRoles',
  label: 'Participation Role',
  options: challengeParticipationRolesFilter,
  collapsed: false,
};

export const organizationCategoriesFilterPanel: FilterPanel = {
  query: 'categories',
  label: 'Category',
  options: organizationCategoriesFilter,
  collapsed: false,
};
