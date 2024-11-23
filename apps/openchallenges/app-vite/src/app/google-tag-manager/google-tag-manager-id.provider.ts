import { ConfigService } from '@sagebionetworks/openchallenges/config';

export const googleTagManagerIdProvider = {
  provide: 'googleTagManagerId',
  useFactory: (configService: ConfigService) => configService.config.googleTagManagerId,
  deps: [ConfigService],
};
