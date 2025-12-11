import { InjectionToken } from '@angular/core';

export interface AppConfig {
  appVersion: string;
  csrApiUrl: string;
  isPlatformServer: boolean;
  ssrApiUrl: string;
  apiDocsUrl: string;
  // Git tag name used to fetch the corresponding commit SHA from GitHub API for site version display
  // Example: "agora/v1.2.3" or "local" for development
  tagName: string;
  googleTagManagerId: string;
  openRouterApiKey: string;
}

export const EMPTY_APP_CONFIG = {} as AppConfig;

export const APP_PORT = new InjectionToken<string>('APP_PORT');
