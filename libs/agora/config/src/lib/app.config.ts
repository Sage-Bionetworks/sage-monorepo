import { InjectionToken } from '@angular/core';

export {
  AppConfig,
  RuntimeAppConfig,
  RuntimeClientConfig,
  ServerConfig,
} from '@sagebionetworks/explorers/config';

// TODO: remove when app.config.server.ts is updated to use CONFIG_BASE_PATH
export const APP_PORT = new InjectionToken<string>('APP_PORT');
