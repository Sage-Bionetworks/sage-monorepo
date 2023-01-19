import { FilterValue } from '@sagebionetworks/openchallenges/ui';

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
    value: undefined,
    label: 'All',
  },
  {
    value: updateYear(thisYear, 1, 1),
    label: (thisYear + 1).toString(),
  },
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

export const challengeStatusFilterValues: FilterValue[] = [
  {
    value: 'active',
    label: 'Active',
  },
  {
    value: 'upcoming',
    label: 'Upcoming',
  },
  {
    value: 'completed',
    label: 'Completed',
  },
];

export const challengeDifficultyFilterValues: FilterValue[] = [
  {
    value: 'good_for_beginners',
    label: 'Good For Beginners',
  },
  {
    value: 'intermediate',
    label: 'Intermediate',
  },
  {
    value: 'advanced',
    label: 'Advanced',
  },
];

export const challengeSubmissionTypesFilterValues: FilterValue[] = [
  {
    value: 'container_image',
    label: 'Container Image',
  },
  {
    value: 'prediction_file',
    label: 'Prediction File',
  },
  {
    value: 'other',
    label: 'Other',
  },
];

export const challengeIncentiveTypesFilterValues: FilterValue[] = [
  {
    value: 'monetary',
    label: 'Monetary',
  },
  {
    value: 'publication',
    label: 'Publication',
  },
  {
    value: 'speaking_engagement',
    label: 'Speaking Engagement',
  },
  {
    value: 'other',
    label: 'Other',
  },
];

export const challengePlatformFilterValues: FilterValue[] = [];

export const challengeInputDataTypeFilterValues: FilterValue[] = [];

export const challengeOrganizationFilterValues: FilterValue[] = [];

export const challengeOrganizerFilterValues: FilterValue[] = [];

export const challengeSortFilterValues: FilterValue[] = [
  {
    value: 'relevance',
    label: 'Relevance',
  },
  // {
  //   value: 'startDate',
  //   label: 'Starting Soon',
  // },
  // {
  //   value: 'endDate',
  //   label: 'Closing Soon',
  // },
  {
    value: 'starred',
    label: 'Most Starred',
  },
];
