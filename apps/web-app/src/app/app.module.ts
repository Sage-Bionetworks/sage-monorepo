import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatSliderModule } from '@angular/material/slider';
import { CountUpModule } from 'ngx-countup';

import { WebDataAccessModule } from '@challenge-registry/web/data-access';
import { WebUiModule } from '@challenge-registry/web/ui';
import {
  ApiModule,
  Configuration,
  // ConfigurationParameters,
  // BASE_PATH
} from '@challenge-registry/api-angular';
import { AppComponent } from './app.component';
import { NxWelcomeComponent } from './nx-welcome.component';
import { AppConfig, APP_CONFIG } from './app.config';
import { TokenService } from './token.service';
import { AppRoutingModule } from './app-routing.module';

@NgModule({
  declarations: [AppComponent, NxWelcomeComponent],
  imports: [
    ApiModule,
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    CountUpModule,
    HttpClientModule,
    MatButtonModule,
    MatSliderModule,
    WebDataAccessModule,
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
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  bootstrap: [AppComponent],
})
export class AppModule {}
