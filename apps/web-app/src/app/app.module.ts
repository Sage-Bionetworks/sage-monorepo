import {
  APP_INITIALIZER,
  CUSTOM_ELEMENTS_SCHEMA,
  NgModule,
} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatSliderModule } from '@angular/material/slider';
import { CountUpModule } from 'ngx-countup';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';

import { WebUiModule } from '@challenge-registry/web/ui';
import { ApiModule, Configuration } from '@challenge-registry/api-angular';
import { AppComponent } from './app.component';
import { AppConfig, APP_CONFIG } from '@challenge-registry/web/config';
import { TokenService, WebAuthModule } from '@challenge-registry/web/auth';
import { AppRoutingModule } from './app-routing.module';
import { WebUtilModule } from '@challenge-registry/web/util';
import { initializeKeycloakFactory } from './initialize-keycloak.factory';

@NgModule({
  declarations: [AppComponent],
  imports: [
    ApiModule,
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    CountUpModule,
    HttpClientModule,
    MatButtonModule,
    MatSliderModule,
    KeycloakAngularModule,
    WebUtilModule,
    WebAuthModule.forRoot(),
    WebUiModule,
  ],
  providers: [
    {
      provide: Configuration,
      useFactory: (config: AppConfig, tokenService: TokenService) =>
        new Configuration({
          credentials: {
            BearerAuth: () => tokenService.getToken(),
          },
          basePath: config.apiUrl,
        }),
      deps: [APP_CONFIG, TokenService],
      multi: false,
    },
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloakFactory,
      multi: true,
      deps: [APP_CONFIG, KeycloakService],
    },
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  bootstrap: [AppComponent],
})
export class AppModule {}
