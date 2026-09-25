import { RuntimeServerConfig } from '@sagebionetworks/qtl/config';

export const configMock: RuntimeServerConfig = {
  appVersion: 'local',
  commitSha: '',
  csrApiUrl: 'http://localhost:4200/v1',
  ssrApiUrl: 'http://qtl-api:3333/v1',
  apiDocsUrl: 'http://localhost:8000/api-docs',
  googleTagManagerEnabled: false,
  googleTagManagerId: '',
  sentryDsn: '',
  sentryEnvironment: '',
  sentryRelease: '',
  environment: 'dev',
  isPlatformServer: true,
};
