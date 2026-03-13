import { InjectionToken } from '@angular/core';

export interface AppConfig {
  appVersion: string;
  commitSha: string;
  apiDocsUrl: string;
  csrApiUrl: string;
  ssrApiUrl: string;
  googleTagManagerId: string;
  isPlatformServer: boolean;
}

export const EMPTY_APP_CONFIG = {} as AppConfig;

export const APP_PORT = new InjectionToken<string>('APP_PORT');
