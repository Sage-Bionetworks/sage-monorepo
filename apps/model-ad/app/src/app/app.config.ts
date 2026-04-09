import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
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
import { LoggerService, provideExplorersConfig } from '@sagebionetworks/explorers/services';
import { httpErrorInterceptor } from '@sagebionetworks/explorers/util';
import { BASE_PATH as API_CLIENT_BASE_PATH } from '@sagebionetworks/model-ad/api-client';
import { configFactory, ConfigService } from '@sagebionetworks/model-ad/config';
import { LOGGER } from '@sagebionetworks/web-shared/angular/logger';
import { provideGtmConfig, provideGtmId } from '@sagebionetworks/web-shared/angular/analytics/gtm';
import { provideMarkdown } from 'ngx-markdown';
import { MessageService } from 'primeng/api';
import { providePrimeNG } from 'primeng/config';
import { CustomUrlSerializer } from './app.custom-url-serializer';
import { routes } from './app.routes';
import { VISUALIZATION_OVERVIEW_PANES } from './content/visualization-overview.content';
import { ModelAdPreset } from './primeNGPreset';
import * as Sentry from '@sentry/angular';

export const appConfig: ApplicationConfig = {
  providers: [
    { provide: APP_ID, useValue: 'model-ad-app' },
    provideAppInitializer(() => {
      const configService = inject(ConfigService);
      return configFactory(configService)().then(() => {
        const release = configService.config.sentryRelease;
        if (release) {
          Sentry.addEventProcessor((event) => ({ ...event, release }));
        }
      });
    }),
    provideExplorersConfig({
      visualizationOverviewPanes: VISUALIZATION_OVERVIEW_PANES,
    }),
    {
      provide: API_CLIENT_BASE_PATH,
      useFactory: (configService: ConfigService) => {
        const config = configService.config;
        return config.isPlatformServer ? config.ssrApiUrl : config.csrApiUrl;
      },
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
    provideHttpClient(withFetch(), withInterceptors([httpErrorInterceptor])),
    provideClientHydration(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideGtmConfig(
      (configService: ConfigService) => ({
        enabled: configService.config.googleTagManagerEnabled,
        gtmId: configService.config.googleTagManagerId,
        isPlatformServer: configService.config.isPlatformServer,
      }),
      [ConfigService],
    ),
    provideGtmId(),
    provideMarkdown(),
    provideRouter(
      routes,
      withComponentInputBinding(),
      withInMemoryScrolling({
        scrollPositionRestoration: 'enabled',
      }),
    ),
    { provide: UrlSerializer, useClass: CustomUrlSerializer },
    { provide: LOGGER, useExisting: LoggerService },
    MessageService,
    {
      provide: ErrorHandler,
      useValue: Sentry.createErrorHandler(),
    },
    provideAppInitializer(() => {
      inject(Sentry.TraceService);
    }),
  ],
};
