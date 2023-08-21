import {
  enableProdMode,
  APP_INITIALIZER,
  importProvidersFrom,
} from '@angular/core';

import { environment } from './environments/environment';
import { AppComponent } from './app/app.component';
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
import { AppRoutingModule } from './app/app-routing.module';
import { BrowserModule, bootstrapApplication } from '@angular/platform-browser';
import {
  BASE_PATH as API_CLIENT_BASE_PATH,
  ApiModule,
} from '@sagebionetworks/openchallenges/api-client-angular';
import {
  configFactory,
  ConfigService,
} from '@sagebionetworks/openchallenges/config';

function bootstrap() {
  // TODO: Consider getting read of environment files, which have been replaced by config/*.json. It
  // is possible to get the `production` value from the config file (see git history). Reading the
  // config file may however be difficult when rendering on the server side.
  if (environment.production) {
    enableProdMode();
  }

  bootstrapApplication(AppComponent, {
    providers: [
      importProvidersFrom(
        BrowserModule.withServerTransition({ appId: 'openchallenges' }),
        ApiModule,
        AppRoutingModule,
        CountUpModule,
        MatButtonModule,
        KeycloakAngularModule,
        // AuthModule.forRoot(),
        MatProgressSpinnerModule,
        SharedUtilModule
      ),
      {
        provide: 'APP_BASE_URL',
        useFactory: () => `.`,
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
        useFactory: (configService: ConfigService) =>
          configService.config.apiUrl,
        deps: [ConfigService],
      },
      { provide: 'googleTagManagerId', useValue: 'GTM-NBR5XD8C' },
      provideAnimations(),
      provideHttpClient(withInterceptorsFromDi()),
    ],
  }).catch((err) => console.error(err));
}

if (document.readyState === 'complete') {
  bootstrap();
} else {
  document.addEventListener('DOMContentLoaded', bootstrap);
}
