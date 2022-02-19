import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { NxWelcomeComponent } from './nx-welcome.component';
import { RouterModule } from '@angular/router';
// import { ApiModule } from '@challenge-registry/api-angular';
import { MatSliderModule } from '@angular/material/slider';

import { WebUiModule } from '@challenge-registry/web/ui';

@NgModule({
  declarations: [AppComponent, NxWelcomeComponent],
  imports: [
    BrowserModule,
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
    WebUiModule,
  ],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  bootstrap: [AppComponent],
})
export class AppModule {}
