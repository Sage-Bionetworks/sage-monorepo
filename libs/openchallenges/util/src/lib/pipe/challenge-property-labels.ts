import {
  ChallengeCategory,
  ChallengeIncentive,
  ChallengeSubmissionType,
} from '@sagebionetworks/openchallenges/api-client-angular';

type ChallengeLabels = {
  value: ChallengeSubmissionType | ChallengeIncentive | ChallengeCategory;
  label: string;
};

export const challengeSubmissionTypesLabels: ChallengeLabels[] = [
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

export const challengeIncentivesLabels: ChallengeLabels[] = [
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

export const challengeCategoriesLabels: ChallengeLabels[] = [
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
