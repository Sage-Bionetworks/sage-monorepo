import { ApplicationConfig, mergeApplicationConfig } from '@angular/core';
import { provideServerRendering } from '@angular/ssr';
import { APP_PORT } from '@sagebionetworks/agora/config';
import { appConfig } from './app.config';

const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(),
    // This provider enables the config service to locate the config file during SSR.
    // Originally added to server.ts (used for production with the Express server),
    // it was moved here to ensure availability in both production and development environments.
    {
      provide: APP_PORT,
      useValue: process.env['PORT'] || '4200',
    },
  ],
};

export const config = mergeApplicationConfig(appConfig, serverConfig);
