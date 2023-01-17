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
    value: 'good_for_beginners',
    label: 'Good For Beginners',
    active: false,
  },
  {
    value: 'intermediate',
    label: 'Intermediate',
    active: false,
  },
  {
    value: 'advanced',
    label: 'Advanced',
    active: false,
  },
];

export const challengeSubmissionTypesFilterValues: FilterValue[] = [
  {
    value: 'container_image',
    label: 'Container Image',
    active: false,
  },
  {
    value: 'prediction_file',
    label: 'Prediction File',
    active: false,
  },
  {
    value: 'other',
    label: 'Other',
    active: false,
  },
];

export const challengeIncentiveTypesFilterValues: FilterValue[] = [
  {
    value: 'monetary',
    label: 'Monetary',
    active: false,
  },
  {
    value: 'publication',
    label: 'Publication',
    active: false,
  },
  {
    value: 'speaking_engagement',
    label: 'Speaking Engagement',
    active: false,
  },
  {
    value: 'other',
    label: 'Other',
    active: false,
  },
];

export const challengePlatformFilterValues: FilterValue[] = [];

export const challengeInputDataTypeFilterValues: FilterValue[] = [];

export const challengeOrganizationFilterValues: FilterValue[] = [];

export const challengeOrganizerFilterValues: FilterValue[] = [];

export const challengeSortFilterValues: FilterValue[] = [
  {
    value: '-startDate',
    label: 'Starting Soon',
    active: false,
  },
  {
    value: '-endDate',
    label: 'Closing Soon',
    active: false,
  },
  {
    value: '-starredCount',
    label: 'Most Starred',
    active: false,
  },
];
