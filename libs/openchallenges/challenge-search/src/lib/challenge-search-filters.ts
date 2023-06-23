import { Filter } from '@sagebionetworks/openchallenges/ui';
import {
  challengeStartYearRangeFilterValues,
  challengeStatusFilterValues,
  // challengeDifficultyFilterValues,
  challengeInputDataTypesFilterValues,
  challengeSubmissionTypesFilterValues,
  challengeIncentiveTypesFilterValues,
  challengePlatformsFilterValues,
  challengeOrganizationsFilterValues,
  challengeOrganizersFilterValues,
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

export const challengePlatformsFilter: Filter = {
  query: 'platforms',
  label: 'Platform',
  values: challengePlatformsFilterValues,
  collapsed: true,
};

// dropdown filters
export const challengeInputDataTypesFilter: Filter = {
  query: 'inputDataTypes',
  label: 'Input Data Type',
  values: challengeInputDataTypesFilterValues,
  collapsed: false,
  showAvatar: false,
};

export const challengeOrganizationsFilter: Filter = {
  query: 'organizations',
  label: 'Organization',
  values: challengeOrganizationsFilterValues,
  collapsed: true,
  showAvatar: true,
};

export const challengeOrganizatersFilter: Filter = {
  query: 'organizers',
  label: 'Organizer',
  values: challengeOrganizersFilterValues,
  collapsed: true,
  showAvatar: true,
};
