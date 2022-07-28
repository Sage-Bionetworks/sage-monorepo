import { NgModule } from '@angular/core';
import { ServerModule } from '@angular/platform-server';

import { AppModule } from './app.module';
import { AppComponent } from './app.component';
// import { AppServerComponent } from './app.server.component';

@NgModule({
  imports: [AppModule, ServerModule],
  // declarations: [AppServerComponent],
  bootstrap: [AppComponent],
})
export class AppServerModule {}
