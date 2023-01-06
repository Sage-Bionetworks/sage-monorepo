import { FilterValue } from '@sagebionetworks/challenge-registry/ui';

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

export const challengeStartYearRangeFilterValues: FilterValue[] = [
  {
    value: updateYear(thisYear, -30, 10),
    label: 'All',
    active: true,
  },
  {
    value: updateYear(thisYear, 1, 1),
    label: (thisYear + 1).toString(),
    active: false,
  },
  {
    value: updateYear(thisYear, 0, 0),
    label: thisYear.toString(),
    active: false,
  },
  {
    value: updateYear(thisYear, -1, -1),
    label: (thisYear - 1).toString(),
    active: false,
  },
  {
    value: updateYear(thisYear, -6, -2),
    label: thisYear - 6 + ' - ' + (thisYear - 2),
    active: false,
  },
  {
    value: updateYear(thisYear, -11, -7),
    label: thisYear - 11 + ' - ' + (thisYear - 7),
    active: false,
  },
  {
    value: updateYear(thisYear, -21, -12),
    label: thisYear - 21 + ' - ' + (thisYear - 12),
    active: false,
  },
  {
    value: 'custom',
    label: 'Custom',
    active: false,
  },
];

export const challengeStatusFilterValues: FilterValue[] = [
  {
    value: 'active',
    label: 'Active',
    active: false,
  },
  {
    value: 'upcoming',
    label: 'Upcoming',
    active: false,
  },
  {
    value: 'completed',
    label: 'Completed',
    active: false,
  },
];

export const challengeDifficultyFilterValues: FilterValue[] = [
  {
    value: 'GoodForBeginners',
    label: 'Good For Beginners',
    active: false,
  },
  {
    value: 'Intermediate',
    label: 'Intermediate',
    active: false,
  },
  {
    value: 'Advanced',
    label: 'Advanced',
    active: false,
  },
];

export const challengeSubmissionTypesFilterValues: FilterValue[] = [
  {
    value: 'DockerImage',
    label: 'Docker Image',
    active: false,
  },
  {
    value: 'PredictionFile',
    label: 'Prediction File',
    active: false,
  },
  {
    value: 'Other',
    label: 'Other',
    active: false,
  },
];

export const challengeInputDataTypeFilterValues: FilterValue[] = [];

export const challengeIncentiveTypesFilterValues: FilterValue[] = [
  {
    value: 'Monetary',
    label: 'Monetary',
    active: false,
  },
  {
    value: 'Publication',
    label: 'Publication',
    active: false,
  },
  {
    value: 'SpeakingEngagement',
    label: 'Speaking Engagement',
    active: false,
  },
  {
    value: 'Other',
    label: 'Other',
    active: false,
  },
];

export const challengePlatformFilterValues: FilterValue[] = [];

export const challengeOrganizationFilterValues: FilterValue[] = [];

export const challengeSortFilterValues: FilterValue[] = [
  {
    value: 'startDate',
    label: 'Starting Soon',
    active: false,
  },
  {
    value: 'endDate',
    label: 'Closing Soon',
    active: false,
  },
  {
    value: 'starredCount',
    label: 'Most Starred',
    active: false,
  },
];
