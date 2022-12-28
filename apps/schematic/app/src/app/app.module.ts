import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { MatTableModule } from '@angular/material/table';

import {
  ApiModule as SchematicApiModule,
  BASE_PATH as SCHEMATIC_API_CLIENT_BASE_PATH,
} from '@sagebionetworks/schematic/api-client-angular';
import { AppComponent } from './app.component';
import { NxWelcomeComponent } from './nx-welcome.component';
import { RouterModule } from '@angular/router';
import { appRoutes } from './app.routes';

@NgModule({
  declarations: [AppComponent, NxWelcomeComponent],
  imports: [
    BrowserModule,
    RouterModule.forRoot(appRoutes, { initialNavigation: 'enabledBlocking' }),
    HttpClientModule,
    MatTableModule,
    SchematicApiModule,
  ],
  providers: [
    {
      provide: SCHEMATIC_API_CLIENT_BASE_PATH,
      useFactory: () => 'http://localhost:7200/api',
      deps: [],
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
