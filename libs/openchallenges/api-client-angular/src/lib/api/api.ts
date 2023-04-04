export * from './challenge.service';
import { ChallengeService } from './challenge.service';
export * from './challengeInputDataType.service';
import { ChallengeInputDataTypeService } from './challengeInputDataType.service';
export * from './challengePlatform.service';
import { ChallengePlatformService } from './challengePlatform.service';
export * from './image.service';
import { ImageService } from './image.service';
export * from './organization.service';
import { OrganizationService } from './organization.service';
export * from './user.service';
import { UserService } from './user.service';
export const APIS = [ChallengeService, ChallengeInputDataTypeService, ChallengePlatformService, ImageService, OrganizationService, UserService];
