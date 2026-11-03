import { RuntimeServerConfig } from '@sagebionetworks/model-ad/config';

export const configMock: RuntimeServerConfig = {
  appVersion: 'local',
  commitSha: '',
  csrApiUrl: 'http://localhost:4200/v2',
  ssrApiUrl: 'http://model-ad-api-next:3334/v2',
  apiDocsUrl: 'http://localhost:8000/api-docs',
  googleTagManagerEnabled: false,
  googleTagManagerId: '',
  sentryDsn: '',
  sentryEnvironment: '',
  sentryRelease: '',
  sentryTracesSampleRate: null,
  environment: 'dev',
  isPlatformServer: true,
};
