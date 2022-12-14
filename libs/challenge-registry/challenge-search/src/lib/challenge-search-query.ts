import {
  // ChallengeDifficulty,
  // ChallengeIncentiveType,
  // ChallengeStatus,
  // ChallengeSubmissionType,
  DateRange,
} from '@sagebionetworks/api-client-angular-deprecated';

export interface ChallengeSearchQuery {
  limit?: number;
  offset?: number;
  // sort?: 'createdAt' | 'updatedAt';
  // direction?: 'asc' | 'desc';
  // searchTerms?: string;
  // topics?: string[];
  // status?: ChallengeStatus[];
  // platformIds?: string[];
  startYearRange?: DateRange;
  // startDateRange?: DateRange;
  // inputDataTypes?: string[];
  // difficulty?: ChallengeDifficulty[];
  // submissionTypes?: ChallengeSubmissionType[];
  // incentiveTypes?: ChallengeIncentiveType[];
  // orgIds?: string[];
  // organizerIds?: string[];
  // sponsorIds?: string[];
}
