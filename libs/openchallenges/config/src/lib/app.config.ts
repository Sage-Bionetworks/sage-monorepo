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
  dataUpdatedOn: string;
  keycloakRealm: string;
  isPlatformServer: boolean;
  privacyPolicyUrl: string;
  termsOfUseUrl: string;
}

export const EMPTY_APP_CONFIG = {} as AppConfig;
