import {
  ChallengeCategory,
  ChallengeIncentive,
  ChallengeSort,
  ChallengeStatus,
  ChallengeSubmissionType,
} from '@sagebionetworks/openchallenges/api-client';

type ChallengePropertyOption = {
  value:
    | ChallengeCategory
    | ChallengeIncentive
    | ChallengeSort
    | ChallengeStatus
    | ChallengeSubmissionType;
  label: string;
};

export const ChallengeCategoriesOptions: ChallengePropertyOption[] = [
  {
    value: ChallengeCategory.Featured,
    label: 'Featured',
  },
  {
    value: ChallengeCategory.Benchmark,
    label: 'Benchmark',
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

export const ChallengeIncentivesOptions: ChallengePropertyOption[] = [
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

export const ChallengeSortOptions: ChallengePropertyOption[] = [
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

export const ChallengeStatusOptions: ChallengePropertyOption[] = [
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

export const ChallengeSubmissionTypesOptions: ChallengePropertyOption[] = [
  {
    value: ChallengeSubmissionType.ContainerImage,
    label: 'Container Image',
  },
  {
    value: ChallengeSubmissionType.Mlcube,
    label: 'MLCube',
  },
  {
    value: ChallengeSubmissionType.Notebook,
    label: 'Notebook',
  },
  {
    value: ChallengeSubmissionType.PredictionFile,
    label: 'Prediction File',
  },
  {
    value: ChallengeSubmissionType.Other,
    label: 'Other',
  },
];
