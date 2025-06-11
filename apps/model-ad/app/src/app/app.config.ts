import { ApplicationConfig, APP_ID, inject, provideAppInitializer } from '@angular/core';
import {
  provideRouter,
  withComponentInputBinding,
  withEnabledBlockingInitialNavigation,
  withInMemoryScrolling,
} from '@angular/router';
import { withInterceptorsFromDi, provideHttpClient } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';
import { BASE_PATH as API_CLIENT_BASE_PATH } from '@sagebionetworks/model-ad/api-client-angular';
import { configFactory, ConfigService } from '@sagebionetworks/model-ad/config';

import { routes } from './app.routes';
import { ModelAdPreset } from './primeNGPreset';
import { provideMarkdown } from 'ngx-markdown';

// This index is used to remove the corresponding provider in app.config.server.ts.
// TODO: This index could be out of sync if we are not careful. Find a more elegant way.
export const APP_BASE_URL_PROVIDER_INDEX = 1;

export const appConfig: ApplicationConfig = {
  providers: [
    { provide: APP_ID, useValue: 'model-ad-app' },
    {
      // This provider must be specified at the index defined by APP_BASE_URL_PROVIDER_INDEX.
      provide: 'APP_BASE_URL',
      useFactory: () => '.',
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
    providePrimeNG({
      theme: {
        preset: ModelAdPreset,
        options: {
          darkModeSelector: false,
        },
      },
    }),
    provideHttpClient(withInterceptorsFromDi()),
    provideMarkdown(),
    provideRouter(
      routes,
      withComponentInputBinding(),
      withEnabledBlockingInitialNavigation(),
      withInMemoryScrolling({
        scrollPositionRestoration: 'enabled',
      }),
    ),
  ],
};
