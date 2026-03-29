import { provideHttpClient, withFetch } from '@angular/common/http';
import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideClientHydration } from '@angular/platform-browser';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideRouter, withComponentInputBinding, withInMemoryScrolling } from '@angular/router';
import { configFactory, ConfigService } from '@sagebionetworks/bixarena/config';
import { BixArenaPreset } from '@sagebionetworks/bixarena/themes';
import { providePrimeNG } from 'primeng/config';
import { appRoutes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideAppInitializer(() => {
      const initializerFn = configFactory(inject(ConfigService));
      return initializerFn();
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
    provideRouter(appRoutes, withComponentInputBinding(), withInMemoryScrolling()),
  ],
};
