import {
  ApplicationConfig,
  provideZoneChangeDetection,
  APP_ID,
  inject,
  provideAppInitializer,
} from '@angular/core';
import {
  provideRouter,
  withEnabledBlockingInitialNavigation,
  withInMemoryScrolling,
} from '@angular/router';
import { appRoutes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
import { configFactory, ConfigService } from '@sagebionetworks/openchallenges/config';
import { BASE_PATH as API_CLIENT_BASE_PATH } from '@sagebionetworks/openchallenges/api-client-angular';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { providePrimeNG } from 'primeng/config';
import Lara from '@primeng/themes/lara';

export const appConfig: ApplicationConfig = {
  providers: [
    { provide: APP_ID, useValue: 'openchallenges-app' },
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
    provideAnimations(),
    // The HTTP client is injected to enable the ConfigService to retrieve the configuration file
    // via HTTP when server-side rendering (SSR) is used.
    providePrimeNG({
      theme: {
        preset: Lara,
        options: {
          darkModeSelector: false,
        },
      },
    }),
    provideHttpClient(withFetch()),
    provideClientHydration(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(
      appRoutes,
      withEnabledBlockingInitialNavigation(),
      withInMemoryScrolling({
        scrollPositionRestoration: 'enabled',
      }),
    ),
  ],
};
