import { Filter } from '@sagebionetworks/openchallenges/ui';
import {
  ChallengeCategoriesOptions,
  ChallengeIncentivesOptions,
  ChallengeStatusOptions,
  ChallengeSubmissionTypesOptions,
  ChallengeSortOptions,
} from '@sagebionetworks/openchallenges/util';

const thisYear = new Date().getFullYear();

const updateYear = (
  thisYear: number,
  startYearDiff: number,
  endYearDiff: number,
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

export const challengeCategoriesFilter: Filter[] = ChallengeCategoriesOptions;
export const challengeIncentivesFilter: Filter[] = ChallengeIncentivesOptions;
export const challengeInputDataTypesFilter: Filter[] = [];
export const challengeOperationsFilter: Filter[] = [];
export const challengeOrganizationsFilter: Filter[] = [];
export const challengeOrganizersFilter: Filter[] = [];
export const challengePlatformsFilter: Filter[] = [];
export const challengeSortFilter: Filter[] = ChallengeSortOptions;
export const challengeStatusFilter: Filter[] = ChallengeStatusOptions;
export const challengeSubmissionTypesFilter: Filter[] =
  ChallengeSubmissionTypesOptions;
