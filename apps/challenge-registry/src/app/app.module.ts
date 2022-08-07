import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { ApiModule } from '@sagebionetworks/api-angular';
import { AppRoutingModule } from './app-routing.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CountUpModule } from 'ngx-countup';
import { HttpClientModule } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { KeycloakAngularModule } from 'keycloak-angular';
import { UtilModule } from '@sagebionetworks/challenge-registry/util';
import { AuthModule } from '@sagebionetworks/challenge-registry/auth';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { AppComponent } from './app.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { SharedUtilModule } from '@sagebionetworks/shared/util';

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
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
