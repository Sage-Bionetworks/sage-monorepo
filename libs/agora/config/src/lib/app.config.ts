import { InjectionToken } from '@angular/core';

export interface AppConfig {
  appVersion: string;
  commitSha: string;
  csrApiUrl: string;
  isPlatformServer: boolean;
  ssrApiUrl: string;
  apiDocsUrl: string;
  googleTagManagerId: string;
}

export const EMPTY_APP_CONFIG = {} as AppConfig;

export const APP_PORT = new InjectionToken<string>('APP_PORT');
