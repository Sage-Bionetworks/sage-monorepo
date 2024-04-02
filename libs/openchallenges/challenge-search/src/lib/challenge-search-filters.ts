import {
  ChallengeSort,
  ChallengeStatus,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Filter } from '@sagebionetworks/openchallenges/ui';
import {
  challengeCategoriesLabels,
  challengeIncentivesLabels,
  challengeSubmissionTypesLabels,
} from '@sagebionetworks/openchallenges/util';

const thisYear = new Date().getFullYear();

const updateYear = (
  thisYear: number,
  startYearDiff: number,
  endYearDiff: number
) => {
  return {
    start: `${thisYear + startYearDiff}-01-01`,
    end: `${thisYear + endYearDiff}-12-31`,
  };
};

export const challengeStartYearRangeFilter: Filter[] = [
  {
    value: undefined,
    label: 'All',
  },
  // {
  //   value: updateYear(thisYear, 1, 1),
  //   label: (thisYear + 1).toString(),
  // },
  {
    value: updateYear(thisYear, 0, 0),
    label: thisYear.toString(),
  },
  {
    value: updateYear(thisYear, -1, -1),
    label: (thisYear - 1).toString(),
  },
  {
    value: updateYear(thisYear, -6, -2),
    label: thisYear - 6 + ' - ' + (thisYear - 2),
  },
  {
    value: updateYear(thisYear, -11, -7),
    label: thisYear - 11 + ' - ' + (thisYear - 7),
  },
  {
    value: updateYear(thisYear, -21, -12),
    label: thisYear - 21 + ' - ' + (thisYear - 12),
  },
  {
    value: 'custom',
    label: 'Custom',
  },
];

export const challengeStatusFilter: Filter[] = [
  {
    value: ChallengeStatus.Active,
    label: 'Active',
  },
  {
    value: ChallengeStatus.Upcoming,
    label: 'Upcoming',
  },
  {
    value: ChallengeStatus.Completed,
    label: 'Completed',
  },
];

export const challengeSubmissionTypesFilter: Filter[] =
  challengeSubmissionTypesLabels;

export const challengeIncentivesFilter: Filter[] = challengeIncentivesLabels;

export const challengePlatformsFilter: Filter[] = [];

export const challengeInputDataTypesFilter: Filter[] = [];

export const challengeCategoriesFilter: Filter[] = challengeCategoriesLabels;

export const challengeOrganizationsFilter: Filter[] = [];

export const challengeOrganizersFilter: Filter[] = [];

export const challengeSortFilter: Filter[] = [
  {
    value: ChallengeSort.Relevance,
    label: 'Relevance',
  },
  {
    value: ChallengeSort.StartDate,
    label: 'Start Date',
  },
  {
    value: ChallengeSort.Starred,
    label: 'Most Starred',
  },
];
