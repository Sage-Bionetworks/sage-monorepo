export * from './challenge.service';
import { ChallengeService } from './challenge.service';
export * from './challengeAnalytics.service';
import { ChallengeAnalyticsService } from './challengeAnalytics.service';
export * from './challengeContribution.service';
import { ChallengeContributionService } from './challengeContribution.service';
export * from './challengePlatform.service';
import { ChallengePlatformService } from './challengePlatform.service';
export * from './edamConcept.service';
import { EdamConceptService } from './edamConcept.service';
export * from './image.service';
import { ImageService } from './image.service';
export * from './organization.service';
import { OrganizationService } from './organization.service';
export * from './user.service';
import { UserService } from './user.service';
export const APIS = [
  ChallengeService,
  ChallengeAnalyticsService,
  ChallengeContributionService,
  ChallengePlatformService,
  EdamConceptService,
  ImageService,
  OrganizationService,
  UserService,
];
