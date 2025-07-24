export * from './aPIKey.service';
import { APIKeyService } from './aPIKey.service';
export * from './authentication.service';
import { AuthenticationService } from './authentication.service';
export * from './challenge.service';
import { ChallengeService } from './challenge.service';
export * from './challengeAnalytics.service';
import { ChallengeAnalyticsService } from './challengeAnalytics.service';
export * from './challengeContribution.service';
import { ChallengeContributionService } from './challengeContribution.service';
export * from './challengeParticipation.service';
import { ChallengeParticipationService } from './challengeParticipation.service';
export * from './challengePlatform.service';
import { ChallengePlatformService } from './challengePlatform.service';
export * from './edamConcept.service';
import { EdamConceptService } from './edamConcept.service';
export * from './image.service';
import { ImageService } from './image.service';
export * from './organization.service';
import { OrganizationService } from './organization.service';
export const APIS = [
  APIKeyService,
  AuthenticationService,
  ChallengeService,
  ChallengeAnalyticsService,
  ChallengeContributionService,
  ChallengeParticipationService,
  ChallengePlatformService,
  EdamConceptService,
  ImageService,
  OrganizationService,
];
