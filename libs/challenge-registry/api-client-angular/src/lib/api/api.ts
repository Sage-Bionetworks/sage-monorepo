export * from './challenge.service';
import { ChallengeService } from './challenge.service';
export * from './challengePlatform.service';
import { ChallengePlatformService } from './challengePlatform.service';
export * from './organization.service';
import { OrganizationService } from './organization.service';
export * from './user.service';
import { UserService } from './user.service';
export const APIS = [ChallengeService, ChallengePlatformService, OrganizationService, UserService];
