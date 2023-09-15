import {
  ApplicationConfig,
  APP_INITIALIZER,
  importProvidersFrom,
} from '@angular/core';
import {
  provideRouter,
  withEnabledBlockingInitialNavigation,
  withInMemoryScrolling,
} from '@angular/router';

import { SharedUtilModule } from '@sagebionetworks/shared/util';
import { MatLegacyProgressSpinnerModule as MatProgressSpinnerModule } from '@angular/material/legacy-progress-spinner';
import { KeycloakAngularModule } from 'keycloak-angular';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import {
  withInterceptorsFromDi,
  provideHttpClient,
} from '@angular/common/http';
import { CountUpModule } from 'ngx-countup';
import { provideAnimations } from '@angular/platform-browser/animations';
import { BrowserModule } from '@angular/platform-browser';
import {
  BASE_PATH as API_CLIENT_BASE_PATH,
  ApiModule,
} from '@sagebionetworks/openchallenges/api-client-angular';
import {
  configFactory,
  ConfigService,
} from '@sagebionetworks/openchallenges/config';

import { routes } from './app.routes';

// This index is used to remove the corresponding provider in app.config.server.ts.
export const APP_BASE_URL_PROVIDER_INDEX = 1;

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(
      BrowserModule.withServerTransition({ appId: 'openchallenges' }),
      ApiModule,
      CountUpModule,
      MatButtonModule,
      KeycloakAngularModule,
      // AuthModule.forRoot(),
      MatProgressSpinnerModule,
      SharedUtilModule
    ),
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
    // {
    //   provide: APP_INITIALIZER,
    //   useFactory: initializeKeycloakFactory,
    //   multi: true,
    //   deps: [ConfigService, KeycloakService, PLATFORM_ID],
    // },
    {
      provide: API_CLIENT_BASE_PATH,
      useFactory: (configService: ConfigService) => configService.config.apiUrl,
      deps: [ConfigService],
    },
    { provide: 'googleTagManagerId', useValue: 'GTM-NBR5XD8C' },
    provideAnimations(),
    provideHttpClient(withInterceptorsFromDi()),
    provideRouter(
      routes,
      withEnabledBlockingInitialNavigation(),
      withInMemoryScrolling({
        scrollPositionRestoration: 'top',
      })
    ),
  ],
};
