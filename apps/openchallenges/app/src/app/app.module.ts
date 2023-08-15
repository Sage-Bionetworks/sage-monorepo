import { APP_INITIALIZER, NgModule } from '@angular/core';
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
import { KeycloakAngularModule } from 'keycloak-angular';
import { AuthModule } from '@sagebionetworks/openchallenges/auth';
import { AppComponent } from './app.component';
import { MatLegacyProgressSpinnerModule as MatProgressSpinnerModule } from '@angular/material/legacy-progress-spinner';
import { SharedUtilModule } from '@sagebionetworks/shared/util';
import {
  configFactory,
  ConfigService,
} from '@sagebionetworks/openchallenges/config';
import { NavbarComponent } from '@sagebionetworks/openchallenges/ui';

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
    AuthModule.forRoot(),
    MatProgressSpinnerModule,
    SharedUtilModule,
    NavbarComponent,
  ],
  providers: [
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
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
