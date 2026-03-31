import { ConfigService } from './config.service';

export const configFactory = (configService: ConfigService) => {
  return () => configService.loadConfig();
};
