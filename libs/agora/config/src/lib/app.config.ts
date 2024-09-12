export interface AppConfig {
  appVersion: string;
  csrApiUrl: string;
  isPlatformServer: boolean;
  ssrApiUrl: string;
  apiDocsUrl: string;
  rollbarToken: string;
}

export const EMPTY_APP_CONFIG = {} as AppConfig;
