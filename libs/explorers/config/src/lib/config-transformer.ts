import { AppConfig } from './config.schema';
import { ClientConfig } from './client-config.schema';

export function transformServerToClientConfig(serverConfig: AppConfig): ClientConfig {
  return {
    environment: serverConfig.environment,
    appVersion: serverConfig.appVersion,
    commitSha: serverConfig.commitSha,
    apiDocsUrl: serverConfig.apiDocsUrl,
    apiBaseUrl: serverConfig.csrApiUrl,
    googleTagManagerId: serverConfig.googleTagManagerId,
    sentryRelease: serverConfig.sentryRelease,
  };
}
