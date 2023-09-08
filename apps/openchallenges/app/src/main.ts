import { enableProdMode } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';

import { environment } from './environments/environment';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

function bootstrap() {
  // TODO: Consider getting read of environment files, which have been replaced by config/*.json. It
  // is possible to get the `production` value from the config file (see git history). Reading the
  // config file may however be difficult when rendering on the server side.
  if (environment.production) {
    enableProdMode();
  }

  bootstrapApplication(AppComponent, appConfig).catch((err) =>
    console.error(err)
  );
}

if (document.readyState === 'complete') {
  bootstrap();
} else {
  document.addEventListener('DOMContentLoaded', bootstrap);
}
