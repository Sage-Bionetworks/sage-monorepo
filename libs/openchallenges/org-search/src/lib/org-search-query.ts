import {
  ChallengePlatform,
  ChallengeStatus,
  ChallengeDifficulty,
  ChallengeIncentiveType,
  ChallengeSubmissionType,
  DateRange,
  Challenge,
} from '@sagebionetworks/openchallenges/api-client-angular-deprecated';

export interface ChallengeSearchQuery {
  limit?: number;
  offset?: number;
  sort?: keyof Challenge;
  // direction?: 'asc' | 'desc';
  searchTerms?: string;
  // topics?: string[];
  status?: ChallengeStatus[];
  platforms?: ChallengePlatform[]; // assume to query platform name instead of ids
  startYearRange?: DateRange;
  inputDataTypes?: string[];
  difficulty?: ChallengeDifficulty[];
  submissionTypes?: ChallengeSubmissionType[];
  incentiveTypes?: ChallengeIncentiveType[];
  organizations?: string[];
  organizers?: string[];
  // sponsorIds?: string[];
}
