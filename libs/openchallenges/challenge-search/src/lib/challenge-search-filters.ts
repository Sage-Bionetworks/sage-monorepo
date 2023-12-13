import {
  ChallengeCategory,
  ChallengeDifficulty,
  ChallengeIncentive,
  ChallengeSort,
  ChallengeStatus,
  ChallengeSubmissionType,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Filter } from '@sagebionetworks/openchallenges/ui';

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

export const challengeDifficultyFilter: Filter[] = [
  {
    value: ChallengeDifficulty.GoodForBeginners,
    label: 'Good For Beginners',
  },
  {
    value: ChallengeDifficulty.Intermediate,
    label: 'Intermediate',
  },
  {
    value: ChallengeDifficulty.Advanced,
    label: 'Advanced',
  },
];

export const challengeSubmissionTypesFilter: Filter[] = [
  {
    value: ChallengeSubmissionType.ContainerImage,
    label: 'Container Image',
  },
  {
    value: ChallengeSubmissionType.PredictionFile,
    label: 'Prediction File',
  },
  {
    value: ChallengeSubmissionType.Notebook,
    label: 'Notebook',
  },
  {
    value: ChallengeSubmissionType.Other,
    label: 'Other',
  },
];

export const challengeIncentivesFilter: Filter[] = [
  {
    value: ChallengeIncentive.Monetary,
    label: 'Monetary',
  },
  {
    value: ChallengeIncentive.Publication,
    label: 'Publication',
  },
  {
    value: ChallengeIncentive.SpeakingEngagement,
    label: 'Speaking Engagement',
  },
  {
    value: ChallengeIncentive.Other,
    label: 'Other',
  },
];

export const challengePlatformsFilter: Filter[] = [];

export const challengeInputDataTypesFilter: Filter[] = [];

export const challengeCategoriesFilter: Filter[] = [
  {
    value: ChallengeCategory.Featured,
    label: 'Featured',
  },
  {
    value: ChallengeCategory.Benchmark,
    label: 'Continuous Benchmark',
  },
  {
    value: ChallengeCategory.Hackathon,
    label: 'Hackathon',
  },
  {
    value: ChallengeCategory.StartingSoon,
    label: 'Starting Soon',
  },
  {
    value: ChallengeCategory.EndingSoon,
    label: 'Closing Soon',
  },
  {
    value: ChallengeCategory.RecentlyStarted,
    label: 'Recently Launched',
  },
  {
    value: ChallengeCategory.RecentlyEnded,
    label: 'Recently Completed',
  },
];

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
