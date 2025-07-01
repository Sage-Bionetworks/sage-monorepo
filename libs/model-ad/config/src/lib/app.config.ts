import { InjectionToken } from '@angular/core';

/* eslint-disable no-unused-vars */
export enum Environment {
  Production = 'prod',
  Staging = 'staging',
  Test = 'test',
  Development = 'dev',
  Local = 'local',
}
/* eslint-enable no-unused-vars */

export interface AppConfig {
  appVersion: string;
  csrApiUrl: string;
  dataUpdatedOn: string;
  environment: Environment;
  googleTagManagerId: string;
  isPlatformServer: boolean;
  privacyPolicyUrl: string;
  ssrApiUrl: string;
  termsOfUseUrl: string;
  apiDocsUrl: string;
}

export const EMPTY_APP_CONFIG = {} as AppConfig;

export const APP_PORT = new InjectionToken<string>('APP_PORT');
