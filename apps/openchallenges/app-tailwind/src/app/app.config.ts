import { ApplicationConfig, APP_INITIALIZER, APP_ID } from '@angular/core';
import {
  provideRouter,
  withEnabledBlockingInitialNavigation,
  withInMemoryScrolling,
} from '@angular/router';
import { withInterceptorsFromDi, provideHttpClient } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { BASE_PATH as API_CLIENT_BASE_PATH } from '@sagebionetworks/openchallenges/api-client-angular';
import { configFactory, ConfigService } from '@sagebionetworks/openchallenges/config';

import { routes } from './app.routes';

// This index is used to remove the corresponding provider in app.config.server.ts.
export const APP_BASE_URL_PROVIDER_INDEX = 1;

export const appConfig: ApplicationConfig = {
  providers: [
    { provide: APP_ID, useValue: 'openchallenges-app' },
    {
      // This provider must be specified at the index defined by APP_BASE_URL_PROVIDER_INDEX.
      provide: 'APP_BASE_URL',
      useFactory: () => '.',
      deps: [],
    },
    {
      provide: APP_INITIALIZER,
      useFactory: configFactory,
      deps: [ConfigService],
      multi: true,
    },
    {
      provide: API_CLIENT_BASE_PATH,
      useFactory: (configService: ConfigService) =>
        configService.config.isPlatformServer
          ? configService.config.ssrApiUrl
          : configService.config.csrApiUrl,
      deps: [ConfigService],
    },
    provideAnimations(),
    provideHttpClient(withInterceptorsFromDi()),
    provideRouter(
      routes,
      withEnabledBlockingInitialNavigation(),
      withInMemoryScrolling({
        scrollPositionRestoration: 'enabled',
      }),
    ),
  ],
};
