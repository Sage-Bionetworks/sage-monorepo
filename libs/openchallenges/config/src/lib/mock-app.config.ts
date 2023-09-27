import { AppConfig, Environment } from './app.config';

export const MOCK_APP_CONFIG: AppConfig = {
  environment: Environment.Test,
  ssrApiUrl: '',
  csrApiUrl: '',
  appVersion: '0.0.1',
  dataUpdatedOn: '2023-08-04',
  keycloakRealm: 'test',
  isPlatformServer: false,
  privacyPolicyUrl: '',
  termsOfUseUrl: '',
};
