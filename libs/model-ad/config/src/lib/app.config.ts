import { InjectionToken } from '@angular/core';

export interface AppConfig {
  appVersion: string;
  csrApiUrl: string;
  googleTagManagerId: string;
  isPlatformServer: boolean;
  privacyPolicyUrl: string;
  ssrApiUrl: string;
  // Git tag name used to fetch the corresponding commit SHA from GitHub API for site version display
  // Example: "model-ad/v1.2.3" or "local" for development
  tagName: string;
  termsOfUseUrl: string;
  apiDocsUrl: string;
}

export const EMPTY_APP_CONFIG = {} as AppConfig;

export const APP_PORT = new InjectionToken<string>('APP_PORT');
