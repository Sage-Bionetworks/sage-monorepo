import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { NxWelcomeComponent } from './nx-welcome.component';
import { RouterModule } from '@angular/router';
// import { ApiModule } from '@challenge-registry/api-angular';
import { MatButtonModule } from '@angular/material/button';
import { MatSliderModule } from '@angular/material/slider';
import { WebDataAccessModule } from '@challenge-registry/web/data-access';
import { WebUiModule } from '@challenge-registry/web/ui';
import {
  ApiModule,
  Configuration,
  // ConfigurationParameters,
  // BASE_PATH
} from '@challenge-registry/api-angular';
import { AppConfig, APP_CONFIG } from './app.config';
import { TokenService } from './token.service';

@NgModule({
  declarations: [AppComponent, NxWelcomeComponent],
  imports: [
    ApiModule,
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatButtonModule,
    MatSliderModule,
    RouterModule.forRoot(
      [
        // {
        //   path: 'login',
        //   loadChildren: () =>
        //     import('login/Module').then((m) => m.RemoteEntryModule),
        // },
      ],
      { initialNavigation: 'enabledBlocking' }
    ),
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
