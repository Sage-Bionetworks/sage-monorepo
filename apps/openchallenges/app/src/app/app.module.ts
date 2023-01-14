import { APP_INITIALIZER, NgModule, PLATFORM_ID } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import {
  ApiModule,
  BASE_PATH as API_CLIENT_BASE_PATH,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { AppRoutingModule } from './app-routing.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CountUpModule } from 'ngx-countup';
import { HttpClientModule } from '@angular/common/http';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { UtilModule } from '@sagebionetworks/openchallenges/util';
import { AuthModule } from '@sagebionetworks/openchallenges/auth';
import { UiModule } from '@sagebionetworks/openchallenges/ui';
import { AppComponent } from './app.component';
import { MatLegacyProgressSpinnerModule as MatProgressSpinnerModule } from '@angular/material/legacy-progress-spinner';
import { SharedUtilModule } from '@sagebionetworks/shared/util';
import {
  configFactory,
  ConfigService,
} from '@sagebionetworks/openchallenges/config';
import { initializeKeycloakFactory } from './initialize-keycloak.factory';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule.withServerTransition({ appId: 'openchallenges' }),
    ApiModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    CountUpModule,
    HttpClientModule,
    MatButtonModule,
    KeycloakAngularModule,
    UtilModule,
    AuthModule.forRoot(),
    UiModule,
    MatProgressSpinnerModule,
    SharedUtilModule,
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: configFactory,
      deps: [ConfigService],
      multi: true,
    },
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloakFactory,
      multi: true,
      deps: [ConfigService, KeycloakService, PLATFORM_ID],
    },
    {
      provide: API_CLIENT_BASE_PATH,
      useFactory: (configService: ConfigService) => configService.config.apiUrl,
      deps: [ConfigService],
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
