import { provideHttpClient, withFetch } from '@angular/common/http';
import {
  APP_ID,
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideClientHydration } from '@angular/platform-browser';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideRouter, withComponentInputBinding, withInMemoryScrolling } from '@angular/router';
import { BASE_PATH } from '@sagebionetworks/bixarena/api-client';
import { configFactory, ConfigService } from '@sagebionetworks/bixarena/config';
import { BixArenaPreset } from '@sagebionetworks/bixarena/themes';
import { ThemeService } from '@sagebionetworks/bixarena/services';
import { providePrimeNG } from 'primeng/config';
import { appRoutes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideAppInitializer(() => {
      const initializerFn = configFactory(inject(ConfigService));
      return initializerFn();
    }),
    provideAppInitializer(() => {
      inject(ThemeService).init();
      return;
    }),
    provideAnimationsAsync(),
    providePrimeNG({
      theme: {
        preset: BixArenaPreset,
        options: {
          darkModeSelector: '.dark',
          cssLayer: true,
        },
      },
    }),
    provideHttpClient(withFetch()),
    provideClientHydration(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    { provide: APP_ID, useValue: 'bixarena-app' },
    provideRouter(
      appRoutes,
      withComponentInputBinding(),
      withInMemoryScrolling({ scrollPositionRestoration: 'enabled' }),
    ),
    {
      provide: BASE_PATH,
      useFactory: (configService: ConfigService) =>
        configService.config.isPlatformServer
          ? configService.config.api.ssrBaseUrl
          : configService.config.api.csrBaseUrl,
      deps: [ConfigService],
    },
  ],
};
