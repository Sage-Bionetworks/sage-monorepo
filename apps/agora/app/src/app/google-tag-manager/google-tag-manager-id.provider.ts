import { ConfigService } from '@sagebionetworks/agora/config';

export const googleTagManagerIdProvider = {
  provide: 'googleTagManagerId',
  useFactory: (configService: ConfigService) => configService.config.googleTagManagerId,
  deps: [ConfigService],
};
