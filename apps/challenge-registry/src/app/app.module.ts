import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { ApiModule } from '@sagebionetworks/api-angular';
import { AppRoutingModule } from './app-routing.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CountUpModule } from 'ngx-countup';
import { HttpClientModule } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { UtilModule } from '@sagebionetworks/challenge-registry/util';
import { AuthModule } from '@sagebionetworks/challenge-registry/auth';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { AppComponent } from './app.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { SharedUtilModule } from '@sagebionetworks/shared/util';
import {
  configFactory,
  ConfigService,
} from '@sagebionetworks/challenge-registry/config';
import { initializeKeycloakFactory } from './initialize-keycloak.factory';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule.withServerTransition({ appId: 'challenge-registry' }),
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
      deps: [ConfigService, KeycloakService],
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
