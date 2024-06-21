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
  enableOperationFilter: boolean;
  appVersion: string;
  csrApiUrl: string;
  dataUpdatedOn: string;
  environment: Environment;
  googleTagManagerId: string;
  isPlatformServer: boolean;
  keycloakRealm: string;
  privacyPolicyUrl: string;
  ssrApiUrl: string;
  termsOfUseUrl: string;
  apiDocsUrl: string;
}

export const EMPTY_APP_CONFIG = {} as AppConfig;
