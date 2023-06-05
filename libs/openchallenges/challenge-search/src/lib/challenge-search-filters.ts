import { Filter } from '@sagebionetworks/openchallenges/ui';
import {
  challengeStartYearRangeFilterValues,
  challengeStatusFilterValues,
  // challengeDifficultyFilterValues,
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

// checkbox filters
export const challengeStatusFilter: Filter = {
  query: 'status',
  label: 'Status',
  values: challengeStatusFilterValues,
  collapsed: false,
};

// export const challengeDifficultyFilter: Filter = {
//   query: 'difficulties',
//   label: 'Difficulty',
//   values: challengeDifficultyFilterValues,
//   collapsed: true,
// };

export const challengeSubmissionTypesFilter: Filter = {
  query: 'submissionTypes',
  label: 'Submission Type',
  values: challengeSubmissionTypesFilterValues,
  collapsed: true,
};

export const challengeIncentiveTypesFilter: Filter = {
  query: 'incentives',
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

// dropdown filters
export const challengeInputDataTypeFilter: Filter = {
  query: 'inputDataTypes',
  label: 'Input Data Type',
  values: challengeInputDataTypeFilterValues,
  // collapsed: false, # TODO Flagged in a merge conflict, commenting out for now
  // showAvatar: false, # TODO Flagged in a merge conflict, commenting out for now
  collapsed: true,
};

export const challengeOrganizationFilter: Filter = {
  query: 'organizations',
  label: 'Organization',
  values: challengeOrganizationFilterValues,
  collapsed: true,
  showAvatar: true,
};

export const challengeOrganizaterFilter: Filter = {
  query: 'organizers',
  label: 'Organizer',
  values: challengeOrganizerFilterValues,
  collapsed: true,
  showAvatar: true,
};
