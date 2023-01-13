import { Filter } from '@sagebionetworks/challenge-registry/ui';
import {
  challengeStartYearRangeFilterValues,
  challengeStatusFilterValues,
  challengeDifficultyFilterValues,
  challengeInputDataTypeFilterValues,
  challengeSubmissionTypesFilterValues,
  challengeIncentiveTypesFilterValues,
  challengePlatformFilterValues,
  challengeOrganizationFilterValues,
  challengeOrganizerFilterValues,
} from './challenge-search-filters-values';

export const challengeStartYearRangeFilter: Filter = {
  query: 'startYearRange',
  label: 'Challenge Year',
  values: challengeStartYearRangeFilterValues,
  collapsed: false,
};

export const challengeStatusFilter: Filter = {
  query: 'status',
  label: 'Status',
  values: challengeStatusFilterValues,
  collapsed: false,
};

export const challengeDifficultyFilter: Filter = {
  query: 'difficulty',
  label: 'Difficulty',
  values: challengeDifficultyFilterValues,
  collapsed: true,
};

export const challengeSubmissionTypesFilter: Filter = {
  query: 'submissionTypes',
  label: 'Submission Type',
  values: challengeSubmissionTypesFilterValues,
  collapsed: true,
};

export const challengeIncentiveTypesFilter: Filter = {
  query: 'incentiveTypes',
  label: 'Incentive Type',
  values: challengeIncentiveTypesFilterValues,
  collapsed: true,
};

export const challengePlatformFilter: Filter = {
  query: 'platforms',
  label: 'Platform',
  values: challengePlatformFilterValues,
  collapsed: true,
};

export const challengeInputDataTypeFilter: Filter = {
  query: 'inputDataTypes',
  label: 'Input Data Type',
  values: challengeInputDataTypeFilterValues,
  collapsed: false,
};

export const challengeOrganizationFilter: Filter = {
  query: 'organizations',
  label: 'Organization',
  values: challengeOrganizationFilterValues,
  collapsed: true,
};

export const challengeOrganizaterFilter: Filter = {
  query: 'organizers',
  label: 'Organizer',
  values: challengeOrganizerFilterValues,
  collapsed: true,
};
