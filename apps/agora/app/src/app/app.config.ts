import { ApplicationConfig, APP_ID, inject, provideAppInitializer } from '@angular/core';
import {
  provideRouter,
  withEnabledBlockingInitialNavigation,
  withInMemoryScrolling,
} from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { BASE_PATH as API_CLIENT_BASE_PATH } from '@sagebionetworks/agora/api-client-angular';
import { BASE_PATH as SYNAPSE_API_CLIENT_BASE_PATH } from '@sagebionetworks/synapse/api-client-angular';
import { configFactory, ConfigService } from '@sagebionetworks/agora/config';

import { routes } from './app.routes';
import {
  httpErrorInterceptor,
  rollbarFactory,
  RollbarService,
} from '@sagebionetworks/agora/services';
import { MessageService } from 'primeng/api';

// This index is used to remove the corresponding provider in app.config.server.ts.
// TODO: This index could be out of sync if we are not careful. Find a more elegant way.
export const APP_BASE_URL_PROVIDER_INDEX = 1;

export const appConfig: ApplicationConfig = {
  providers: [
    { provide: APP_ID, useValue: 'agora-app' },
    {
      // This provider must be specified at the index defined by APP_BASE_URL_PROVIDER_INDEX.
      provide: 'APP_BASE_URL',
      useFactory: () => '.',
      deps: [],
    },
    {
      provide: SYNAPSE_API_CLIENT_BASE_PATH,
      useFactory: () => 'https://repo-prod.prod.sagebase.org',
      deps: [],
    },
    provideAppInitializer(() => {
      const initializerFn = configFactory(inject(ConfigService));
      return initializerFn();
    }),
    {
      provide: API_CLIENT_BASE_PATH,
      useFactory: (configService: ConfigService) =>
        configService.config.isPlatformServer
          ? configService.config.ssrApiUrl
          : configService.config.csrApiUrl,
      deps: [ConfigService],
    },
    provideAnimationsAsync(),
    provideHttpClient(withInterceptors([httpErrorInterceptor])),
    {
      provide: RollbarService,
      useFactory: rollbarFactory,
    },
    provideRouter(
      routes,
      withEnabledBlockingInitialNavigation(),
      withInMemoryScrolling({
        scrollPositionRestoration: 'enabled',
      }),
    ),
    MessageService,
  ],
};
