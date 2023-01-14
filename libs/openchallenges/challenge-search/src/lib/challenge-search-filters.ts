import { Filter } from '@sagebionetworks/openchallenges/ui';
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
  queryName: 'startYearRange',
  label: 'Challenge Year',
  values: challengeStartYearRangeFilterValues,
  collapsed: false,
};

export const challengeStatusFilter: Filter = {
  queryName: 'status',
  label: 'Status',
  values: challengeStatusFilterValues,
  collapsed: false,
};

export const challengeDifficultyFilter: Filter = {
  queryName: 'difficulty',
  label: 'Difficulty',
  values: challengeDifficultyFilterValues,
  collapsed: true,
};

export const challengeInputDataTypeFilter: Filter = {
  queryName: 'inputDataTypes',
  label: 'Input Data Type',
  values: challengeInputDataTypeFilterValues,
  collapsed: false,
};

export const challengeSubmissionTypesFilter: Filter = {
  queryName: 'submissionTypes',
  label: 'Submission Type',
  values: challengeSubmissionTypesFilterValues,
  collapsed: true,
};

export const challengeIncentiveTypesFilter: Filter = {
  queryName: 'incentiveTypes',
  label: 'Incentive Type',
  values: challengeIncentiveTypesFilterValues,
  collapsed: true,
};

export const challengePlatformFilter: Filter = {
  queryName: 'platforms',
  label: 'Platform',
  values: challengePlatformFilterValues,
  collapsed: true,
};

export const challengeOrganizationFilter: Filter = {
  queryName: 'organizations',
  label: 'Organization',
  values: challengeOrganizationFilterValues,
  collapsed: true,
};

export const challengeOrganizaterFilter: Filter = {
  queryName: 'organizers',
  label: 'Organizer',
  values: challengeOrganizerFilterValues,
  collapsed: true,
};
