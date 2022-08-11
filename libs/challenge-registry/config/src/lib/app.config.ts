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
  environment: Environment;
  apiUrl: string;
  appVersion: string;
}

export const APP_CONFIG = new InjectionToken<AppConfig>('APP_CONFIG');

export const EMPTY_APP_CONFIG = {} as AppConfig;
