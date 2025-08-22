export * from './api-key.service';
import { APIKeyService } from './api-key.service';
export * from './authentication.service';
import { AuthenticationService } from './authentication.service';
export * from './challenge.service';
import { ChallengeService } from './challenge.service';
export * from './challenge-analytics.service';
import { ChallengeAnalyticsService } from './challenge-analytics.service';
export * from './challenge-contribution.service';
import { ChallengeContributionService } from './challenge-contribution.service';
export * from './challenge-platform.service';
import { ChallengePlatformService } from './challenge-platform.service';
export * from './edam-concept.service';
import { EdamConceptService } from './edam-concept.service';
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
  ChallengePlatformService,
  EdamConceptService,
  ImageService,
  OrganizationService,
];
