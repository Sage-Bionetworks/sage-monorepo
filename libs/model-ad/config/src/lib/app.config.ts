import { InjectionToken } from '@angular/core';

export interface AppConfig {
  appVersion: string;
  csrApiUrl: string;
  googleTagManagerId: string;
  isPlatformServer: boolean;
  privacyPolicyUrl: string;
  ssrApiUrl: string;
  tagName: string;
  termsOfUseUrl: string;
  apiDocsUrl: string;
}

export const EMPTY_APP_CONFIG = {} as AppConfig;

export const APP_PORT = new InjectionToken<string>('APP_PORT');
