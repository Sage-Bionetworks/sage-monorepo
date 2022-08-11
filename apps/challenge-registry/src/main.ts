import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';

import { environment } from './environments/environment';

function bootstrap() {
  if (environment.production) {
    enableProdMode();
  }

  document.addEventListener('DOMContentLoaded', () => {
    platformBrowserDynamic([
      {
        provide: 'APP_BASE_URL',
        useFactory: () => `.`,
        deps: [],
      },
    ])
      .bootstrapModule(AppModule)
      .catch((err) => console.error(err));
  });
}

if (document.readyState === 'complete') {
  bootstrap();
} else {
  document.addEventListener('DOMContentLoaded', bootstrap);
}
