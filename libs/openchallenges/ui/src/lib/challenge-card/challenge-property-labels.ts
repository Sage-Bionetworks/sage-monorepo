import {
  ChallengeIncentive,
  ChallengeSubmissionType,
} from '@sagebionetworks/openchallenges/api-client-angular';

export const ChallengeIncentiveLabels: Record<ChallengeIncentive, string> = {
  monetary: 'Monetary',
  publication: 'Publication',
  speaking_engagement: 'Speaking Engagement',
  other: 'Other',
};

export const ChallengeSubmissionTypeLabels: Record<
  ChallengeSubmissionType,
  string
> = {
  container_image: 'Container Image',
  prediction_file: 'Prediction File',
  notebook: 'Notebook',
  mlcube: 'MLCube',
  other: 'Other',
};
