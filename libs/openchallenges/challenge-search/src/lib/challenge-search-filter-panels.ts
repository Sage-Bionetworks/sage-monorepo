import { FilterPanel } from '@sagebionetworks/openchallenges/ui';
import {
  challengeStartYearRangeFilter,
  challengeStatusFilter,
  challengeInputDataTypesFilter,
  challengeSubmissionTypesFilter,
  challengeIncentivesFilter,
  challengePlatformsFilter,
  challengeOrganizationsFilter,
  challengeOrganizersFilter,
  challengeCategoriesFilter,
} from './challenge-search-filters';

export const challengeStartYearRangeFilterPanel: FilterPanel = {
  query: 'startYearRange',
  label: 'Challenge Year',
  options: challengeStartYearRangeFilter,
  collapsed: false,
};

// checkbox filters
export const challengeStatusFilterPanel: FilterPanel = {
  query: 'status',
  label: 'Status',
  options: challengeStatusFilter,
  collapsed: false,
};

export const challengeSubmissionTypesFilterPanel: FilterPanel = {
  query: 'submissionTypes',
  label: 'Submission Type',
  options: challengeSubmissionTypesFilter,
  collapsed: false,
};

export const challengeIncentivesFilterPanel: FilterPanel = {
  query: 'incentives',
  label: 'Incentive Type',
  options: challengeIncentivesFilter,
  collapsed: false,
};

export const challengePlatformsFilterPanel: FilterPanel = {
  query: 'platforms',
  label: 'Platform',
  options: challengePlatformsFilter,
  collapsed: false,
};

// dropdown filters
export const challengeInputDataTypesFilterPanel: FilterPanel = {
  query: 'inputDataTypes',
  label: 'Input Data Type',
  options: challengeInputDataTypesFilter,
  collapsed: false,
  showAvatar: false,
};

export const challengeCategoriesFilterPanel: FilterPanel = {
  query: 'categories',
  label: 'Category',
  options: challengeCategoriesFilter,
  collapsed: false,
};

export const challengeOrganizationsFilterPanel: FilterPanel = {
  query: 'organizations',
  label: 'Organization',
  options: challengeOrganizationsFilter,
  collapsed: false,
  showAvatar: true,
};

export const challengeOrganizatersFilterPanel: FilterPanel = {
  query: 'organizers',
  label: 'Organizer',
  options: challengeOrganizersFilter,
  collapsed: false,
  showAvatar: true,
};
