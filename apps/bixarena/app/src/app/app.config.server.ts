import { provideServerRendering } from '@angular/ssr';
import { mergeApplicationConfig, ApplicationConfig } from '@angular/core';
import { CONFIG_BASE_PATH } from '@sagebionetworks/platform/config/angular';
import { appConfig } from './app.config';

const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(),
    { provide: CONFIG_BASE_PATH, useValue: 'apps/bixarena/app/src/config' },
  ],
};

export const config = mergeApplicationConfig(appConfig, serverConfig);
