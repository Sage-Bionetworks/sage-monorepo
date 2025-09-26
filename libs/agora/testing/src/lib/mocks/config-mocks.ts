import { AppConfig } from '@sagebionetworks/agora/config';

export const configMock: AppConfig = {
  apiDocsUrl: 'http://localhost:8000/api-docs',
  appVersion: 'local',
  csrApiUrl: 'http://localhost:4200/v1',
  ssrApiUrl: 'http://agora-api:3333/v1',
  tagName: 'local',
  isPlatformServer: false,
  googleTagManagerId: '',
};
