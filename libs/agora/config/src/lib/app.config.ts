import { InjectionToken } from '@angular/core';

export interface AppConfig {
  apiDocsUrl: string;
  appVersion: string;
  commitSha: string;
  csrApiUrl: string;
  ssrApiUrl: string;
  googleTagManagerId: string;
  isPlatformServer: boolean;
  sentryDSN: string;
  sentryEnvironment: string;
  sentryRelease: string;
}

export const EMPTY_APP_CONFIG = {} as AppConfig;

export const APP_PORT = new InjectionToken<string>('APP_PORT');
