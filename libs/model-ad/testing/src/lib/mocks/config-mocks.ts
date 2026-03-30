import { RuntimeAppConfig } from '@sagebionetworks/model-ad/config';

export const configMock: RuntimeAppConfig = {
  appVersion: 'local',
  commitSha: '',
  csrApiUrl: 'http://localhost:4200/v1',
  ssrApiUrl: 'http://model-ad-api:3333/v1',
  apiDocsUrl: 'http://localhost:8000/api-docs',
  googleTagManagerId: '',
  sentryRelease: '',
  environment: 'dev',
  isPlatformServer: false,
};
