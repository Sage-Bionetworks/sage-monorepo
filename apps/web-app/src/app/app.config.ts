import { InjectionToken } from '@angular/core';

export enum Environment {}
// Production = 'prod',
// Staging = 'staging',
// Test = 'test',
// Development = 'dev',
// Local = 'local',

export interface AppConfig {
  environment: Environment;
  apiUrl: string;
  appVersion: string;
  seedDatabase: boolean;
}

export const APP_CONFIG = new InjectionToken<AppConfig>('APP_CONFIG');
