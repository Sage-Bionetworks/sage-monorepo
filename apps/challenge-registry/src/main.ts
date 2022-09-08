import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';

import { environment } from './environments/environment';

function bootstrap() {
  // TODO: Consider getting read of environment files, which have been replaced by config/*.json. It
  // is possible to get the `production` value from the config file (see git history). Reading the
  // config file may however be difficult when rendering on the server side.
  if (environment.production) {
    enableProdMode();
  }

  platformBrowserDynamic([
    {
      provide: 'APP_BASE_URL',
      useFactory: () => `.`,
      deps: [],
    },
  ])
    .bootstrapModule(AppModule)
    .catch((err) => console.error(err));
}

if (document.readyState === 'complete') {
  bootstrap();
} else {
  document.addEventListener('DOMContentLoaded', bootstrap);
}
