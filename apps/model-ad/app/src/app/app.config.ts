import { provideHttpClient, withFetch, withInterceptorsFromDi } from '@angular/common/http';
import {
  APP_ID,
  ApplicationConfig,
  ErrorHandler,
  inject,
  provideAppInitializer,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideClientHydration } from '@angular/platform-browser';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {
  provideRouter,
  UrlSerializer,
  withComponentInputBinding,
  withInMemoryScrolling,
} from '@angular/router';
import { BASE_PATH as API_CLIENT_BASE_PATH } from '@sagebionetworks/model-ad/api-client';
import { configFactory, ConfigService } from '@sagebionetworks/model-ad/config';
import { provideMarkdown } from 'ngx-markdown';
import { MessageService } from 'primeng/api';
import { providePrimeNG } from 'primeng/config';
import { CustomUrlSerializer } from './app.custom-url-serializer';
import { routes } from './app.routes';
import { ModelAdPreset } from './primeNGPreset';
import { GlobalErrorHandler } from '@sagebionetworks/explorers/services';
import { provideGoogleTagManager } from 'angular-google-tag-manager';
import { GTM_CONFIG } from '@sagebionetworks/web-shared/angular/analytics/gtm';

export const appConfig: ApplicationConfig = {
  providers: [
    { provide: APP_ID, useValue: 'model-ad-app' },
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
    provideHttpClient(withFetch(), withInterceptorsFromDi()),
    provideClientHydration(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideMarkdown(),
    {
      provide: GTM_CONFIG,
      useFactory: () => {
        const config = inject(ConfigService);
        return {
          gtmId: config.config.googleTagManagerId,
          isPlatformServer: config.config.isPlatformServer,
        };
      },
    },
    provideGoogleTagManager({
      id: inject(ConfigService).config.googleTagManagerId,
    }),
    provideRouter(
      routes,
      withComponentInputBinding(),
      withInMemoryScrolling({
        scrollPositionRestoration: 'enabled',
      }),
    ),
    { provide: UrlSerializer, useClass: CustomUrlSerializer },
    MessageService,
    { provide: ErrorHandler, useClass: GlobalErrorHandler },
  ],
};
