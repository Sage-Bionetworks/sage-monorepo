import { Filter } from '@sagebionetworks/challenge-registry/ui';
import {
  challengeStartYearRangeFilterValues,
  challengeStatusFilterValues,
  challengeDifficultyFilterValues,
  challengeInputDataTypeFilterValues,
  challengeSubmissionTypesFilterValues,
  challengeIncentiveTypesFilterValues,
  challengePlatformFilterValues,
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
  collapsed: true,
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
  collapsed: true,
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
