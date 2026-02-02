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
import { GlobalErrorHandler, provideExplorersConfig } from '@sagebionetworks/explorers/services';
import { httpErrorInterceptor } from '@sagebionetworks/explorers/util';
import { BASE_PATH as API_CLIENT_BASE_PATH } from '@sagebionetworks/model-ad/api-client';
import { configFactory, ConfigService } from '@sagebionetworks/model-ad/config';
import { provideMarkdown } from 'ngx-markdown';
import { MessageService } from 'primeng/api';
import { providePrimeNG } from 'primeng/config';
import { CustomUrlSerializer } from './app.custom-url-serializer';
import { routes } from './app.routes';
import { VISUALIZATION_OVERVIEW_PANES } from './content/visualization-overview.content';
import { ModelAdPreset } from './primeNGPreset';

export const appConfig: ApplicationConfig = {
  providers: [
    { provide: APP_ID, useValue: 'model-ad-app' },
    provideAppInitializer(() => {
      const initializerFn = configFactory(inject(ConfigService));
      return initializerFn();
    }),
    provideExplorersConfig({
      visualizationOverviewPanes: VISUALIZATION_OVERVIEW_PANES,
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
    provideHttpClient(withFetch(), withInterceptors([httpErrorInterceptor])),
    provideClientHydration(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideMarkdown(),
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
