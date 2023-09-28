import { ConfigService } from '@sagebionetworks/openchallenges/config';

export const googleTagManagerIdFactory = (
  configService: ConfigService
): string => {
  console.log(`GTM ID in Factory: ${configService.config.googleTagManagerId}`);
  return configService.config.googleTagManagerId;
};
