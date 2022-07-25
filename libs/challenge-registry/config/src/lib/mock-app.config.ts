import { AppConfig, Environment } from './app.config';

export const MOCK_APP_CONFIG: AppConfig = {
  environment: Environment.Test,
  apiUrl: 'http://localhost:4200/api',
  appVersion: '0.0.1',
  keycloakRealm: 'test',
};
