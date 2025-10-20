import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import {
  APP_ID,
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {
  provideRouter,
  UrlSerializer,
  withEnabledBlockingInitialNavigation,
  withInMemoryScrolling,
} from '@angular/router';
import { BASE_PATH as API_CLIENT_BASE_PATH } from '@sagebionetworks/agora/api-client';
import { configFactory, ConfigService } from '@sagebionetworks/agora/config';
import { BASE_PATH as SYNAPSE_API_CLIENT_BASE_PATH } from '@sagebionetworks/synapse/api-client';
import { providePrimeNG } from 'primeng/config';
import { AgoraPreset } from './primeNGPreset';

import {
  httpErrorInterceptor,
  rollbarFactory,
  RollbarService,
} from '@sagebionetworks/agora/services';
import { MessageService } from 'primeng/api';
import { CustomUrlSerializer } from './app.custom-uri-serializer';
import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
import { provideMarkdown } from 'ngx-markdown';
import { provideGoogleTagManager } from 'angular-google-tag-manager';
import { GTM_CONFIG } from '@sagebionetworks/web-shared/angular/analytics/gtm';

export const appConfig: ApplicationConfig = {
  providers: [
    { provide: APP_ID, useValue: 'agora-app' },
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
    providePrimeNG({
      theme: {
        preset: AgoraPreset,
        options: {
          darkModeSelector: false,
        },
      },
    }),
    provideHttpClient(withFetch(), withInterceptors([httpErrorInterceptor])),
    provideClientHydration(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    {
      provide: RollbarService,
      useFactory: rollbarFactory,
    },
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
      withEnabledBlockingInitialNavigation(),
      withInMemoryScrolling({
        scrollPositionRestoration: 'enabled',
      }),
    ),
    { provide: UrlSerializer, useClass: CustomUrlSerializer },
    MessageService,
  ],
};
