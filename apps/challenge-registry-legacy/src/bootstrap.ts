import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import {
  AppConfig,
  APP_CONFIG,
  Environment,
} from '@sagebionetworks/challenge-registry/config';

fetch('/config/config.json')
  .then((response) => response.json() as Promise<AppConfig>)
  .then((config: AppConfig) => {
    if (
      [Environment.Production, Environment.Staging].includes(config.environment)
    ) {
      enableProdMode();
    }

    console.log('challenge-registry config', config);

    platformBrowserDynamic([{ provide: APP_CONFIG, useValue: config }])
      .bootstrapModule(AppModule)
      .catch((err) => console.error(err));
  });
