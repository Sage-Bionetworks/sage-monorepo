import { RuntimeServerConfig } from '@sagebionetworks/agora/config';

export const configMock: RuntimeServerConfig = {
  appVersion: 'local',
  commitSha: '',
  csrApiUrl: 'http://localhost:4200/v1',
  ssrApiUrl: 'http://agora-api:3333/v1',
  apiDocsUrl: 'http://localhost:8000/api-docs',
  googleTagManagerEnabled: false,
  googleTagManagerId: '',
  sentryDsn: '',
  sentryEnvironment: '',
  sentryRelease: '',
  environment: 'dev',
  isPlatformServer: true,
};
