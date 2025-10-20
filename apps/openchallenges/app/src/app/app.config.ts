import {
  ApplicationConfig,
  provideZoneChangeDetection,
  APP_ID,
  inject,
  provideAppInitializer,
} from '@angular/core';
import { provideRouter, withComponentInputBinding, withInMemoryScrolling } from '@angular/router';
import { appRoutes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
import { configFactory, ConfigService } from '@sagebionetworks/openchallenges/web/angular/config';
import { BASE_PATH as API_CLIENT_BASE_PATH } from '@sagebionetworks/openchallenges/api-client';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { providePrimeNG } from 'primeng/config';
import { provideGtmConfig, provideGtmId } from '@sagebionetworks/web-shared/angular/analytics/gtm';
import Lara from '@primeng/themes/lara';
import { telemetryFactory } from './telemetry.factory';

export const appConfig: ApplicationConfig = {
  providers: [
    { provide: APP_ID, useValue: 'openchallenges-app' },
    provideAppInitializer(() => {
      const configService = inject(ConfigService);
      return configFactory(configService)().then(() => telemetryFactory(configService)());
    }),
    {
      provide: API_CLIENT_BASE_PATH,
      useFactory: (configService: ConfigService) =>
        configService.config.isPlatformServer
          ? configService.config.api.ssr.url
          : configService.config.api.csr.url,
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
    provideGtmConfig(
      (config: ConfigService) => ({
        enabled: config.config.google.tagManager.enabled,
        gtmId: config.config.google.tagManager.id,
        isPlatformServer: config.config.isPlatformServer,
      }),
      [ConfigService],
    ),
    provideGtmId(),
    provideRouter(
      appRoutes,
      withComponentInputBinding(),
      withInMemoryScrolling({
        scrollPositionRestoration: 'enabled',
      }),
    ),
  ],
};
